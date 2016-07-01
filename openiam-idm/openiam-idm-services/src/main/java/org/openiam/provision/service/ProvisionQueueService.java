package org.openiam.provision.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component("provQueueService")
public class ProvisionQueueService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void enqueue(final ProvisionDataContainer data) {
    	redisTemplate.opsForList().leftPush("provQueue", data);
    }

    public void enqueue(final List<ProvisionDataContainer> dataList) {
        for (final ProvisionDataContainer data : dataList) {
            enqueue(data);
        }
    }
}
