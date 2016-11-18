package org.openiam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@Import(BaseConfiguration.class)
@ImportResource(value={"classpath:bpm-context.xml"})
public class BaseActivitiConfiguration {

}
