package com.MyRepMoney.dataloader;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MyRepMoney.SourceDataSet;
import com.MyRepMoney.util.*;
import com.cdymek.util.CdymekException;
import com.cdymek.util.PropertiesManager;
import com.cdymek.util.UrlDownloadHelper;
import com.cdymek.util.ZipArchiveHandler;
import com.cdymek.util.sql.ConnectionManager;

/**
 * This class is an extension of data loader designed to download the data from the FEC.GOV website
 * @author cdymek
 *
 */
public class FECSummaryDataLoader extends DataLoader {
	
	
	private static Logger m_logger = null;
	
	/**
	 * Constructor that initializes the logger class
	 */
	public FECSummaryDataLoader() {
		super();
		m_logger = LogManager.getLogger(this.getClass());
		m_logger.debug("Initializing FECSummaryDataLoader");
	}
	
	/**
	 * Method takes each entry in master_source_download, looks for the corresponding data set at the url specified, and attempts to load it in to the target table.
	 */
	public void process(SourceDataSet sourceDataSet) throws MyRepMoneyException {
		
		//Get the working directory from the properties manager
		m_logger.info("Processing Source Data Set|" + sourceDataSet.toString());
		try {
	
			String workingDir = PropertiesManager.instance().getProperty("dataloader.workingdir");
			
			//If the source is supposed to be sql, run the steps below
			if (sourceDataSet.getSourceFileType() != null && sourceDataSet.getSourceFileType().equals("sql")) {
				loadDataToTable(sourceDataSet.getTargetTable(), sourceDataSet.getElectionCycle());
			}
			//If the source file is not supposed to be a zip file, just try loading what was downloaded
			else {
				m_logger.error("Failed to process SourceDataSet|Unhandled Source Data Set Type|" + sourceDataSet.toString());
			}
		}
		catch (CdymekException e) {
			m_logger.error("Exception Processing Source Data Set|" + sourceDataSet.toString(), e);
			throw new MyRepMoneyException("Exception Processing Source Data Set|" + sourceDataSet.toString(), e);
		}		
		m_logger.info("Source Data Set Processed|" + sourceDataSet.toString());
				
	}
	

