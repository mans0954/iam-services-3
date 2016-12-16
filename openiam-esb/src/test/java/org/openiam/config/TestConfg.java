package org.openiam.config;

import org.openiam.test.config.SoapHeaderInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@ComponentScan({"org.openiam.authmanager.service.integration", 
				"org.openiam.authentication.integration", 
				"org.openiam.provisioning", 
				"org.openiam.idm.stresstest"})
@Configuration
@ImportResource({"classpath:test-integration-environment.xml", "classpath:test-esb-integration.xml"})
public class TestConfg {

}
