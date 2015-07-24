package com.nightwind.omo.resources;

import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.nightwind.omo.exception.DishNoFoundException;
import com.nightwind.omo.exception.DishStatusPurchaseUnavailable;
import com.nightwind.omo.exception.InsufficientAccountBalanceException;
import com.nightwind.omo.exception.OrderStatusChangeException;
import com.nightwind.omo.exception.OrderUnrecognizedException;
import com.nightwind.omo.exception.RequestLengthException;
import com.nightwind.omo.exception.UserDeliveryNotFound;
import com.nightwind.omo.manager.DeliveryManager;
import com.nightwind.omo.manager.OrderManager;
import com.nightwind.omo.model.Delivery;
import com.nightwind.omo.model.Dish;
import com.nightwind.omo.model.Order;
import com.nightwind.omo.utils.DateUtils;
import com.nightwind.omo.utils.ErrorResponse;
import com.owlike.genson.Genson;

public class OrdersResource {
	
	String username;
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	public OrdersResource() {
	}
	
	public OrdersResource(String username, UriInfo uriInfo, Request request) {
		super();
		this.username = username;
		this.uriInfo = uriInfo;
		this.request = request;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrders() {
		List<Order> orders = new OrderManager(username).getOrders();
		Genson genson = DateUtils.getGenson();
		String json = genson.serialize(orders);
		return Response.ok(json).build();
	}
	
	public static void main(String[] args) {
		new OrdersResource().getOrders();
	}
	

	@POST
    @Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	// 修改成一个user对应一个delivery
	public Response putOrder(@BeanParam Order order/*, @FormParam("deliveryId") String deliveryId*/) {
		Response res = Response.status(400).entity(new ErrorResponse("bad request")).build();
		try {
//			Delivery delivery = DeliveryManager.getDelivery(deliveryId);
			Delivery delivery = DeliveryManager.getUserDelivery(username);
			if(delivery == null || delivery.getAddress().trim().length() == 0 || delivery.getTel().trim().length() == 0) {
				throw new UserDeliveryNotFound();
			}
			order.setAddress(delivery.getAddress());
			order.setTel(delivery.getTel());
			new OrderManager(order.getUsername()).putOrder(order);
			Genson genson = DateUtils.getGenson();
			String json = genson.serialize(order);
			res = Response.status(201).entity(json).build();
		} catch (UserDeliveryNotFound e) {
			res = Response.status(400).entity(new ErrorResponse("请完善收餐信息")).build();
		} catch (InsufficientAccountBalanceException e) {
			res = Response.status(400).entity(new ErrorResponse("您余额不足")).build();
		} catch (DishNoFoundException e) {
			res = Response.status(400).entity(new ErrorResponse("菜品不存在")).build();
		} catch (RequestLengthException e) {
			res = Response.status(400).entity(e.getMessage()).build();
		} catch (DishStatusPurchaseUnavailable e) {
			res = Response.status(400).entity(e.getMessage()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	@Path("{orderId}")
	public OrderResource getOrder(@PathParam("orderId") String orderId) {
		if (new OrderManager(username).verifyOwner(orderId)) {
			return new OrderResource(orderId, username, uriInfo, request);
		} else {
			throw new NotFoundException();
		}
	}
	
	public class OrderResource {

		String orderId;
		String username;
		@Context
		UriInfo uriInfo;
		@Context
		Request request;
		
		public OrderResource() {
		}
		
		public OrderResource(String orderId, String username, UriInfo uriInfo,
				Request request) {
			super();
			this.orderId = orderId;
			this.username = username;
			this.uriInfo = uriInfo;
			this.request = request;
		}
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response getOrder() {
			Order order = null;
			try {
				order = new OrderManager(username).getOrder(orderId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (order == null) {
				throw new NotFoundException();
			}
			Genson genson = DateUtils.getGenson();
			String json = genson.serialize(order);
			return Response.ok(json).build();
		}
		
		@PUT
	    @Consumes("application/x-www-form-urlencoded")
		@Produces(MediaType.APPLICATION_JSON)
		public Response updateStatus(@FormParam("status") String status) {
			Response res = Response.status(400).entity(new ErrorResponse("bad request")).build();
			try {
				OrderManager orderManager = new OrderManager(username);
				orderManager.updateOrderStatus(orderId, status);
				Order order = orderManager.getOrder(orderId);
				Genson genson = DateUtils.getGenson();
				String json = genson.serialize(order);
				res = Response.created(uriInfo.getAbsolutePath()).entity(json).build();
			} catch (OrderStatusChangeException e) {
				res = Response.status(400).entity(new ErrorResponse("订单状态更改失败，当前状态：" + e.getMessage())).build();
			} catch (OrderUnrecognizedException e) {
				res = Response.status(400).entity(new ErrorResponse("订单状态不可识别: " + e.getMessage())).build();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}
			
	}
	

}
