package org.openiam.elasticsearch.service;

import org.apache.log4j.Logger;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Alexander Duckardt
 * Date: 9/19/14.
 */
@Service("elasticsearchReindexService")
public class ElasticsearchReindexServiceImpl implements InitializingBean, ElasticsearchReindexService {
    private static Logger log = Logger.getLogger(ElasticsearchReindexServiceImpl.class);
    protected final long SHUTDOWN_TIME = 5000;
    private ExecutorService service;
    @Autowired
    private ElasticsearchReindexProcessor reindexProcessor;

    @Override
    public void reindex(ElasticsearchReindexRequest reindexRequest) throws Exception {
        if(reindexRequest!=null)
            reindexProcessor.pushToQueue(reindexRequest);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        service = Executors.newCachedThreadPool();
        service.submit(reindexProcessor);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                service.shutdown();
                try {
                    if (!service.awaitTermination(SHUTDOWN_TIME, TimeUnit.MILLISECONDS)) { //optional *
                        log.warn("Executor did not terminate in the specified time."); //optional *
                        List<Runnable> droppedTasks = service.shutdownNow(); //optional **
                        log.warn("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed."); //optional **
                    }
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
        });

    }
}
