package com.nightwind.omo.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;

import com.nightwind.omo.manager.DishManager;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

@XmlRootElement
public class Order {
	@PathParam("orderId")
	private int id;
	@PathParam("username")
	private String username;
	private String address;
	private String tel;

	@FormParam("dishId")
	private int dishId;

	@FormParam("dishCount")
	private int dishCount;
	
	private double dishCost;
	private double cost;
	private String status;
	private Date time;
	private Date confirmTime;
	private Date sendTime;
	
//	private Dish dish;

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

	public int getDishId() {
		return dishId;
	}

	public void setDishId(int dishId) {
		this.dishId = dishId;
	}

	public int getDishCount() {
		return dishCount;
	}

	public void setDishCount(int dishCount) {
		this.dishCount = dishCount;
	}

	public double getDishCost() {
		return dishCost;
	}

	public void setDishCost(double dishCost) {
		this.dishCost = dishCost;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	

	public Order() {
	}
	

	/**
	 * dish detail for android client
	 * @return
	 */
	public Dish getDish() {
		Dish dish = DishManager.getDish(String.valueOf(dishId));
		return dish;
	}

//	public void setDish(Dish dish) {
//		this.dish = dish;
//	}
	
}
