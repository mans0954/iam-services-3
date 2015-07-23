package org.openiam.config;

import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Import(BaseConfiguration.class)
@ImportResource(value={"classpath:bpm-context.xml"})
public class BaseActivitiConfiguration {

}
