package com.nightwind.omo.model;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.nightwind.omo.exception.RequestLengthException;
import com.nightwind.omo.manager.UserManager;
import com.nightwind.omo.utils.ErrorResponse;

@Path("auth")
public class Auth {

	@Context
	HttpServletRequest HSRequest;
	
	@POST
	@Path("{username}")
    @Consumes("application/x-www-form-urlencoded")
	public Response checkToken(@PathParam("username") String username, @FormParam("password") String password) {
		String ip = HSRequest.getRemoteAddr();
		UserManager.checkBan(ip);
		
		System.out.println("username = " + username + " password = " + password);
		Response res = Response.status(403).entity(new ErrorResponse("password")).build();
		try {
			if (UserManager.checkPassword(username, password)) {
				String token = UserManager.getToken(username);
				UserToken userToken = new UserToken(username, token);
				res = Response.status(201).entity(userToken).build();
			} else {
			UserManager.ban(ip);
			}
		} catch (RequestLengthException e) {
			UserManager.ban(ip);
			res = Response.status(400).entity(e.getMessage()).build();
		}
		return res;
	}
}
