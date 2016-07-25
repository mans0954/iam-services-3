package org.openiam.message.consumer;

import org.openiam.concurrent.OpenIAMSyncronizer;
import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.gateway.AbstractRequestServiceGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by alexander on 07/07/16.
 */
public abstract class AbstractRedisMessageListener<Message extends OpenIAMMQRequest> extends AbstractMessageListener {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    @Qualifier("listenerTaskExecutor")
    private TaskExecutor listenerTaskExecutor;


    @Value("${org.openiam.message.broker.polling.time}")
    private Long pollingTime;

    @Autowired
    @Qualifier("redisRequestServiceGateway")
    private AbstractRequestServiceGateway serviceGateway;



    public AbstractRedisMessageListener(OpenIAMQueue queueToListen) {
        super(queueToListen);
    }

    @Override
    protected void doStart() {
        final String queueName = this.getQueueToListen().getQueueName();
        final Class<Message> messageClazz = getMessageType();
        listenerTaskExecutor.execute(new Runnable() {
            private OpenIAMSyncronizer monitor = new OpenIAMSyncronizer();
            @Override
            public void run() {
                log.debug("MESSAGE Listener started on queue: {}", queueName);
                while(true){
//                    log.debug("MESSAGE Listener on queue '{}' is getting messages from queue", queueName);
                    // start pooling redis
                    final Long size = redisTemplate.opsForList().size(queueName);
                    if(size != null) {
                        for(long i = 0; i < size.intValue() ; i++) {
                            final Object key = redisTemplate.opsForList().rightPop(queueName);
                            if(key != null && (key.getClass().equals(messageClazz))) {
                                Message message = (Message)key;
                                log.debug("GOT MESSAGE in {} queue: {}", queueName, message);
//                                onMessage(message);
                            }
                        }
                    }
//                    log.debug("MESSAGE Listener on queue '{}' is waiting", queueName);
                    monitor.doWait(pollingTime);
//                    try {
//                        Thread.sleep(pollingTime);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public TaskExecutor getListenerTaskExecutor() {
        return listenerTaskExecutor;
    }

    public void setListenerTaskExecutor(TaskExecutor listenerTaskExecutor) {
        this.listenerTaskExecutor = listenerTaskExecutor;
    }



    private Class<Message> getMessageType()   {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        Class<Message> result = null;
        if (paramType.getActualTypeArguments()[paramType
                .getActualTypeArguments().length - 1] instanceof Class) {
            result = (Class<Message>) paramType.getActualTypeArguments()[paramType
                    .getActualTypeArguments().length - 1];

        } else if (paramType.getActualTypeArguments()[paramType
                .getActualTypeArguments().length - 1] instanceof ParameterizedType) {
            result = (Class<Message>) ((ParameterizedType) paramType
                    .getActualTypeArguments()[paramType
                    .getActualTypeArguments().length - 1]).getRawType();
        }
        return result;
    }


}
