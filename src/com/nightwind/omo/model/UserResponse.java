package com.nightwind.omo.model;

public class UserResponse {

	private String username;
	private String name;
	private Double balance;
	private String tel;
	private String address;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
	
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public UserResponse() {
		
	}
	
	public UserResponse(User user, Delivery delivery) {
		if (user != null) {
			this.username = user.getUsername();
			this.name = user.getName();
			this.balance = user.getBalance();
		}
		if (delivery != null) {
			this.tel = delivery.getTel();
			this.address = delivery.getAddress();
		}
	}

}
