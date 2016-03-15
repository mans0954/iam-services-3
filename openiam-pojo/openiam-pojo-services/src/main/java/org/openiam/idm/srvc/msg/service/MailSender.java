package org.openiam.idm.srvc.msg.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.RandomStringUtils;
import org.openiam.redis.RedisTaskScheduler;
import org.openiam.redis.TaskTriggerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component("mailSender")
public class MailSender implements TaskTriggerListener {

    private RedisTaskScheduler mailTaskScheduler;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedisMessageListenerContainer listener;
    
    @PostConstruct
    public void init() {
    	mailTaskScheduler = new RedisTaskScheduler();
    	mailTaskScheduler.setRedisTemplate(redisTemplate);
    	mailTaskScheduler.setTaskTriggerListener(this);
    }

    public void send(final Message mail) {
    	if(mail.getProcessingTime() != null) {
    		mailTaskScheduler.schedule(String.format("mailTask-%s", RandomStringUtils.randomAlphanumeric(10)), mail, mail.getProcessingTime());
    	} else {
    		redisTemplate.convertAndSend("mailQueue", mail);
    	}
    }

	@Override
	public void taskTriggered(String taskId, Object object) {
		redisTemplate.convertAndSend("mailQueue", (Message)object);
	}

}
