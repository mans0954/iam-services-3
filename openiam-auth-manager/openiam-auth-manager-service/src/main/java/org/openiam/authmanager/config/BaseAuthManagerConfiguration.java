package org.openiam.authmanager.config;

import org.openiam.config.BaseConfiguration;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import net.sf.ehcache.Ehcache;

@Configuration
@Import(BaseConfiguration.class)
public class BaseAuthManagerConfiguration {

	@Bean(name="authManagerCacheManager")
	public EhCacheManagerFactoryBean authManagerCacheManager() {
		final EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
		bean.setShared(true);
		bean.setConfigLocation(new ClassPathResource("authorization.manager.ehcache.xml"));
		return bean;
	}
	
	@Bean(name="authManagerUserCache")
	public Ehcache authManagerUserCache() {
		return authManagerCacheManager().getObject().getCache("org.openiam.authorization.manager.USER_CACHE");
	}
}
