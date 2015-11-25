package org.openiam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Import(BaseConfiguration.class)
public class BaseAccessManagerConfig {
	
	@Value("${org.openiam.uri.patterns.defaut.object.file}")
	private String patternFileName;
	
	@Value("${org.openiam.template.field.defaut.object.file}")
	private String fieldFileName;

	@Bean(name="defaultPatternResource")
	public ClassPathResource defaultPatternResource() {
		return new ClassPathResource(patternFileName);
	}
	
	@Bean(name="defaultTemplateFieldResource")
	public ClassPathResource defaultTemplateFieldResource() {
		return new ClassPathResource(fieldFileName);
	}
}
