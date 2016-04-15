package org.openiam.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.cache.OpeniamAnnotationCacheOperationSource;
import org.openiam.cache.OpeniamCacheInterceptor;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.ProxyCachingConfiguration;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author Lev Bornovalov
 * 
 * Overrides Spring's defualt cache configuration, allowing us to provide a custom cache interceptor.
 * 
 * Required for IDMAPPS-3644
 */
@Configuration
public class OpeniamCacheConfiguration extends ProxyCachingConfiguration implements ApplicationContextAware {
	
	private static final Log LOG = LogFactory.getLog(OpeniamCacheConfiguration.class);
	
	@Autowired
    private HazelcastConfiguration hazelcastConfiguration;
	
	private ApplicationContext ctx;
	
	/**
	 * 
	 */
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public CacheOperationSource cacheOperationSource() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Creating cacheOperationSource...");
		}
		return new OpeniamAnnotationCacheOperationSource();
	}

	/**
	 * This is a direct copy/paste from the superclass.
	 * 
	 * However, it allows us to instantiate a custom implementation of CacheInterceptor,
	 * which we need in order to do get access to the generated @Cacheable and @Cachevict keys
	 */
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	@Override
	public CacheInterceptor cacheInterceptor() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Creating cacheInterceptor...");
		}
		final OpeniamCacheInterceptor interceptor = new OpeniamCacheInterceptor();
		interceptor.setApplicationContext(ctx);
		interceptor.setCacheOperationSources(cacheOperationSource());
		if (this.cacheResolver != null) {
			interceptor.setCacheResolver(this.cacheResolver);
		}
		else if (this.cacheManager != null) {
			interceptor.setCacheManager(this.cacheManager);
		}
		if (this.keyGenerator != null) {
			interceptor.setKeyGenerator(this.keyGenerator);
		}
		if (this.errorHandler != null) {
			interceptor.setErrorHandler(this.errorHandler);
		}
		interceptor.setHazelcastConfiguration(hazelcastConfiguration);
		interceptor.init();
		return interceptor;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}
}
