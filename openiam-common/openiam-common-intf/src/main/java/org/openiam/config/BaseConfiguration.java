package org.openiam.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.openiam.idm.util.CustomJacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.Resource;

@Configuration
@EnableAsync
@EnableScheduling
//@EnableCaching
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@ImportResource({"classpath:environmentContext.xml", "classpath:databaseContext.xml"})
public class BaseConfiguration implements SchedulingConfigurer {
	
	@Autowired
	private CustomJacksonMapper mapper;
	
	@Value("${org.openiam.spring.taskscheduler.scheduler.size}")
	private int taskSchedulerSize;
	
	@Value("${org.openiam.batch.task.executor.size}")
	private int corePoolSize;
	
	@Value("${org.openiam.batch.task.executor.size}")
	private int maxPoolSize;
	
	@Value("${org.openiam.batch.task.executor.queue.capacity}")
	private int queueCapacity;
	
	@Bean(name="authManagerCompilationPool")
	public ThreadPoolTaskExecutor authManagerCompilationPool() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		return executor;
	}
	
	/* this is for @Async stuff */
	@Bean(name="taskExecutor")
	public ThreadPoolTaskExecutor taskExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		return executor;
	}

	public MappingJackson2HttpMessageConverter getConverter() {
		final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(mapper);
		return converter;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		final List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(getConverter());
		restTemplate.setMessageConverters(converters);
		return restTemplate;
	}
	
	@Bean(destroyMethod="shutdown", name="myScheduler")
    public Executor myScheduler() {
        return Executors.newScheduledThreadPool(taskSchedulerSize);
    }

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(myScheduler());
	}
	
	@Bean(name="batchTaskThreadExecutor")
	public ThreadPoolTaskExecutor batchTaskThreadExecutor() {
		final ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
		e.setCorePoolSize(corePoolSize);
		e.setMaxPoolSize(maxPoolSize);
		e.setQueueCapacity(queueCapacity);
		return e;
	}
}
