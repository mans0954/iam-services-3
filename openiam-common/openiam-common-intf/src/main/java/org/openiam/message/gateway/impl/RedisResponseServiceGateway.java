package org.openiam.message.gateway.impl;

import org.openiam.message.dto.OpenIAMMQResponse;
import org.openiam.message.gateway.AbstractResponseServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 07/07/16.
 */
@Component
public class RedisResponseServiceGateway extends AbstractResponseServiceGateway {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    protected void doSend(String queueName, OpenIAMMQResponse response) {
        redisTemplate.opsForList().leftPush(queueName, response);
    }
}
