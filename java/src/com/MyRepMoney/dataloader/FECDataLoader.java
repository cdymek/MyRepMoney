package com.MyRepMoney.dataloader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MyRepMoney.SourceDataSet;
import com.MyRepMoney.util.*;
import com.cdymek.util.CdymekException;
import com.cdymek.util.PropertiesManager;
import com.cdymek.util.UrlDownloadHelper;
import com.cdymek.util.ZipArchiveHandler;

/**
 * This class is an extension of data loader designed to download the data from the FEC.GOV website
 * @author cdymek
 *
 */
public class FECDataLoader extends DataLoader {
	
	
	private static Logger m_logger = null;
	
	/**
	 * Constructor that initializes the logger class
	 */
	public FECDataLoader() {
		m_logger = LogManager.getLogger(this.getClass());
	}
	
	/**
	 * Method takes each entry in master_source_download, looks for the corresponding data set at the url specified, and attempts to load it in to the target table.
	 */
	public void process(SourceDataSet sourceDataSet) throws MyRepMoneyException {
		
		//Get the working directory from the properties manager
		m_logger.info("Processing " + sourceDataSet.toString() + ".");
		try {
	
			String workingDir = PropertiesManager.instance().getProperty("dataloader.workingdir");
			
			//Iterate over the SourceDataSet provided
			String localFile = downloadFile(sourceDataSet);
			m_logger.debug("Processing file|" + localFile);
			//If the source is supposed to be a zip file, extract the files and load each one
			if (sourceDataSet.getSourceFileType() != null && sourceDataSet.getSourceFileType().equals("zip")) {
				String[] extractedFiles = ZipArchiveHandler.instance().extractFiles(localFile, workingDir, null);
				for (int j = 0; j < extractedFiles.length; j++) {
					m_logger.debug("Loading file|" + extractedFiles[j]);
					loadFiletoTable(extractedFiles[j], sourceDataSet.getTargetTable(), sourceDataSet.getDelimiter());
				}
			}
			//If the source file is not supposed to be a zip file, just try loading what was downloaded
			else {
				loadFiletoTable(localFile, sourceDataSet.getTargetTable(), sourceDataSet.getDelimiter());
			}
			m_logger.info("File processed|" + localFile);
		}
		catch (CdymekException e) {
			throw new MyRepMoneyException("Exception in process SourceDataSet|" + sourceDataSet.toString(), e);
		}		
				
	}
	
	/**
	 * Method that downloads a file from the specified url to a local file
	 * @param sourceDataSet
	 * @return
	 */
	private String downloadFile(SourceDataSet sourceDataSet) throws MyRepMoneyException {
		try {			
			UrlDownloadHelper udh = UrlDownloadHelper.getInstance();
			String localDirectory = PropertiesManager.instance().getProperty("dataloader.localdir");
			String localFileName = sourceDataSet.getTargetTable() + sourceDataSet.getElectionCycle() + "." + sourceDataSet.getSourceFileType();
			m_logger.debug("Using UrlDataHandler to download data|" + localFileName);
			String file = udh.downloadFile(sourceDataSet.getSourceUrl(), localDirectory, localFileName);	
			m_logger.debug("Downloaded file|" + localFileName);
			return file;
		}
		catch (CdymekException e) {
			throw new MyRepMoneyException("Exception in process SourceDataSet|" + sourceDataSet.toString(), e);
		}		
	}
	
	/**
	 * Method that loads a local file in to a target database table
	 * @param localFile
	 * @param targetTable
	 */
	private void loadFiletoTable(String localFile, String targetTable, String delimiter) throws MyRepMoneyException {
		// TODO Implement SQL query to load the file
		String sql = "DELETE FROM " + targetTable;
		executeUpdate(sql);
		sql = "LOAD DATA LOCAL INFILE '" + localFile + "' INTO TABLE " + targetTable + " FIELDS TERMINATED BY '" + delimiter 
				+ "' ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\\n' STARTING BY ''";
		executeUpdate(sql);
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
		
		System.out.println("Running FECDataLoader process...");
		FECDataLoader fecDL = new FECDataLoader();
		try {
			fecDL.process(sds);
		} catch (MyRepMoneyException e) {
			System.err.println("Failed to download and load file");
			e.printStackTrace();
		}
		System.out.println("Processing complete, terminating program");	
		
	}

}
