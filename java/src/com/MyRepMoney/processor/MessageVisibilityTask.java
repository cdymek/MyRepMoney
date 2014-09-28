package com.MyRepMoney.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cdymek.util.CdymekException;
import com.cdymek.util.PropertiesManager;
import com.cdymek.util.aws.QueueManager;
import com.cdymek.util.aws.QueueMessage;

/**
 * This class runs a separate thread which checks with a certain frequency whether or not a message has finished processing.
 * If the message has not completed processing, it sends a request to the SQS queue to extend the visibility timeout of the
 * message to prevent other consumers from processing the message
 * @author cdymek
 *
 */
public class MessageVisibilityTask extends ProcessorTask {
	
	private static Logger m_logger = null;
	private int messageWaitTimeout = 30;
	private QueueMessage qMessage = null;
	
	
	/**
	 * Initialize an instance of the class
	 * @param name
	 * @param type
	 */
	public MessageVisibilityTask(String name, String type, QueueMessage qMsg) {
		super(name, type);
		m_logger = LogManager.getLogger(this.getClass());
		m_logger.debug("Logging start.");
		qMessage = qMsg;
		try {
			messageWaitTimeout = Integer.parseInt(PropertiesManager.instance().getProperty("AWS_SQS_messagevisibility"));
		} catch (NumberFormatException | CdymekException e1) {
			m_logger.error("Failed to get property aws.sqs.messagevisibility.  Using default value|" + messageWaitTimeout);
		}		
	}

	/**
	 * The method which orchestrates the work
	 */
	public void process() {
		QueueManager qm = QueueManager.getInstance();
		qm.changeMessageVisibilityTimeout(qMessage, messageWaitTimeout);
	}

}
