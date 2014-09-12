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
	private long sleepTime;
	private String threadName;
	private volatile boolean running = true;
	   
	/**
	 * Constructor that initializes the logger class
	 */
	public MyRepMoneyDataLoadProcessorTask(String name, String type) {
		super(name, type);
		m_logger = LogManager.getLogger(this.getClass());
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
			qInMsg = qm.readMessage();
			if (qInMsg == null)
				break;
			iMsgCount++;
			
			//Parse that message in to a SourceDataSet object
			SourceDataSet sds = new SourceDataSet(qInMsg.getMessage());
			
			//Initiate the appropriate data loader class
			String dataLoadClassName = sds.getDataLoaderClass();
			Object instance;
			try {
				instance = Class.forName(dataLoadClassName).newInstance();
				DataLoader dataLoader = (DataLoader)instance;
				
				//Execute the process() method of the DataLoader class
				dataLoader.process(sds);
	
				//Remove the message from the Queue
				qm.deleteMessage(qInMsg);
				qInMsg = qm.readMessage();
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				m_logger.error("Messages Processed|" + iMsgCount + "|Error: Failed to load class|" + dataLoadClassName, e);
			} catch (MyRepMoneyException e) {
				m_logger.error("Messages Processed|" + iMsgCount + "|Error: An error occurred during the data load process using class specified|" + dataLoadClassName, e);
			}
		}
		m_logger.info(iMsgCount + " Messages Found");
		
	}	
}
