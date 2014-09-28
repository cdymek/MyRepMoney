package com.MyRepMoney;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cdymek.util.CdymekException;
import com.cdymek.util.sql.ConnectionManager;

public class SourceDataSetLoader {

	/**
	 * This class uses a database connection to load the records from the master_source_download table.
	 * It can also save a record or set of records to the table.
	 */
	private static Logger m_logger = null;
	
	/**
	 * Constructor that initializes the logger class
	 */
	public SourceDataSetLoader() {
		m_logger = LogManager.getLogger(this.getClass());
	}
	
	/**
	 * Method returns all the records in the mater_source_download table with an option to filter those that are inactive.
	 * @return
	 */
	public SourceDataSet[] load(boolean activeOnly) throws CdymekException {
		String sql = "SELECT SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, "
					+ "DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, DELIMITER FROM master_source_download";
		if (activeOnly) {
			sql += " WHERE SOURCE_ACTIVE = 1";
		}
		return execute(sql);

	}
	
	/**
	 * Method returns all those records in the master_source_download table that contain the url specified.
	 * @param url
	 * @param activeOnly
	 * @return
	 */
	public SourceDataSet[] findByUrl(String url, boolean activeOnly) throws CdymekException {
		String sql = "SELECT SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, "
				+ "DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, DELIMITER FROM master_source_download";
		sql = sql + " WHERE SOURCE_URL = '" + url.toUpperCase() + "'";
		if (activeOnly) {
			sql += " AND SOURCE_ACTIVE = 1";
		}
		return execute(sql);
	}
	
	/**
	 * Method returns all the records for a specific source of data
	 * @param source
	 * @param activeOnly
	 * @return
	 */
	public SourceDataSet[] findBySource(String source, boolean activeOnly) throws CdymekException  {
		String sql = "SELECT SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, "
				+ "DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, DELIMITER FROM master_source_download";
		sql = sql + " WHERE SOURCE_ENTITY = '" + source.toUpperCase() + "'";
		if (activeOnly) {
			sql += " AND SOURCE_ACTIVE = 1";
		}
		return execute(sql);		
	}
	
	/**
	 * Method returns all records that use the specified DataLoader class name
	 * @param className
	 * @param activeOnly
	 * @return
	 */
	public SourceDataSet[] findByDataLoaderClass(String className, boolean activeOnly) throws CdymekException  {
		String sql = "SELECT SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, "
				+ "DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, DELIMITER FROM master_source_download";
		sql = sql + " WHERE DATA_LOADER_CLASS = '" + className.toUpperCase() + "'";
		if (activeOnly) {
			sql += " AND SOURCE_ACTIVE = 1";
		}
		return execute(sql);		
	}
	
	/**
	 * Method which finds any entries based on being between the values in lastRunTimestmap and currentRunTimestamp
	 * @param lastTimestamp
	 * @param activeOnly
	 * @return
	 */
	public SourceDataSet[] findByLastRunTimestamp(Date lastRunTimestamp, Date currentRunTimestamp, boolean activeOnly)  throws CdymekException {
		
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		String sLastRunTimestamp = df.format(lastRunTimestamp);  
		String sCurrentRunTimestamp = df.format(currentRunTimestamp);
		Date lastRunTime;
		try {
			lastRunTime = df.parse(sLastRunTimestamp);
		} catch (ParseException e) {
			m_logger.error("Exception parsing date|" + sLastRunTimestamp);
			throw new CdymekException("Exception parsing date|" + sLastRunTimestamp, e);
		}
		Date currentRunTime;
		try {
			currentRunTime = df.parse(sCurrentRunTimestamp);
		} catch (ParseException e) {
			m_logger.error("Exception parsing date|" + sCurrentRunTimestamp);
			throw new CdymekException("Exception parsing date|" + sCurrentRunTimestamp, e);
		}
		
		String sql = "SELECT SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, "
				+ "DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, DELIMITER FROM master_source_download";
		
		//Check to see if we changed days
		if (lastRunTime.after(currentRunTime)) {
			sql = sql + " WHERE (SCHEDULED_LOAD_TIME  BETWEEN '" + sLastRunTimestamp + "' AND '23:59:59' OR "
					+ " SCHEDULED_LOAD_TIME BETWEEN '00:00:00' AND '" + sCurrentRunTimestamp + "')";
		}
		else {
			sql = sql + " WHERE SCHEDULED_LOAD_TIME  BETWEEN '" + sLastRunTimestamp + "' AND '" + sCurrentRunTimestamp + "'";
		}
		if (activeOnly) {
			sql += " AND SOURCE_ACTIVE = 1";
		}
		return execute(sql);
	}
	/**
	 * Method that upserts the records in the ArrayList in to the database
	 * @param newRecords
	 */
	public void save(ArrayList recordSet) {
		//TODO Implement method		
	}
	
