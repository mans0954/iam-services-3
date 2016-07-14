package org.openiam.message.gateway;

import org.openiam.message.dto.OpenIAMMQResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by alexander on 07/07/16.
 */
@Service
public class RedisResponseServiceGateway extends AbstractResponseServiceGateway {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    protected void doSend(String queueName, OpenIAMMQResponse response) {
        redisTemplate.opsForList().leftPush(queueName, response);
    }
}
