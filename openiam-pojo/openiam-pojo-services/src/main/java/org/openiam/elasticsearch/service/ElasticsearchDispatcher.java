package org.openiam.elasticsearch.service;

import org.apache.log4j.Logger;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Alexander Duckardt
 * Date: 9/19/14.
 */
@Component("elasticsearchDispatcher")
public class ElasticsearchDispatcher  implements InitializingBean, SessionAwareMessageListener {
    private static Logger log = Logger.getLogger(ElasticsearchDispatcher.class);
    protected final long SHUTDOWN_TIME = 5000;

    @Autowired
    ElasticsearchReindexService elasticsearchReindexService;

    private ExecutorService service;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        final ElasticsearchReindexRequest request = (ElasticsearchReindexRequest)((ObjectMessage)message).getObject();
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    elasticsearchReindexService.reindex(request);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        service = Executors.newCachedThreadPool();
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