	/**
	 * Helper method which executes the pre-built SQL statement provided.
	 * @param sql
	 * @return
	 */
	private SourceDataSet[] execute(String sql) throws CdymekException {
		SourceDataSet sdss[] = new SourceDataSet[0];
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = ConnectionManager.getInstance().getConnection();

			m_logger.debug("Executing SQL Statement|" + sql);
			pstmt = conn.prepareStatement(sql);			
			ResultSet rs = pstmt.executeQuery();			
			ArrayList al = formatResults(rs);		
			sdss = new SourceDataSet[al.size()];
			sdss = (SourceDataSet[])al.toArray(sdss);
		}
		catch (CdymekException e) {
			m_logger.error("Failed to get connection", e);
			throw e;
		} catch (SQLException e) {
			m_logger.error("A sql error occurred|" + sql, e);
			throw new CdymekException("A sql error occurred|" + sql, e);
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
			try {
				if (conn != null) {
					conn.close();
				}
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
		}
		return sdss;
	}
	
	/**
	 * Helper method which translates a sql ResultSet in to an array of SourceDataSet objects
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	private ArrayList formatResults(ResultSet rs) throws SQLException {
		ArrayList al = new ArrayList();
		
		while (rs.next()) {
			SourceDataSet sds = new SourceDataSet();
			sds.setAddedBy(rs.getString("ADDED_BY"));
			String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("DATE_ADDED"));
			sds.setDateAdded(s);
			sds.setElectionCycle(rs.getString("ELECTION_CYCLE"));
			sds.setSourceActive(rs.getInt("SOURCE_ACTIVE"));
			sds.setSourceEntity(rs.getString("SOURCE_ENTITY"));
			sds.setSourceFileType(rs.getString("SOURCE_FILE_TYPE"));
			sds.setSourceUrl(rs.getString("SOURCE_URL"));
			sds.setDataLoaderClass(rs.getString("DATA_LOADER_CLASS"));
			sds.setScheduledLoadTime(rs.getString("SCHEDULED_LOAD_TIME"));
			sds.setTargetTable(rs.getString("TARGET_TABLE"));
			sds.setDelimiter(rs.getString("DELIMITER"));
			m_logger.debug("Adding Record|" + sds.toString());
			al.add(sds);
		}
		return al;
	}
	
	public void updateLastRunTimestamp(Date lastRunTimestamp) throws CdymekException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			sql = "INSERT INTO JCS_Control (Run_Timestamp)  VALUES ('" + df.format(lastRunTimestamp) + "')";
			m_logger.debug("Executing SQL Statement|" + sql);
			pstmt = conn.prepareStatement(sql);			
			pstmt.executeUpdate(sql);	
		}
		catch (CdymekException e) {
			m_logger.error("Failed to get connection", e);
			throw e;
		} catch (SQLException e) {
			m_logger.error("A sql error occurred|" + sql, e);
			throw new CdymekException("A sql error occurred|" + sql, e);
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
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
	 * Method which queries the database to get the timestamp from when the JCS program last run
	 * @return
	 */
	public Date getLastRunTimestamp() throws CdymekException{
		Date lastRunTimestamp = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			sql = "SELECT MAX(Run_Timestamp) AS Last_Run_Timestamp from JCS_Control";
			m_logger.debug("Executing SQL Statement|" + sql);
			pstmt = conn.prepareStatement(sql);			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				lastRunTimestamp = rs.getTimestamp("Last_Run_Timestamp");		
		}
		catch (CdymekException e) {
			m_logger.error("Failed to get connection", e);
			throw e;
		} catch (SQLException e) {
			m_logger.error("A sql error occurred|" + sql, e);
			throw new CdymekException("A sql error occurred|" + sql, e);
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
			try {
				if (conn != null) {
					conn.close();
				}
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
		}
		return lastRunTimestamp;
	}
	
	/**
	 * Main method for testing purposes only
	 * @param args
	 */
	public static void main (String[] args) {
		System.out.println("Try querying the master_source_download table");
		SourceDataSetLoader sdsl = new SourceDataSetLoader();
		
		String sLastRunTimestamp = "2014-09-02 00:00:00";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date lastRunTime = null;
		try {
			lastRunTime = df.parse(sLastRunTimestamp);
		} catch (ParseException e) {
			System.err.println("Exception parsing date|" + sLastRunTimestamp);
			e.printStackTrace();
		}
		
		try {
			SourceDataSet[] sdss = sdsl.findByLastRunTimestamp(lastRunTime, Calendar.getInstance().getTime(), false);
			for (int i = 0; i < sdss.length; i++) {
				System.out.println(sdss[i].toString());
			}
		} catch (CdymekException e) {
			e.printStackTrace();
		}
		System.out.println("Test complete");
	}
}
