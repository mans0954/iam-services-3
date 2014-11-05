package org.openiam.elasticsearch.service;

import org.openiam.core.dao.lucene.HibernateSearchDao;
import org.openiam.elasticsearch.factory.ESSearchDAOFactory;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by: Alexander Duckardt
 * Date: 10/3/14.
 */
@Component
public class ElasticsearchReindexProcessorImpl implements ElasticsearchReindexProcessor{
    private final Logger log = LoggerFactory.getLogger(ElasticsearchReindexProcessorImpl.class);
    private BlockingQueue<ElasticsearchReindexRequest> requestQueue = new LinkedBlockingQueue<ElasticsearchReindexRequest>();
    private ElasticsearchReindexRequest reindexRequest;

    @Autowired
    private ESSearchDAOFactory searchDAOFactory;

    @Override
    public void pushToQueue(ElasticsearchReindexRequest reindexRequest) {
        log.debug("adding reindex request {} to queue - starting", reindexRequest);
        requestQueue.add(reindexRequest);
        log.debug("adding reindex request {} to queue - finished", reindexRequest);
    }

    private ElasticsearchReindexRequest pullFromQueue() throws InterruptedException {
        return requestQueue.take();
    }

    @Override
    public void run() {
        log.debug("Thread ID:" + Thread.currentThread().getId() + ". Thread name:" + Thread.currentThread().getName());
        try {
            while ((reindexRequest = pullFromQueue()) != null) {
                try {
                    log.debug("processing reindex request {} - starting", reindexRequest);
                    processingRequest(reindexRequest);
                    log.debug("processing reindex request {} - finished", reindexRequest);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processingRequest(ElasticsearchReindexRequest reindexRequest) throws Exception {
        HibernateSearchDao searchDao = searchDAOFactory.getSearchDAO(reindexRequest.getEntityClass().getName());

        if(reindexRequest.isSaveOrUpdate()){
            searchDao.updateIndecies(reindexRequest.getEntityIdList());
        } else if(reindexRequest.isDeleteRequest()){
            searchDao.deleteIndecies(reindexRequest.getEntityIdList());
        }
    }
}
