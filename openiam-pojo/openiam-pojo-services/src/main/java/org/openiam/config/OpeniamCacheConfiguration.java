package org.openiam.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.cache.CacheBeanPostProcessor;
import org.openiam.cache.OpeniamAnnotationCacheOperationSource;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.annotation.ProxyCachingConfiguration;
import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.type.AnnotationMetadata;

import com.hazelcast.spring.cache.HazelcastCacheManager;

/**
 * @author Lev Bornovalov
 * 
 * Overrides Spring's defualt cache configuration, allowing us to provide a custom cache interceptor.
 * 
 * Required for IDMAPPS-3644
 * 
 */
@Configuration
public class OpeniamCacheConfiguration extends ProxyCachingConfiguration implements ApplicationContextAware {
	
	private static final Log LOG = LogFactory.getLog(OpeniamCacheConfiguration.class);
	
	@Autowired
    private HazelcastConfiguration hazelcastConfiguration;
	
	private ApplicationContext ctx;
	
	public OpeniamCacheConfiguration() {
		super();
		if(LOG.isDebugEnabled()) {
			LOG.debug("Constructing OpeniamCacheConfiguration...");
		}
	}
	
	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		//do nothing - parent class implementation depends on @EnableCaching being present, which
		//screws everything up
	}
	
	@Bean(name = CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor() {
		BeanFactoryCacheOperationSourceAdvisor advisor =
				new BeanFactoryCacheOperationSourceAdvisor();
		advisor.setCacheOperationSource(cacheOperationSource());
		advisor.setAdvice(cacheInterceptor());
		return advisor;
	}
	
	/**
	 * 
	 */
	@Bean
	@Override
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public CacheOperationSource cacheOperationSource() {
		DefaultListableBeanFactory foo = null;
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
	@Override
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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
	
	/* AM-851 - Custom @Cacheable annotations should *never* cache Entity objects */
	@Bean
	public CacheBeanPostProcessor cacheBeanPostProcessor() {
		return new CacheBeanPostProcessor();
	}
	
	/**
	 * See http://docs.hazelcast.org/docs/3.5/manual/html/springintegration.html
	 * @return
	 */
	 @Bean
	 public CacheManager cacheManager() {
		 return new HazelcastCacheManager(hazelcastConfiguration.getHazelcastInstance());
	 }
	
	 @Bean
	 public KeyGenerator keyGenerator() {
		 return null;
	 }
	 /**
	  * End hazelcast specific cache stuff
	  */
}
