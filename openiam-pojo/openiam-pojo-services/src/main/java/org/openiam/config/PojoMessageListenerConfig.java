package org.openiam.config;

import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.consumer.AbstractMessageListener;
import org.openiam.message.consumer.AbstractRedisMessageListener;
import org.openiam.mq.MetaDataListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by alexander on 12/07/16.
 */
@Configuration
public class PojoMessageListenerConfig {

//        @Bean
//        @Autowired
//        public AbstractMessageListener metaDataListener(TaskExecutor listenerTaskExecutor, TaskExecutor workerTaskExecutor, RedisTemplate redisTemplate){
//            AbstractRedisMessageListener listener = new MetaDataListener(OpenIAMQueue.MetaElementQueue);
//            listener.setListenerTaskExecutor(listenerTaskExecutor);
//            listener.setWorkerTaskExecutor(workerTaskExecutor);
//            listener.setRedisTemplate(redisTemplate);
//            return listener;
//        }
}
