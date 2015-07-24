package com.nightwind.omo.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Dish {
	
	private int id;
	private String name;
	private String info;
	private double cost;
	private String picUrl;
	private String status;
	private int nosheryId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picurl) {
		this.picUrl = picurl;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	public int getNosheryId() {
		return nosheryId;
	}
	public void setNosheryId(int nosheryId) {
		this.nosheryId = nosheryId;
	}
	public Dish() {
	}

}
