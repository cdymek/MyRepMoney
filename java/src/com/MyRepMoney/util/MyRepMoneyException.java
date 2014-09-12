package com.MyRepMoney.util;

/**
 * This class extends Exception to implement an application level exception tracker.
 * @author cdymek
 *
 */
public class MyRepMoneyException extends Exception {

	/**
	 * Throws an exception with the message provided
	 * @param message
	 */
	public MyRepMoneyException(String message) {
		super(message);
	}
	
	/**
	 * Throws an exception with a custom message plus the original exception that triggered the error.
	 * @param message
	 * @param t
	 */
	public MyRepMoneyException(String message, Throwable t) {
		super(message, t);
	}
}
