package org.openiam.idm.srvc.msg.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component("mailSender")
public class MailSender {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    @Qualifier("scheduler")
    private ThreadPoolTaskScheduler scheduler;
    
    @PostConstruct
    public void init() {

    }

    public void send(final Message mail) {
    	if(mail.getProcessingTime() != null) {
    		scheduler.schedule(new ScheduledMail(mail), mail.getProcessingTime());
    	} else {
    		redisTemplate.opsForList().leftPush("mailQueue", mail);
    	}
    }
    
    private class ScheduledMail implements Runnable {
    	
    	Message mail;
    	
    	ScheduledMail(final Message mail) {
    		this.mail = mail;
    	}

		@Override
		public void run() {
			redisTemplate.opsForList().leftPush("mailQueue", mail);
		}
    	
    }
}
