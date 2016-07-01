package org.openiam.idm.srvc.msg.service;

import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("mailDispatcher")
public class MailDispatcher implements Sweepable {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    MailSenderClient mailSenderClient;

	@Override
	@Scheduled(fixedRate=500, initialDelay=500)
	public void sweep() {
		final Long size = redisTemplate.opsForList().size("mailQueue");
		if(size != null) {
			for(long i = 0; i < size.intValue() ; i++) {
				final Object key = redisTemplate.opsForList().rightPop("mailQueue");
				if(key != null) {
					final Message entity = (Message)key;
					mailSenderClient.send(entity);
				}
			}
		}
	}
}
