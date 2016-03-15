package org.openiam.idm.srvc.msg.service;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Component("mailDispatcher")
public class MailDispatcher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedisMessageListenerContainer listener;
    
    @Autowired
    MailSenderClient mailSenderClient;
	
	@PostConstruct
    public void init() {
		listener.addMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
				final Message entity = (Message)redisTemplate.getDefaultSerializer().deserialize(message.getBody());
				mailSenderClient.send(entity);
			}
		}, Arrays.asList(new Topic[] { new ChannelTopic("mailQueue")}));
	}
}
