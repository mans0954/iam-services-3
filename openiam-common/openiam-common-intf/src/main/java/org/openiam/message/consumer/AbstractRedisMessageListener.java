package org.openiam.message.consumer;

import org.apache.commons.lang.StringUtils;
import org.openiam.concurrent.OpenIAMSyncronizer;
import org.openiam.message.constants.OpenIAMAPI;
import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.dto.AbstractMQMessage;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.dto.OpenIAMMQResponse;
import org.openiam.message.gateway.ServiceGateway;
import org.openiam.message.processor.AbstractAPIProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

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
    @Autowired
    @Qualifier("workerTaskExecutor")
    private TaskExecutor workerTaskExecutor;

    @Value("${org.openiam.message.broker.polling.time}")
    private Long pollingTime;

    @Autowired
    @Qualifier("redisRequestServiceGateway")
    private ServiceGateway serviceGateway;

    private ConcurrentHashMap<OpenIAMAPI, AbstractAPIProcessor> workerMap = new ConcurrentHashMap<OpenIAMAPI, AbstractAPIProcessor>();

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
                log.info("MESSAGE Listener started on queue: {}", queueName);
                while(true){
                    log.info("MESSAGE Listener on queue '{}' is getting messages from queue", queueName);
                    // start pooling redis
                    final Long size = redisTemplate.opsForList().size(queueName);
                    if(size != null) {
                        for(long i = 0; i < size.intValue() ; i++) {
                            final Object key = redisTemplate.opsForList().rightPop(queueName);
                            if(key != null && (key.getClass().equals(messageClazz))) {
                                Message message = (Message)key;
                                log.debug("GOT MESSAGE in {} queue: {}", queueName, message);
                                onMessage(message);
                            }
                        }
                    }
                    log.info("MESSAGE Listener on queue '{}' is waiting", queueName);
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

    public TaskExecutor getWorkerTaskExecutor() {
        return workerTaskExecutor;
    }

    public void setWorkerTaskExecutor(TaskExecutor workerTaskExecutor) {
        this.workerTaskExecutor = workerTaskExecutor;
    }

    protected void addTask(AbstractAPIProcessor processor, Message message, OpenIAMAPI apiName,  boolean isAsync) throws Exception {
        if(isAsync){
            // get(or run it id necessary) worker by API name
            AbstractAPIProcessor currentWorker = workerMap.get(apiName);
            if(currentWorker==null || !currentWorker.isRunning()){
                currentWorker = (AbstractAPIProcessor)processor.cloneTask();
                workerMap.put(apiName, currentWorker);
                workerTaskExecutor.execute(currentWorker);
            }
            // add to queue
            currentWorker.pushToQueue(message);
        } else {
            final AbstractAPIProcessor task = (AbstractAPIProcessor)processor.cloneTask();
            workerTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    task.processRequest(message);
                }
            });
        }
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

    private  void  onMessage(Message message){
        try {
           doOnMessage(message);
        } catch (Exception e) {
            log.warn("Cannot process message now. pus it back to queue: {}", e);
            serviceGateway.send(this.getQueueToListen().getQueueName(), message);
        }
    }

    protected abstract void doOnMessage(Message message) throws Exception;
}
