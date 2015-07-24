package com.nightwind.omo.resources;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.nightwind.omo.exception.SMSIncorrectException;
import com.nightwind.omo.manager.DeliveryManager;
import com.nightwind.omo.manager.UserManager;
import com.nightwind.omo.model.Delivery;
import com.nightwind.omo.utils.DateUtils;
import com.nightwind.omo.utils.ErrorResponse;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

//@Path("/delivery")
public class DeliveriesResource {

	String username;
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	@Context
	HttpServletRequest HSRequest;


	public DeliveriesResource() {
	}

	public DeliveriesResource(String username, UriInfo uriInfo, Request request) {
		this.username = username;
		this.uriInfo = uriInfo;
		this.request = request;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeliveries() {
		List<Delivery> deliveries = DeliveryManager.getDeliveries(username);
		Genson genson = DateUtils.getGenson();
		String json = genson.serialize(deliveries);
		return Response.ok(json).build();
	}

//	@POST
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response putDelivery(@NotNull @FormParam("address") String address,
//			@NotNull @FormParam("tel") String tel, @NotNull @FormParam("smscode") String smscode) {
//		Delivery delivery = new Delivery(username, tel, address);
//		Response res;
//		try {
//			delivery = DeliveryManager.putDelivery(delivery, smscode);
////			res = Response.created(uriInfo.getAbsolutePath()).entity(delivery).build();
//			URI uri = URI.create(uriInfo.getAbsolutePath().toString().concat(String.valueOf(delivery.getId())));
//			res = Response.created(uri).entity(delivery).build();
//		} catch (SMSIncorrectException e) {
//			res = Response.status(400).entity("{\"error\"=\"smscode\"}").build();
////			throw new WebApplicationException("{\"error\"=\"smscode\"}", 400);
//		} catch (Exception e) {
//			e.printStackTrace();
//			res = Response.status(400).build();
////			throw new WebApplicationException(400);
//		}
//		return res;
//	}
	
	@POST
    @Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	public Response putDelivery(@BeanParam Delivery delivery, @NotNull @FormParam("smscode") String smscode) {
		String ip = HSRequest.getRemoteAddr();
		UserManager.checkBan(ip);
		Response res;
		try {
			delivery = DeliveryManager.putDelivery(delivery, smscode);
			URI uri = URI.create(uriInfo.getAbsolutePath().toString().concat(String.valueOf(delivery.getId())));
			res = Response.created(uri).entity(delivery).build();
		} catch (SMSIncorrectException e) {
			UserManager.ban(ip);
			res = Response.status(400).entity(new ErrorResponse("smscode")).build();
		} catch (ConstraintViolationException e) {
			res = Response.status(400).build();
		} catch (Exception e) {
			e.printStackTrace();
			res = Response.status(400).build();
		}
		return res;
	}


	@GET
	@Path("count")
	@Produces(MediaType.TEXT_HTML)
	public String getCount() {
		int count = DeliveryManager.getDeliveries(username).size();
		return String.valueOf(count);
	}

	@Path("{deliveryId}")
	public DeliveryResource getDelivery(@PathParam("deliveryId") String deliveryId) {
		if (DeliveryManager.verifyOwner(deliveryId, username)) {
			return new DeliveryResource(username, deliveryId, uriInfo, request);
		} else {
			throw new NotFoundException();
		}
	}

	public class DeliveryResource {

		String id;
		String username;
		@Context
		UriInfo uriInfo;
		@Context
		Request request;

		public DeliveryResource() {
		}

		public DeliveryResource(String username, String id, UriInfo uriInfo,
				Request request) {
			super();
			this.id = id;
			this.username = username;
			this.uriInfo = uriInfo;
			this.request = request;
		}

		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response getDelivery() {
			Delivery delivery = null;
			try {
				delivery = DeliveryManager.getDelivery(id);
			} catch (IndexOutOfBoundsException ignore) {

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (delivery == null) {
				throw new WebApplicationException(404);
			}
//			String time = DateUtils.simpleDateFormat.format(delivery.getTelVerifiTime());
//			System.out.println("delivery date = " + time);
			Genson genson = DateUtils.getGenson();
			String json = genson.serialize(delivery);
			ResponseBuilder res = Response.ok(json);
			return res.build();
		}
		

		@PUT
	    @Consumes("application/x-www-form-urlencoded")
		@Produces(MediaType.APPLICATION_JSON)
		public Response updateDelivery(@BeanParam Delivery delivery, @NotNull @FormParam("smscode") String smscode) {
			String ip = HSRequest.getRemoteAddr();
			UserManager.checkBan(ip);
			Response res = Response.status(400).build();
			try {
				delivery = DeliveryManager.update(delivery, smscode, true);
				res = Response.created(uriInfo.getAbsolutePath()).entity(delivery).build();
			} catch (SMSIncorrectException e) {
				UserManager.ban(ip);
				res = Response.status(400).entity(new ErrorResponse("smscode")).build();
			} catch (ConstraintViolationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}
		
		@DELETE
	    @Consumes("application/x-www-form-urlencoded")
		public Response deleteDelivery(@BeanParam Delivery delivery) {
			Response res;
			try {
				DeliveryManager.delete(delivery);
				res = Response.noContent().build();
			} catch (Exception e) {
				res = Response.status(403).build();
				e.printStackTrace();
			}
			return res;
		}

	}

}
