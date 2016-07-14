package org.openiam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String host;
	
	@Value("${spring.redis.port}")
	private int port;
	
	@Value("${spring.redis.password}")
	private String password;

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		final JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName(host);
		factory.setPassword(password);
		factory.setPort(port);
		factory.setUsePool(true); /* will use default pool */
		return factory;
	}
	
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setEnableTransactionSupport(false); /* enabling transaction support slows things down considerably */
		template.setKeySerializer(new StringRedisSerializer()); /* otherwise our keys will have binary data in them, and they will not be referenceable */
		return template;
	}
	
	@Bean
	public RedisMessageListenerContainer messageListenerContainer() {
		final RedisMessageListenerContainer listener = new RedisMessageListenerContainer();
		listener.setConnectionFactory(jedisConnectionFactory());
		listener.setMessageListeners(new HashMap<MessageListener, Collection<? extends Topic>>());
		return listener;
	}

	@Bean(name = "listenerTaskExecutor")
	public TaskExecutor listenerTaskExecutor() {
		return createDemonTaskExecutor("ListenerTaskExecutor-");
	}

	@Bean(name = "workerTaskExecutor")
	public TaskExecutor workerTaskExecutor() {
		return createDemonTaskExecutor("WorkerTaskExecutor-");
	}

	private TaskExecutor createDemonTaskExecutor(String prefix){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(50);
		executor.setQueueCapacity(100);
		executor.setDaemon(true);
		executor.setThreadNamePrefix(prefix);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}
}
