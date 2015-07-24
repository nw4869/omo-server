package com.nightwind.omo.exception;

public class OrderUnrecognizedException extends Exception {

	private static final long serialVersionUID = -5355782774729854341L;
	
	public OrderUnrecognizedException() {
		
	}

	public OrderUnrecognizedException(String msg) {
		super(msg);
	}
	
}
