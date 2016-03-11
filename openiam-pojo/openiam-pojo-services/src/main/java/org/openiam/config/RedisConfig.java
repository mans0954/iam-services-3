package org.openiam.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

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
		return template;
	}
	
	@Bean
	public RedisMessageListenerContainer messageListenerContainer() {
		final RedisMessageListenerContainer listener = new RedisMessageListenerContainer();
		listener.setConnectionFactory(jedisConnectionFactory());
		listener.setMessageListeners(new HashMap<MessageListener, Collection<? extends Topic>>());
		return listener;
	}
}
