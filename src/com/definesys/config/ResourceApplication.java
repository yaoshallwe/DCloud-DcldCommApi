package com.definesys.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class ResourceApplication extends Application{
    public ResourceApplication() {
        super();
    }
}
