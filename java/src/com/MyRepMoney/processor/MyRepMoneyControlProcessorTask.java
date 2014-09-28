package com.MyRepMoney.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.MyRepMoney.SourceDataSet;
import com.MyRepMoney.SourceDataSetLoader;
import com.cdymek.util.aws.QueueMessage;
import com.cdymek.util.aws.QueueManager;
import com.cdymek.util.CdymekException;
import com.cdymek.util.PropertiesManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;


/**
 * This class is designed to do one thing - and do it repeatedly.  The class will run a loop, checking at a user configured interval in property jcs.checkinterval, to see if any new data load jobs need to be submitted based on the value in jcs_master_job_list.  If a job needs to be executed, a request will be pushed to an AWS SQS queue.  After each execution the jcs_last_time_check value will be updated with the time of the check.
 * 
 * @author cdymek
 *
 */
public class MyRepMoneyControlProcessorTask extends ProcessorTask implements Runnable {

	private static Logger m_logger = null;
	   
	/**
	 * Constructor that initializes the logger class
	 */
	public MyRepMoneyControlProcessorTask(String name, String type) {
		super(name, type);
		m_logger = LogManager.getLogger(this.getClass());
	}
	
	
	/**
	 * Class method that begins processing
	 */
	public void process() {
		
		//Initialize the app
		m_logger.info("Initializing Job Control Server (JCS) application.");
		SourceDataSetLoader sdl = new SourceDataSetLoader();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dton = new SimpleDateFormat("yyyy-MM-dd");
		QueueManager qm = QueueManager.getInstance();

		//Get the current date / time
		Date currentRunTimestamp = Calendar.getInstance().getTime();
		String sCurrentRunTimestamp = df.format(currentRunTimestamp);
		
		////Default to the full 24 hour period if there is no record in the database
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentRunTimestamp);
		cal.add(Calendar.DATE, -1);
		cal.add(Calendar.SECOND, 1);
		Date lastRunTimestamp = cal.getTime();
		String sLastRunTimestamp = df.format(lastRunTimestamp);
		
		SourceDataSet[] sdss = new SourceDataSet[0];
		try {
			
			//Get the last run date / time; use it if the difference between the current timestamp and the last stored timestamp is less than 1 day
			Date jcsLastRunTimestamp = sdl.getLastRunTimestamp();
			if (jcsLastRunTimestamp != null && Days.daysBetween(new DateTime(currentRunTimestamp), new DateTime(jcsLastRunTimestamp)).getDays() < 1) {
				lastRunTimestamp = jcsLastRunTimestamp;
				sLastRunTimestamp = df.format(lastRunTimestamp);
			}
						
			//Get all the entries that need to be processed
			m_logger.debug("Retrieving all records between dates specified|" + sLastRunTimestamp + "|" + sCurrentRunTimestamp);
			sdss = sdl.findByLastRunTimestamp(lastRunTimestamp, currentRunTimestamp, true);
			
			//Push them in to the queue
			for (int i = 0; i < sdss.length; i++) {
				// Send a new message to check the threshold
				QueueMessage qOutMsg = new QueueMessage();
				qOutMsg.setMessage(sdss[i].toString());
				qm.sendMessage(qOutMsg);
				m_logger.debug("Sent Message|" + qOutMsg.toString());
			}
			
			//Update the last run date / timestamp
			sdl.updateLastRunTimestamp(currentRunTimestamp);				
			
		} catch (CdymekException e) {
			m_logger.error("Error retrieving records scheduled to run between|" + sLastRunTimestamp + "|" + sCurrentRunTimestamp, e);
		}
		m_logger.info("Processing complete for " + sdss.length + " entries.");
	}
	
}
