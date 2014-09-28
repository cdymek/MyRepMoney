package com.MyRepMoney;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MyRepMoney.dataloader.DataLoader;
import com.MyRepMoney.processor.ProcessorTask;
import com.cdymek.util.aws.QueueManager;
import com.cdymek.util.CdymekException;
import com.cdymek.util.PropertiesManager;
import com.cdymek.util.StaticUtilityHelpers;

public class MyRepMoneyProcessor {

	private static Logger m_logger = null;
	
	/**
	 * Method which cleans up the SQS Queue before beginning execution, if appropriate
	 */
	public static void cleanupQueue() {
		// Check for a new incoming message
		m_logger.info("Remove any existing messages from the queue.");
		QueueManager qm = QueueManager.getInstance();
		qm.cleanup();
		m_logger.info("Queue cleanup complete.");
	}	
	
	/**
	 * Method which initiates the overall process
	 * @param args
	 */
	public static void main(String[] args) {
	
		
		//Start the logger
		m_logger = LogManager.getLogger("com.MyRepMoney.MyRepMoneyProcessor");
		m_logger.warn("Initializing MyRepMoney Processor.");
		
		//Get the server type and thread class
		String serverType = null;
		String threadClass = null;
		
		//Prep the queue if we're running in test mode
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("test.mode=Y")) {
				cleanupQueue();
				break;
			}
		}
		
		//Check to see if we set a server type to override the propreties file
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("server.type=")) {
				String sKeyPair[] = args[i].split("=");
				if (sKeyPair.length > 1) {
					serverType = sKeyPair[1];
					break;
				}
			}
		}
		//If no server type was provided, check the properties file
		if (serverType == null) {
			try {
				serverType = PropertiesManager.instance().getProperty("server.type");
			} catch (CdymekException e) {
				m_logger.error("Failed to read property 'server.type'.  Continuing with server type value " + serverType);
			}
		}
		
		//Get the property needed for the monitor file
		String m_sMonitorDir = "";
		try {
			m_sMonitorDir = StaticUtilityHelpers.makeSafeDir(PropertiesManager.instance().getProperty(
					serverType + ".monitor_dir"));
		} catch (CdymekException e) {
			m_logger.fatal(e);
		}		

		//Only continue if we have a server type
		if (serverType != null) {
			try {
				//Get the processor threadclass
				threadClass = PropertiesManager.instance().getProperty(serverType + ".threadclass");
				if (threadClass == null)
					throw new CdymekException("Failed to get value for threadclass.");			
				m_logger.warn("Starting up server type " + serverType + " using threadClass " + threadClass);
				
				int iThreadCount = Integer.parseInt(PropertiesManager.instance().getProperty(serverType + ".threadcount"));
				
				//Start the processor thread
				ProcessorTask[] pts = new ProcessorTask[iThreadCount];
				Thread[] threads = new Thread[iThreadCount];
				boolean bStartupSuccess = true;
				
				//Start the processor thread
				for (int i = 0; i < iThreadCount; i++) {
					try {
						Constructor c = Class.forName(threadClass).getConstructor(String.class, String.class);
						int iThreadNum = i + 1;
						ProcessorTask myPT = (ProcessorTask)c.newInstance(serverType + "-Thread-" + iThreadNum, serverType);						
						Thread thread = new Thread(myPT);
						thread.start();
						pts[i] = myPT;
						threads[i] = thread;
					} catch (InstantiationException e) {
						m_logger.error(i + "|Failed to initialize thread|" + threadClass + "|", e);
						bStartupSuccess = false;
						break;
					} catch (IllegalAccessException e) {
						m_logger.error(i + "|Failed to initialize thread|" + threadClass + "|", e);
						bStartupSuccess = false;
						break;
					} catch (ClassNotFoundException e) {
						m_logger.error(i + "|Failed to initialize thread|" + threadClass + "|", e);
						bStartupSuccess = false;
						break;
					} catch (NoSuchMethodException e) {
						m_logger.error(i + "|Failed to initialize thread|" + threadClass + "|", e);
						bStartupSuccess = false;
						break;
					} catch (SecurityException e) {
						m_logger.error(i + "|Failed to initialize thread|" + threadClass + "|", e);
						bStartupSuccess = false;
						break;
					} catch (IllegalArgumentException e) {
						m_logger.error(i + "|Failed to initialize thread|" + threadClass + "|", e);
						bStartupSuccess = false;
						break;
					} catch (InvocationTargetException e) {
						m_logger.error(i + "|Failed to initialize thread|" + threadClass + "|", e);
						bStartupSuccess = false;
						break;
					}					
				}	
				
				//If we successfully initialized all the threads, then start monitoring for a graceful termination
				if (bStartupSuccess) {
					//Begin checking for the monitor file to exit the program.
					boolean bRunning = true;
					while (bRunning) {
						try {
							Thread.sleep(15000);
						} catch (InterruptedException e) {
							m_logger.error(e);
						}
						
						m_logger.debug("Checking directory for kill switch|" + m_sMonitorDir);
						File folder = new File(m_sMonitorDir);
						File[] listOfFiles = folder.listFiles();
						m_logger.debug("Checking for kill switch...");
						for (int i = 0; i < listOfFiles.length; i++) {
							if (listOfFiles[i].isFile() && listOfFiles[i].getName().indexOf("kill.txt") >= 0) {
								bRunning = false;
								m_logger.warn("Initiating shutdown.");
								break;
							}
						}
			
					}
				}
				
				//Initiate a graceful shutdown
				for (int i = 0; i < pts.length; i++) {
					try {
						pts[i].terminate();
						threads[i].join();
					} catch (InterruptedException e) {
						m_logger.error("Thread " + i + "|" + e.getMessage());
					} catch (NullPointerException e) {
						m_logger.error("Thread " + i + "|Error|" + e.getMessage(), e);
					}
				}
				
				//Write out a shutdown complete file
				File doneFile = new File(m_sMonitorDir + "done.txt");
				try {
					doneFile.createNewFile();
				} catch (IOException e) {
					m_logger.error("IOException creating '" + m_sMonitorDir + "done.txt while shutting down server.", e);
				}
				m_logger.warn("Shutdown complete.");
			}catch (CdymekException e1) {
				m_logger.error("Failed to get a valid value for property '" + serverType + ".threadclass.  Shutting down server.");
			}
		}
		else {
			m_logger.error("A valid server type was not provided.  Shutting down the server.");
		}		
	}
}
