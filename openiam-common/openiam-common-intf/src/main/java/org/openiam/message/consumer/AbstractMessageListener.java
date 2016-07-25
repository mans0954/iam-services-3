package org.openiam.message.consumer;

import org.openiam.message.constants.OpenIAMAPI;
import org.openiam.message.constants.OpenIAMQueue;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.processor.AbstractAPIProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alexander on 07/07/16.
 */
public abstract class AbstractMessageListener<Message extends OpenIAMMQRequest> {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private OpenIAMQueue queueToListen;
    private boolean isInitialized=false;

    @Autowired
    @Qualifier("workerTaskExecutor")
    private TaskExecutor workerTaskExecutor;

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
    public OpenIAMQueue getQueueToListen() {
        return queueToListen;
    }
}
