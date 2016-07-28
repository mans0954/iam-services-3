/**
 * 
 */
package org.openiam.mq.processor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Alexander Dukkardt
 * 
 */
public class BaseBackgroundProcessorService extends AbstractBackgroundProcessorService {

    public BaseBackgroundProcessorService(ThreadPoolTaskExecutor taskExecutor, ThreadPoolTaskExecutor workerTaskExecutor) {
        this.workerTaskExecutor = workerTaskExecutor;
        this.taskExecutor = taskExecutor;
    }

    public boolean isFinished() throws Exception {
        return ((ThreadPoolTaskExecutor) taskExecutor).getActiveCount() == 0;
    }
}
