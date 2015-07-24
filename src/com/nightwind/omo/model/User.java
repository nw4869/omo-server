package com.nightwind.omo.model;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User extends UserResponse {

	@NotNull
	@PathParam("username")
	private String username;

	@NotNull
	@FormParam("password")
	private String password;
	
	private String identity;
	
	private Double balance;
	
	@NotNull
	@FormParam("name")
	private String name;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public User() {
	}
	
	public User(String username, String password, String identity) {
		super();
		this.username = username;
		this.password = password;
		this.identity = identity;
	}
	
	@Override
	public String toString() {
		return "username = " + username + " name = " + name + " password = " + password;
	}
}
