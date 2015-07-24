package com.nightwind.omo.model;

public class UserToken {
	private String uername;
	private String token;

	public String getUername() {
		return uername;
	}

	public void setUername(String uername) {
		this.uername = uername;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserToken() {
	}

	public UserToken(String uername, String token) {
		super();
		this.uername = uername;
		this.token = token;
	}
	
	
}
