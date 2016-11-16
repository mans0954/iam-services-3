package org.openiam.mq.listener;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.processor.AbstractAPIDispatcher;
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
public abstract class AbstractMessageListener<RequestBody extends BaseServiceRequest, API extends OpenIAMAPI> {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private MqQueue queueToListen;
    private boolean isInitialized=false;

    @Autowired
    @Qualifier("workerTaskExecutor")
    private TaskExecutor workerTaskExecutor;
    @Autowired
    private BaseBackgroundProcessorService baseBackgroundProcessorService;

    private ConcurrentHashMap<API, AbstractAPIDispatcher> workerMap = new ConcurrentHashMap<API, AbstractAPIDispatcher>();

    public AbstractMessageListener(MqQueue queueToListen){
        this.queueToListen=queueToListen;
    }

    public TaskExecutor getWorkerTaskExecutor() {
        return workerTaskExecutor;
    }

    public void setWorkerTaskExecutor(TaskExecutor workerTaskExecutor) {
        this.workerTaskExecutor = workerTaskExecutor;
    }

    protected void addTask(AbstractAPIDispatcher processor, byte[] correlationId, MQRequest<RequestBody, API> message, API apiName, boolean isAsync) throws RejectMessageException, CloneNotSupportedException  {
//        if(message.getCorrelationId()==null){
//            message.setCorrelationId(correlationId);
//        }
        baseBackgroundProcessorService.addTask(processor,message,apiName,isAsync);
    }
    public MqQueue getQueueToListen() {
        return queueToListen;
    }

    protected abstract void doOnMessage(MQRequest<RequestBody, API> message, byte[] correlationId, boolean isAsync) throws  RejectMessageException, CloneNotSupportedException ;
}
