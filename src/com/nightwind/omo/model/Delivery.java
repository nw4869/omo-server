package com.nightwind.omo.model;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Delivery {
	@PathParam("deliveryId")
	private int id;
	
	@NotNull
	@PathParam("username")
	private String username;
	
	@NotNull
	@FormParam("address")
	private String address;

	@NotNull
	@FormParam("tel")
	private String tel;
	
	private Date telVerifiTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Date getTelVerifiTime() {
		return telVerifiTime;
	}

	public void setTelVerifiTime(Date telVerifiTime) {
		this.telVerifiTime = telVerifiTime;
	}

	public Delivery() {

	}

	public Delivery(String username, String tel, String address) {
		this.username = username;
		this.tel = tel;
		this.address = address;
	}

	@Override
	public String toString() {
		return "relivery: username = " + username + " tel = " + tel + " address = " + address; 
	}

}
