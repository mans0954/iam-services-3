package org.openiam.mq.processor;

import org.openiam.concurrent.AbstractBaseRunnableBackgroundTask;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Dukkardt
 * 
 */
public abstract class AbstractBackgroundProcessorService {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected ThreadPoolTaskExecutor workerTaskExecutor;
    protected ThreadPoolTaskExecutor taskExecutor;

    private ConcurrentHashMap<OpenIAMAPI, AbstractAPIProcessor> workerMap = new ConcurrentHashMap<OpenIAMAPI, AbstractAPIProcessor>();

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                taskExecutor.shutdown();
                workerTaskExecutor.shutdown();
            }
        });
    }

    public void addTask(AbstractAPIProcessor processor, MQRequest message, OpenIAMAPI apiName, boolean isAsync)
            throws RejectMessageException, CloneNotSupportedException {
        if(isAsync){
            // get(or run if it is necessary) worker by API name
            AbstractAPIProcessor currentWorker = workerMap.get(apiName);
            if(currentWorker==null || !currentWorker.isRunning()){
                currentWorker = (AbstractAPIProcessor)processor.cloneTask();
                workerMap.put(apiName, currentWorker);
                log.info("Add async worker {}", processor.getClass().getName());
                workerTaskExecutor.execute(currentWorker);
            }
            // add to queue
            log.info("Add mq {} to the worker {}", message, processor.getClass().getName());
            currentWorker.pushToQueue(message);
        } else {
            if(!isFull()){
                log.info("Add task {}", processor.getClass().getName());
                final AbstractAPIProcessor task = (AbstractAPIProcessor)processor.cloneTask();
                taskExecutor.execute(new AbstractBaseRunnableBackgroundTask() {
                    @Override
                    public void run() {
                        task.processRequest(message);
                    }
                });
            } else {
                log.info("executor pool is full. Reject the mq {} and queued it for redelivery",message);
                throw new RejectMessageException();
            }
        }
    }

    public boolean isFull() {
        ThreadPoolTaskExecutor te = ((ThreadPoolTaskExecutor) taskExecutor);
        return te.getActiveCount() > te.getCorePoolSize();
    }

}
