package org.openiam.message.gateway.impl;

import org.openiam.concurrent.OpenIAMSyncronizer;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.dto.OpenIAMMQResponse;
import org.openiam.message.gateway.AbstractRequestServiceGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexander on 06/07/16.
 */
@Component("redisRequestServiceGateway")
public class RedisRequestServiceGateway extends AbstractRequestServiceGateway {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Value("${org.openiam.message.broker.reply.timeout}")
    private Long replyTimeout;
    @Value("${org.openiam.message.broker.reply.polling.time}")
    private Long pollingTime;

    @Override
    public void doSend(String queueName, final OpenIAMMQRequest request) {
        redisTemplate.opsForList().leftPush(queueName, request);
    }

    @Override
    protected OpenIAMMQResponse doSendAndReceive(String queueName, final OpenIAMMQRequest request) {
        //this is sync message and has to be processed asap
        request.setReplyTo(getReplyQuequeName(queueName));
        request.setCorrelationID(this.generateCorrelationId());
        final SynchronousQueue<OpenIAMMQResponse> replyHandoff = new SynchronousQueue<OpenIAMMQResponse>();
        SynchrousListener listener = new SynchrousListener(request, replyHandoff);
        taskExecutor.execute(listener);

        redisTemplate.opsForList().rightPush(queueName, request);
        OpenIAMMQResponse reply = null;
        try {
            reply = (replyTimeout < 0) ? replyHandoff.take() : replyHandoff.poll(replyTimeout, TimeUnit.MILLISECONDS);
            listener.stop();
        } catch (InterruptedException e) {
            log.warn(
                    "No messages received during reply timeout. Callback queue: {} CANCEL CONSUMER",
                    request.getReplyTo());
        }
        return reply;
    }

    protected String getReplyQuequeName(String queueName) {
        String callbackName = queueName+".callback." + System.currentTimeMillis()
                + UUID.randomUUID().toString();
        return callbackName;
    }

    private class SynchrousListener implements Runnable{
        private OpenIAMSyncronizer monitor = new OpenIAMSyncronizer();

        private boolean isStopped = false;
        private SynchronousQueue<OpenIAMMQResponse> replyHandoff;
        private OpenIAMMQRequest request;

        public SynchrousListener(OpenIAMMQRequest request, SynchronousQueue<OpenIAMMQResponse> replyHandoff){
            this.replyHandoff=replyHandoff;
            this.request=request;
        }

        @Override
        public void run() {
            while(!isStopped){
                // start pooling redis
                final Long size = redisTemplate.opsForList().size(request.getReplyTo());
                if(size != null) {
                    for(long i = 0; i < size.intValue() ; i++) {
                        final Object key = redisTemplate.opsForList().rightPop(request.getReplyTo());
                        if(key != null && (key instanceof OpenIAMMQResponse)) {
                            OpenIAMMQResponse response = (OpenIAMMQResponse)key;
                            log.debug("GOT MESSAGE in {} queue: {}", request.getReplyTo(), response);
                            if(response.getCorrelationID().equals(request.getCorrelationID())){
                                try {
                                    replyHandoff.put(response);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                    }
                }
                monitor.doWait(pollingTime);
//                    try {
//                        Thread.sleep(pollingTime);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
            }
        }
        protected synchronized void stop(){
            isStopped=true;
        }
    }
}
