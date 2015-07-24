package com.nightwind.omo.exception;

public class RequestLengthException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5696757484908607987L;

	
	public RequestLengthException() {
		
	}
	
	public RequestLengthException(String msg) {
		super(msg);
	}
}
