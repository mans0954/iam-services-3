package org.openiam.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.openiam.am.srvc.config.AmMessageListenerConfig;
import org.openiam.authmanager.web.AuthorizationManagerHessianServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.annotation.ProxyCachingConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ComponentScan(basePackages={"org.openiam"},excludeFilters={})
@Configuration
@SpringBootApplication
@EnableWebMvc
//@EnableCaching
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@Import(value={BasePojoConfiguration.class, IdmMessageListenerConfig.class, AmMessageListenerConfig.class, BaseAuthManagerConfiguration.class, BaseActivitiConfiguration.class, BaseAccessManagerConfig.class, OpeniamCacheConfiguration.class})
public class Application {

	public static void main(final String[] args) {
    	SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public ServletRegistrationBean cxfServlet(){
		final CXFServlet servlet = new CXFServlet();
	    final ServletRegistrationBean registration = new ServletRegistrationBean(servlet,"/idmsrvc/*");
	    final Map<String, String> initParameters = new HashMap<String, String>();
	    initParameters.put("config-location", "classpath:ws-beans.xml");
	    registration.setInitParameters(initParameters);
	    registration.setLoadOnStartup(1);
	    return registration;
	}
	
	@Bean
	public ServletRegistrationBean hessianServlet(){
		final AuthorizationManagerHessianServlet servlet = new AuthorizationManagerHessianServlet();
	    final ServletRegistrationBean registration = new ServletRegistrationBean(servlet,"/iamauthmgr");
	    registration.setLoadOnStartup(2);
	    return registration;
	}
	
	@Bean
    public FilterRegistrationBean someFilterRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new OpenSessionInViewFilter());
        registration.addUrlPatterns("/*");
        registration.setName("OpenSessionInViewFilter");
        return registration;
    }
}
