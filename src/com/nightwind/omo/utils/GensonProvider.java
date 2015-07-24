package com.nightwind.omo.utils;

import java.text.SimpleDateFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

@Provider
@Consumes({ "application/my+json" })
@Produces({ "application/my+json" })
public class GensonProvider implements ContextResolver<Genson> {
   private final Genson genson = new GensonBuilder().useDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).create();

    @Override
   public Genson getContext(Class<?> type) {
     return genson;
   }
}