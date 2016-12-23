package org.openiam.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.openiam.concurrent.OpenIAMThreadPoolTaskExecutor;
import org.openiam.concurrent.OpenIAMThreadPoolTaskScheduler;
import org.openiam.concurrent.SecurityInfoProvider;
import org.openiam.idm.util.CustomJacksonMapper;
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
	private CustomJacksonMapper mapper;
	
	@Value("${org.openiam.spring.taskscheduler.scheduler.size}")
	private int taskSchedulerSize;
	
	@Value("${org.openiam.batch.task.executor.size}")
	private int corePoolSize;
	
	@Value("${org.openiam.batch.task.executor.size}")
	private int maxPoolSize;
	
	@Value("${org.openiam.batch.task.executor.queue.capacity}")
	private int queueCapacity;
	
	@Value("${org.openiam.idm.system.user.id}")
	private String systemUserId;
	
	@Bean(name="authManagerCompilationPool")
	public OpenIAMThreadPoolTaskExecutor authManagerCompilationPool() {
		final OpenIAMThreadPoolTaskExecutor executor = new OpenIAMThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.initialize();
		executor.setRequestorIDProvider(new SecurityInfoProvider() {
			
			@Override
			public String getRequestorId() {
				return systemUserId;
			}

			@Override
			public String getLanguageId() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		return executor;
	}
	
	/* this is for @Async stuff */
	@Bean(name="taskExecutor")
	public OpenIAMThreadPoolTaskExecutor taskExecutor() {
		final OpenIAMThreadPoolTaskExecutor executor = createTaskExecutor(100, 200, 300, "TaskExecutor-");
		return executor;
	}

	@Bean(name = "workerTaskExecutor")
	public OpenIAMThreadPoolTaskExecutor workerTaskExecutor() {
		final OpenIAMThreadPoolTaskExecutor executor = createTaskExecutor(20, 50, 100, "WorkerTaskExecutor-");
		return executor;
	}

	private OpenIAMThreadPoolTaskExecutor createTaskExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String prefix){
		final OpenIAMThreadPoolTaskExecutor executor = new OpenIAMThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix(prefix);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		executor.setRequestorIDProvider(new SecurityInfoProvider() {
			
			@Override
			public String getRequestorId() {
				return systemUserId;
			}

			@Override
			public String getLanguageId() {
				// TODO Auto-generated method stub
				return null;
			}
		});
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
	public OpenIAMThreadPoolTaskScheduler scheduler() {
		final OpenIAMThreadPoolTaskScheduler scheduler = new OpenIAMThreadPoolTaskScheduler();
		scheduler.setPoolSize(taskSchedulerSize);
		scheduler.initialize();
		scheduler.setRequestorIDProvider(new SecurityInfoProvider() {
			
			@Override
			public String getRequestorId() {
				return systemUserId;
			}

			@Override
			public String getLanguageId() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		return scheduler;
	}
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(scheduler());
	}
	
	@Bean(name="batchTaskThreadExecutor")
	public OpenIAMThreadPoolTaskExecutor batchTaskThreadExecutor() {
		final OpenIAMThreadPoolTaskExecutor e = new OpenIAMThreadPoolTaskExecutor();
		e.setCorePoolSize(corePoolSize);
		e.setMaxPoolSize(maxPoolSize);
		e.setQueueCapacity(queueCapacity);
		e.initialize();
		e.setRequestorIDProvider(new SecurityInfoProvider() {
			
			@Override
			public String getRequestorId() {
				return systemUserId;
			}

			@Override
			public String getLanguageId() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		return e;
	}
}
