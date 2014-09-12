package com.MyRepMoney;

import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cdymek.util.CdymekException;
import com.cdymek.util.PropertiesManager;

public class SourceDataSet {

	/**
	 * An object which represents the table represented below.
	 * 
	 * CREATE TABLE master_source_download (
	 * SOURCE_ENTITY VARCHAR(25) NOT NULL,
	 * SOURCE_URL VARCHAR(500) NOT NULL,
	 * TARGET_TABLE VARCHAR(500) NOT NULL,
	 * SOURCE_ACTIVE TINYINT(1) NOT NULL DEFAULT 0,
	 * DATE_ADDED TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 * election_cycle VARCHAR(10) NOT NULL,
	 * ADDED_BY VARCHAR(25)
	 * );
	 */
	
	private String m_sSourceEntity;
	private String m_sSourceUrl;
	private String m_sTargetTable;
	private String m_sSourceFileType;
	private int m_iSourceActive;
	private String m_sDateAdded;
	private String m_sElectionCycle;
	private String m_sAddedBy;
	private String m_sDataLoaderClass;
	private String m_sScheduledLoadTime;
	private String m_sDelimiter;
	public static String TOSTRING_DELIMITER = "^";
	private static Logger m_logger = null;
	
	/**
	 * Empty constructor
	 */
	public SourceDataSet() {
		m_logger = LogManager.getLogger(this.getClass());
		try {
			TOSTRING_DELIMITER = PropertiesManager.instance().getProperty("SourceDataSet.delimiter");
		} catch (CdymekException e) {
			m_logger.warn("Could not get TOSTRING_DELIMITER value; defaulting to '^'",e);
		}
	}
	
	/**
	 * Constructor using a specialized formatted string containing the key=value pair relationships
	 * @param values
	 */
	public SourceDataSet(String values) {
		parseString(values);
	}
	/**
	 * Stores the Source Entity value
	 * @param sourceEntity
	 */
	public void setSourceEntity(String sourceEntity) {
		m_sSourceEntity = sourceEntity;
	}
	/**
	 * Returns the Source Entity value
	 * @return
	 */
	public String getSourceEntity() {
		return m_sSourceEntity;
	}
	
	/**
	 * Stores the Source File Type value
	 * @param sourceFileType
	 */
	public void setSourceFileType(String sourceFileType) {
		m_sSourceFileType = sourceFileType;
	}
	/**
	 * Returns the Source File Type value
	 * @return
	 */
	public String getSourceFileType() {
		return m_sSourceFileType;
	}
	
	/**
	 * Stores the Source Url value
	 * @param sourceUrl
	 */
	public void setSourceUrl(String sourceUrl) {
		m_sSourceUrl = sourceUrl;
	}
	/**
	 * Returns the Source Url value
	 * @return
	 */
	public String getSourceUrl() {
		return m_sSourceUrl;
	}
	
	/**
	 * Stores the Active value
	 * @param SourceActive
	 */
	public void setSourceActive(int SourceActive) {
		m_iSourceActive = SourceActive;
	}
	/**
	 * Returns the Active value
	 * @return
	 */
	public int getSourceActive() {
		return m_iSourceActive;
	}
	
	/**
	 * Stores the Target Table value
	 * @param targetTable
	 */
	public void setTargetTable(String targetTable) {
		m_sTargetTable = targetTable;
	}
	/**
	 * Returns the Target Table value
	 * @return
	 */
	public String getTargetTable() {
		return m_sTargetTable;
	}
	
	/**
	 * Stores the Date Added value
	 * @param DateAdded
	 */
	public void setDateAdded(String DateAdded) {
		m_sDateAdded = DateAdded;
	}
	/**
	 * Returns the Date Added value
	 * @return
	 */
	public String getDateAdded() {
		return m_sDateAdded;
	}
	
	
	/**
	 * Stores the Election Cycle value
	 * @param ElectionCycle
	 */
	public void setElectionCycle(String ElectionCycle) {
		m_sElectionCycle = ElectionCycle;
	}
	/**
	 * Returns the Election Cycle value
	 * @return
	 */
	public String getElectionCycle() {
		return m_sElectionCycle;
	}
	
