package org.openiam.config;

import org.openiam.test.config.SoapHeaderInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.ComponentScan.Filter;

@Configuration
@ImportResource({"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class IntegrationTestConfig {

}
