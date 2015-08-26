package org.openiam.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.openiam.idm.srvc.synch.rs.SynchRestEndpoint;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Vitaly on 8/24/2015.
 */
@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(SynchRestEndpoint.class);
    }
}