	/**
	 * Stores the Added By value
	 * @param AddedBy
	 */
	public void setAddedBy(String AddedBy) {
		m_sAddedBy = AddedBy;
	}
	/**
	 * Returns the Added By value
	 * @return
	 */
	public String getAddedBy() {
		return m_sAddedBy;
	}

	
	/**
	 * Stores the Data Loader Class value
	 * @param dataLoaderClass
	 */
	public void setDataLoaderClass(String dataLoaderClass) {
		m_sDataLoaderClass = dataLoaderClass;
	}
	/**
	 * Returns the Data Loader Class value
	 * @return
	 */
	public String getDataLoaderClass() {
		return m_sDataLoaderClass;
	}
	
	/**
	 * Stores the Scheduled Load Time value
	 * @param scheduledLoadTime
	 */
	public void setScheduledLoadTime(String scheduledLoadTime) {
		m_sScheduledLoadTime = scheduledLoadTime;
	}
	/**
	 * Returns the Data Loader Class value
	 * @return
	 */
	public String getScheduledLoadTime() {
		return m_sScheduledLoadTime;
	}
	
	/**
	 * Method to set the delimiter associated with the data set
	 * @param delimiter
	 */
	public void setDelimiter(String delimiter) {
		m_sDelimiter = delimiter;
	}
	
	/**
	 * Method to return the delimiter associated with the data set
	 * @return
	 */
	public String getDelimiter() {
		return m_sDelimiter;
	}
	
	/**
	 * Override the toString method with a formatted result of the values contained in this class
	 */
	public String toString() {
		return "AddedBy=" + this.getAddedBy() 
				+ TOSTRING_DELIMITER + "DataLoaderClass=" + this.getDataLoaderClass() 
				+ TOSTRING_DELIMITER + "DateAdded=" + this.getDateAdded() 
				+ TOSTRING_DELIMITER + "ElectionCycle=" + this.getElectionCycle() 
				+ TOSTRING_DELIMITER + "ScheduledLoadTime=" + this.getScheduledLoadTime() 
				+ TOSTRING_DELIMITER + "SourceActive=" + this.getSourceActive() 
				+ TOSTRING_DELIMITER + "SourceEntity=" + this.getSourceEntity() 
				+ TOSTRING_DELIMITER + "SourceFileType=" + this.getSourceFileType() 
				+ TOSTRING_DELIMITER + "SourceUrl=" + this.getSourceUrl() 
				+ TOSTRING_DELIMITER + "TargetTable=" + this.getTargetTable()
				+ TOSTRING_DELIMITER + "Delimiter=" + this.getDelimiter();
	}
	
	/**
	 * Method which parses a string containing the values delimited by a '|'
	 * @param values
	 */
	public void parseString(String values) {
		StringTokenizer st = new StringTokenizer(values, TOSTRING_DELIMITER);
		Properties props = new Properties();

		while (st.hasMoreTokens()) {
			String sKeyPair[] = ((String) st.nextToken()).split("=");
			props.put(sKeyPair[0], sKeyPair[1]);
		}

		setAddedBy((String)props.get("AddedBy"));
		setDataLoaderClass((String)props.get("DataLoaderClass"));
		setDateAdded((String)props.get("DateAdded"));
		setElectionCycle((String)props.get("ElectionCycle"));
		setScheduledLoadTime((String)props.get("ScheduledLoadTime"));
		setSourceActive(new Integer((String)props.get("SourceActive")).intValue());
		setSourceEntity((String)props.get("SourceEntity"));
		setSourceFileType((String)props.get("SourceFileType"));
		setSourceUrl((String)props.get("SourceUrl"));
		setTargetTable((String)props.get("TargetTable"));
		setDelimiter((String)props.getProperty("Delimiter"));
	}
}
