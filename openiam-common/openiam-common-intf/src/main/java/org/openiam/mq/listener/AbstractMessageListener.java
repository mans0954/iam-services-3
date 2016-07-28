package org.openiam.mq.listener;

import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.processor.AbstractAPIProcessor;
import org.openiam.mq.processor.BaseBackgroundProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alexander on 07/07/16.
 */
public abstract class AbstractMessageListener {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private OpenIAMQueue queueToListen;
    private boolean isInitialized=false;

    @Autowired
    @Qualifier("workerTaskExecutor")
    private TaskExecutor workerTaskExecutor;
    @Autowired
    private BaseBackgroundProcessorService baseBackgroundProcessorService;

    private ConcurrentHashMap<OpenIAMAPI, AbstractAPIProcessor> workerMap = new ConcurrentHashMap<OpenIAMAPI, AbstractAPIProcessor>();

    public AbstractMessageListener(OpenIAMQueue queueToListen){
        this.queueToListen=queueToListen;
    }

    public TaskExecutor getWorkerTaskExecutor() {
        return workerTaskExecutor;
    }

    public void setWorkerTaskExecutor(TaskExecutor workerTaskExecutor) {
        this.workerTaskExecutor = workerTaskExecutor;
    }

    protected void addTask(AbstractAPIProcessor processor, byte[] correlationId, MQRequest message, OpenIAMAPI apiName,  boolean isAsync) throws RejectMessageException, CloneNotSupportedException  {
        if(message.getCorrelationId()==null){
            message.setCorrelationId(correlationId);
        }
        baseBackgroundProcessorService.addTask(processor,message,apiName,isAsync);
    }
    public OpenIAMQueue getQueueToListen() {
        return queueToListen;
    }

    protected abstract void doOnMessage(MQRequest message, byte[] correlationId, boolean isAsync) throws  RejectMessageException, CloneNotSupportedException ;
}
