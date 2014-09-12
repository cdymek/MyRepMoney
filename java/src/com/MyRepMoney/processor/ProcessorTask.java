package com.MyRepMoney.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.MyRepMoney.SourceDataSet;
import com.MyRepMoney.SourceDataSetLoader;
import com.MyRepMoney.util.MyRepMoneyException;
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
public abstract class ProcessorTask implements Runnable {

	private static Logger m_logger = null;
		private long sleepTime;
		private String threadName;
		private String sType;
		private volatile boolean running = true;
		   
		/**
		 * Constructor that initializes the logger class
		 */
		public ProcessorTask(String name, String type) {
			threadName = name;
			sType = type;
			m_logger = LogManager.getLogger(this.getClass());
			try {
				sleepTime = Long.parseLong(PropertiesManager.instance().getProperty(sType + ".sleeptime")) * 1000;
			} catch (NumberFormatException | CdymekException e1) {
				m_logger.error("Failed to get property " + sType + ".sleeptime.  Aborting process.");
				return;
			}
		}
		
		
		/**
		 * Method to start the thread running
		 */
		public void run() {
			
			while (running) {
				m_logger.info("Running worker thread.");
				process();
				try {
					m_logger.info("Putting worker thread to sleep.");
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					m_logger.fatal(e);
					running = false;
				}
			}
			m_logger.warn("Worker thread terminated");
		}
		
		/**
		 * Method to stop the thread
		 */
		public void terminate() {
			running = false;
		}
		
		
		/**
		 * Class method that begins processing
		 */
		public abstract void process(); 
}