	/**
	 * Method that loads a local file in to a target database table
	 * @param localFile
	 * @param targetTable
	 */
	private void loadDataToTable(String targetTable,String electionCycle) throws MyRepMoneyException {
		
		Connection conn = null;
		String sql = null;
		m_logger.debug("Generation Zip Code totals|" + targetTable + "|" + electionCycle);
		
		//Get a random number to use 
		int rand = ThreadLocalRandom.current().nextInt(0,100000);
		try {	
			//Get a connection object
			conn = ConnectionManager.getInstance().getConnection();	
			conn.setAutoCommit(false);
			
			//Create the needed temp tables
			sql = "CREATE TABLE tmp_fec_zip_summaries_comm_" + rand + " ("
					+ "ELECTION_CYCLE VARCHAR(10), "
					+ "ZIP_CODE VARCHAR(5), "
					+ "AMT DECIMAL(14,2) "
					+ ");";
			executeUpdate(conn,sql);
			sql = "CREATE TABLE tmp_fec_zip_summaries_ind_" + rand + " ("
					+ "ELECTION_CYCLE VARCHAR(10), "
					+ "ZIP_CODE VARCHAR(5), "
					+ "AMT DECIMAL(14,2) "
					+ ");";
			executeUpdate(conn,sql);
			sql = "CREATE TABLE tmp_fec_zip_summaries_total_" + rand + " ("
					+ "ELECTION_CYCLE VARCHAR(10), "
					+ "ZIP_CODE VARCHAR(5), "
					+ "AMT DECIMAL(14,2) "
					+ ");";
			executeUpdate(conn,sql);
			
			//Get the three data sets
			sql = "INSERT INTO tmp_fec_zip_summaries_comm_" + rand + " (ELECTION_CYCLE, ZIP_CODE, AMT) "
					+ "SELECT ELECTION_CYCLE, LEFT(ZIP_CODE, 5), SUM(TRANSACTION_AMT) "
					+ "FROM stg_fec_candidate_contrib_comm "
					+ "WHERE stg_fec_candidate_contrib_comm.ELECTION_CYCLE = '" + electionCycle + "' "
					+ "GROUP BY election_cycle, LEFT(zip_code, 5);";	
			executeUpdate(conn,sql);
			sql = "INSERT INTO tmp_fec_zip_summaries_ind_" + rand + " (ELECTION_CYCLE, ZIP_CODE, AMT)  "
					+ "SELECT ELECTION_CYCLE, LEFT(ZIP_CODE, 5), SUM(TRANSACTION_AMT)  "
					+ "FROM stg_fec_ind_contrib_file  "
					+ "WHERE stg_fec_ind_contrib_file.ELECTION_CYCLE = '" + electionCycle + "' "
					+ "GROUP BY election_cycle, LEFT(zip_code, 5) ;";
			executeUpdate(conn,sql);
			sql = "INSERT INTO tmp_fec_zip_summaries_total_" + rand + " (ELECTION_CYCLE, ZIP_CODE, AMT) "
					+ "SELECT election_cycle, zip_code, sum(AMT) "
					+ "FROM ( "
					+ "select election_cycle, zip_code, AMT FROM tmp_fec_zip_summaries_comm_" + rand + " "
					+ "	UNION "
					+ "	select election_cycle, zip_code, AMT FROM tmp_fec_zip_summaries_ind_" + rand + " "
					+ "	) AS tmp "
					+ "GROUP BY election_cycle, zip_code ;";
			executeUpdate(conn,sql);
			
			//Clean out the old data
			sql = "DELETE FROM " + targetTable + " WHERE ELECTION_CYCLE = '" + electionCycle + "'";
			executeUpdate(conn,sql);
			
			//Insert the new data
			sql = "INSERT INTO fec_zip_summaries_full (ELECTION_CYCLE, ZIP_CODE, COMM_AMT, IND_AMT, TOTAL_AMT) "
					+ "SELECT tmp_fec_zip_summaries_total_" + rand + ".election_cycle, tmp_fec_zip_summaries_total_" + rand + ".zip_code,  "
					+ "COALESCE(tmp_fec_zip_summaries_comm_" + rand + ".amt, 0) as comm_amt, COALESCE(tmp_fec_zip_summaries_ind_" + rand + ".amt, 0) as IND_AMT,  "
					+ "COALESCE(tmp_fec_zip_summaries_total_" + rand + ".amt, 0) as TOTAL_AMT "
					+ "FROM tmp_fec_zip_summaries_total_" + rand + " LEFT JOIN tmp_fec_zip_summaries_ind_" + rand + "  "
					+ "ON (tmp_fec_zip_summaries_total_" + rand + ".election_cycle = tmp_fec_zip_summaries_ind_" + rand + ".election_cycle  "
					+ "AND tmp_fec_zip_summaries_total_" + rand + ".zip_code = tmp_fec_zip_summaries_ind_" + rand + ".zip_code) "
					+ "LEFT JOIN tmp_fec_zip_summaries_comm_" + rand + " ON "
					+ "(tmp_fec_zip_summaries_total_" + rand + ".election_cycle = tmp_fec_zip_summaries_comm_" + rand + ".election_cycle  "
					+ "AND tmp_fec_zip_summaries_total_" + rand + ".zip_code = tmp_fec_zip_summaries_comm_" + rand + ".zip_code)";
			executeUpdate(conn,sql);
			
			//Drop the temp tables
			sql = "drop table tmp_fec_zip_summaries_comm_" + rand + ";";
			executeUpdate(conn,sql);			
			sql = "drop table tmp_fec_zip_summaries_ind_" + rand + ";";
			executeUpdate(conn,sql);			
			sql = "drop table tmp_fec_zip_summaries_total_" + rand + ";";
			executeUpdate(conn,sql);			
			
			//Commit and finish
			conn.commit();
			m_logger.debug("Zip Code results from Individual & Committee contributions combined|" + targetTable + "|" + electionCycle);
		
		} catch (SQLException e) {
			m_logger.error("Failed to execute transaction due to SQL Exception|" + sql, e);
			throw new MyRepMoneyException("Failed to execute transaction due to SQL Exception|" + sql, e);
		} catch (CdymekException e) {
			m_logger.error("Failed to execute|" + sql, e);
			throw new MyRepMoneyException("Failed to execute|" + sql, e);
		}
		finally {

			try {
				if (conn != null) {
					conn.close();
				}
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
		}
	}

	
	/**
	 * Method to test the FECDataLoader class
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Creating dummy SourceDataSet object");
		SourceDataSet sds = new SourceDataSet();
		sds.setAddedBy("dymekc");
		sds.setDateAdded("2014-08-25 16:42:22");
		sds.setElectionCycle("2013-2014");
		sds.setSourceActive(1);
		sds.setSourceEntity("FEC");
		sds.setSourceFileType("zip");
		sds.setSourceUrl("ftp://ftp.fec.gov/FEC/2014/pas214.zip");
		sds.setTargetTable("stg_fec_candidate_contrib_comm");
		sds.setDelimiter("|");
		
		System.out.println("Original|" + sds.toString());
		System.out.println("New|" + 
				new SourceDataSet(sds.toString()).toString());
		
		System.out.println("Running FECSummaryDataLoader process...");
		FECSummaryDataLoader fecDL = new FECSummaryDataLoader();
		try {
			fecDL.process(sds);
		} catch (MyRepMoneyException e) {
			System.err.println("Failed to download and load file");
			e.printStackTrace();
		}
		System.out.println("Processing complete, terminating program");	
		
	}

}
