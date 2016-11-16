package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.idm.srvc.batch.dispatcher.*;
import org.openiam.mq.constants.BatchTaskAPI;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 25/10/16.
 */
@Component
public class BatchTaskListener extends AbstractRabbitMQListener<BatchTaskAPI> {
    GetBatchTaskDispatcher  getBatchTaskDispatcher;
    GetSchedulesForTaskDispatcher getSchedulesForTaskDispatcher;
    GetNumSchedulesForTaskDispatcher getNumSchedulesForTaskDispatcher;
    RunTaskDispatcher runTaskDispatcher;
    DeleteTaskDispatcher deleteTaskDispatcher;
    SaveTaskDispatcher saveTaskDispatcher;
    FindBeansDispatcher findBeansDispatcher;
    CountBatchTaskDispatcher countBatchTaskDispatcher;

    public BatchTaskListener() {
        super(OpenIAMQueue.BatchTaskQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, BatchTaskAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        BatchTaskAPI apiName = message.getRequestApi();
        switch (apiName){
            case GetBatchTask:
                addTask(getBatchTaskDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetSchedulesForTask:
                addTask(getSchedulesForTaskDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetNumOfSchedulesForTask:
                addTask(getNumSchedulesForTaskDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Run:
            case Schedule:
                addTask(runTaskDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Save:
                addTask(saveTaskDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Delete:
            case DeleteScheduledTask:
                addTask(deleteTaskDispatcher, correlationId, message, apiName, isAsync);
                break;
            case FindBeans:
                addTask(findBeansDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Count:
                addTask(countBatchTaskDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
