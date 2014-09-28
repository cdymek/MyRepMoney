package com.MyRepMoney.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.MyRepMoney.SourceDataSet;
import com.MyRepMoney.SourceDataSetLoader;
import com.MyRepMoney.dataloader.DataLoader;
import com.MyRepMoney.util.MyRepMoneyException;
import com.cdymek.util.aws.QueueMessage;
import com.cdymek.util.aws.QueueManager;
import com.cdymek.util.CdymekException;
import com.cdymek.util.PropertiesManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
public class MyRepMoneyDataLoadProcessorTask extends ProcessorTask implements Runnable {

	private static Logger m_logger = null;

	   
	/**
	 * Constructor that initializes the logger class
	 */
	public MyRepMoneyDataLoadProcessorTask(String name, String type) {
		super(name, type);
		m_logger = LogManager.getLogger(this.getClass());
		m_logger.debug("Logging start.");
	}
	
	
	/**
	 * Class method that begins processing
	 */
	public void process() {
		
		//Prepare the QueueManager for the SQS Queue
		QueueManager qm = QueueManager.getInstance();
		QueueMessage qInMsg = null;
		int iMsgCount = 0;
		
		//Loop until all the messages have been consumed
		for (;;) {			
			//Retrieve a message from the queue
			DataLoader dataLoader = null;
			String dataLoadClassName = null;
			long startTime = System.currentTimeMillis();

			//Check to see if we should shut down
			if (!running) {
				m_logger.warn("Shutdown initiated; breaking loop after processing messages|" + iMsgCount);
				break;
			}
			
			try {
				//Read the next message
				qInMsg = qm.readMessage();
				if (qInMsg == null) {
					m_logger.info("No additional messages found|Processed Messages|" + iMsgCount);
					break;
				}
				iMsgCount++;
				m_logger.debug("Processing message|" + iMsgCount);
				
				//Set up the thread to monitor the message's visibility timeout
				MessageVisibilityTask mvt = new MessageVisibilityTask(this.threadName + "-VisibilityTask", "dataloader.messagevisibilitytask", qInMsg);
				Thread mvtThread = new Thread(mvt);
				mvtThread.start();
								
				//Parse that message in to a SourceDataSet object
				SourceDataSet sds = new SourceDataSet(qInMsg.getMessage());
				
				//Initiate the appropriate data loader class
				dataLoadClassName = sds.getDataLoaderClass();
				m_logger.debug("Initializing Data Loader class|" + dataLoadClassName);
			


				Constructor c = Class.forName(dataLoadClassName).getConstructor();
				dataLoader = (DataLoader)c.newInstance();
				
				//Execute the process() method of the DataLoader class
				dataLoader.process(sds);
	
				//Remove the message from the Queue
				qm.deleteMessage(qInMsg);
				
				//Shut down the MessageVisibilityTask thread
				mvt.terminate();
				mvtThread.interrupt();
				mvtThread.join();
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
				m_logger.error("Messages Processed|" + iMsgCount + "|Error: Failed to load class|" + dataLoadClassName, e);
			} catch (MyRepMoneyException e) {
				m_logger.error("Messages Processed|" + iMsgCount + "|Error: An error occurred during the data load process using class specified|" + dataLoadClassName, e);
			}
			catch (Throwable t) {
				m_logger.error("Messages Processed|" + iMsgCount + "|Error: An unknown error occurred|" + dataLoadClassName, t);
			}
			finally {
				dataLoader = null;
				long runTime = System.currentTimeMillis() - startTime;
				m_logger.info("Message Processing Time|" + runTime + " ms");
			}
		}
		m_logger.info(iMsgCount + " Messages Processed");
		
	}	
}
