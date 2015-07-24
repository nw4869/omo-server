package com.nightwind.omo.utils;


import org.glassfish.jersey.server.ResourceConfig;

import com.owlike.genson.ext.jaxrs.GensonJsonConverter;

public class ServerApplication extends ResourceConfig {
    public ServerApplication() {
        super();
        register(GensonProvider.class);
    }
}
