package com.nightwind.omo.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.id.IdentifierGenerationException;

import com.nightwind.omo.exception.RequestLengthException;
import com.nightwind.omo.exception.SMSIncorrectException;
import com.nightwind.omo.exception.UserDeliveryNotFound;
import com.nightwind.omo.manager.DeliveryManager;
import com.nightwind.omo.manager.UserManager;
import com.nightwind.omo.model.Delivery;
import com.nightwind.omo.model.User;
import com.nightwind.omo.model.UserResponse;
import com.nightwind.omo.model.UserToken;
import com.nightwind.omo.utils.ErrorResponse;

@Path("users")
public class UsersResource {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	@Context
	HttpServletRequest HSRequest;

	@Path("{username}")
	public UserResource getUser(@PathParam("username") String username, @HeaderParam("Authorization") String token) {
		String ip = HSRequest.getRemoteAddr();
		UserManager.checkBan(ip);
		System.out.println("method = " + request.getMethod());
		System.out.println(uriInfo.getAbsolutePath().toString());
		System.out.println(HSRequest.getRequestURI());
		
//		String uri = HSRequest.getRequestURI();
//		String authUri = "/onlineMealOrdering/rest/users/nw/auth";
		
		try {
			if (/*uri.equals(authUri) || */UserManager.checkToken(username, token)) {
				return new UserResource(username, request, uriInfo);
			} else {
				UserManager.ban(ip);
				Response res = Response.status(403).entity(new ErrorResponse("token error")).build();
				throw new WebApplicationException(res);
			}
		} catch (RequestLengthException e) {
			UserManager.ban(ip);
			Response res = Response.status(403).entity(e.getMessage()).build();
			throw new WebApplicationException(res);
		}
	}
	

	@POST
    @Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	public Response newUser(@BeanParam User u, @FormParam("username") String username) {
		u.setUsername(username);
		System.out.println(u);
		Response res = Response.status(400).entity(new ErrorResponse("其他错误")).build();
		try {
			UserManager.putUser(u);
			UserToken uk = new UserToken(username, UserManager.getToken(username));
			res = Response.created(uriInfo.getAbsolutePath()).entity(uk).build();
		} catch (ConstraintViolationException | IdentifierGenerationException e) {
			res = Response.status(400).entity(new ErrorResponse("存在不为空参数为空")).build();
		} catch (org.hibernate.exception.ConstraintViolationException e) {
			res = Response.status(400).entity(new ErrorResponse("用户名重复")).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public class UserResource {

		String username;
		@Context
		UriInfo uriInfo;
		@Context
		Request request;

		public UserResource(String username, Request request, UriInfo uriInfo) {
			super();
			this.username = username;
			this.uriInfo = uriInfo;
			this.request = request;
		}

		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public UserResponse getUser() {
			User u = null;
			Delivery delivery = null;
			try {
				u = UserManager.getUser(username);
				try { 
					delivery = DeliveryManager.getUserDelivery(username);
				} catch (Exception ignored) {
				}
			} catch (Exception e) {
				throw new NotFoundException();
				// throw new WebApplicationException(404);
			}
			if (u == null) {
				throw new NotFoundException();
			}
			return new UserResponse(u, delivery);
		}

		@PUT
	    @Consumes("application/x-www-form-urlencoded")
		@Produces(MediaType.APPLICATION_JSON)
		public Response updateUser(@BeanParam User user, @BeanParam Delivery delivery, @FormParam("smscode") String smscode, @HeaderParam("Authorization") String token) {
			System.out.println(user);
			System.out.println(delivery);
			System.out.println("smscode = " + smscode);
			System.out.println("Authorization = " + token);
			String ip = HSRequest.getRemoteAddr();
			UserManager.checkBan(ip);
			Response res = Response.status(403).entity(new ErrorResponse("token error")).build();
			try {
				if (UserManager.checkToken(user.getUsername(), token)) {
					UserManager.update(user);
					try {
						DeliveryManager.updateOrInstertDelivery(username, delivery, smscode);
						user = UserManager.getUser(username);
						delivery = DeliveryManager.getUserDelivery(username);
						res = Response.created(uriInfo.getAbsolutePath()).entity(new UserResponse(user, delivery)).build();
					} catch (SMSIncorrectException e) {
						res = Response.status(403).entity(new ErrorResponse("smscode error")).build();
//						e.printStackTrace();
					} catch (UserDeliveryNotFound e) {
//						e.printStackTrace();
					}
				} 
			} catch (RequestLengthException e) {
				res = Response.status(403).entity(e.getMessage()).build();
			}
			if (res.getStatus() != 201) {
				UserManager.ban(ip);
			}
			return res;
		}


		@Path("/deliveries")
		public DeliveriesResource getDeliveries() {
			return new DeliveriesResource(username, uriInfo, request);
		}
		
		@Path("/orders")
		public OrdersResource getOrders() {
			return new OrdersResource(username, uriInfo, request);
		}
		
//		@POST
//		@Path("/auth")
//	    @Consumes("application/x-www-form-urlencoded")
//		public Response checkToken(@FormParam("password") String password) {
//			System.out.println("username = " + username + " password = " + password);
//			Response res = Response.status(403).entity(new ErrorResponse("password")).build();
//			if (UserManager.checkPassword(username, password)) {
//				String token = UserManager.getToken(username);
//				UserToken userToken = new UserToken(username, token);
//				res = Response.status(201).entity(userToken).build();
//			}
//			return res;
//		}

	}

}
