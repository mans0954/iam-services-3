package org.openiam.config;

import javax.servlet.ServletContext;

import org.openiam.authmanager.config.BaseAuthManagerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletContext;

@Configuration
@ComponentScan(value="org.openiam", excludeFilters={
	@Filter(
		type=FilterType.REGEX,
		pattern={
			"org.openiam.config.Application",
			"org.openiam.config.TestConfg"
		}
	)
})
@Import(value={BasePojoConfiguration.class, BaseAuthManagerConfiguration.class, BaseActivitiConfiguration.class, BaseAccessManagerConfig.class})
public class UnitTestConfig {

	@Bean
	public ServletContext servletContext() {
		final MockServletContext ctx = new MockServletContext();
		ctx.setContextPath("/openiam-esb");
		return ctx;
	}
}
