package org.openiam.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.idm.util.CustomJacksonMapper;
import org.openiam.mq.processor.BaseBackgroundProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableAspectJAutoProxy
@ImportResource({"classpath:environmentContext.xml", "classpath:databaseContext.xml"})
public class BaseConfiguration implements SchedulingConfigurer {
	
	@Autowired
	private HazelcastConfiguration hazelcastConfig;
	
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
		return createTaskExecutor(100, 200, 300, "TaskExecutor-");
	}

	@Bean(name = "workerTaskExecutor")
	public ThreadPoolTaskExecutor workerTaskExecutor() {
		return createTaskExecutor(20, 50, 100, "WorkerTaskExecutor-");
	}

	@Bean
	public BaseBackgroundProcessorService baseBackgroundProcessorService(){
		return new BaseBackgroundProcessorService(taskExecutor(), workerTaskExecutor());
	}

	private ThreadPoolTaskExecutor createTaskExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String prefix){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix(prefix);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
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
	
	
	@Bean(destroyMethod="shutdown", name="scheduler")
	public ThreadPoolTaskScheduler scheduler() {
		final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(taskSchedulerSize);
		return scheduler;
	}
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(scheduler());
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
