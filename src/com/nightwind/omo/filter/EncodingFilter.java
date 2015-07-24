package com.nightwind.omo.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;

@PreMatching
public class EncodingFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext arg0) throws IOException {
		((ServletRequest) arg0.getRequest()).setCharacterEncoding("UTF-8"); 
		System.out.println("converted");
	}



}
