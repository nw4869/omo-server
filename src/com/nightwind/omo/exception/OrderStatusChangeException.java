package com.nightwind.omo.exception;

public class OrderStatusChangeException extends Exception {

	private static final long serialVersionUID = -4045197776809952839L;

	public OrderStatusChangeException() {
		
	}
	
	public OrderStatusChangeException(String msg) {
		super(msg);
	}
}
