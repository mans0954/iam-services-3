package org.openiam.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.openiam.authmanager.web.AuthorizationManagerHessianServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;

@Configuration
@SpringBootApplication
//@EnableAutoConfiguration
//@EnableJpaRepositories
@Import(value={BasePojoConfiguration.class, BaseAuthManagerConfiguration.class, BaseActivitiConfiguration.class, BaseAccessManagerConfig.class})
//@ImportResource("classpath:ws-beans.xml")
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
	    return registration;
	}
	
	@Bean
	public ServletRegistrationBean hessianServlet(){
		final AuthorizationManagerHessianServlet servlet = new AuthorizationManagerHessianServlet();
	    final ServletRegistrationBean registration = new ServletRegistrationBean(servlet,"/iamauthmgr");
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
