package org.openiam.elasticsearch.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.BaseIdentity;
import org.openiam.core.dao.BaseDao;
import org.openiam.elasticsearch.dao.OpeniamElasticSearchRepository;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.openiam.elasticsearch.service.ElasticsearchReindexProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class ElasticSearchReindexer implements ApplicationContextAware, ElasticsearchReindexProcessor {
	
	private BlockingQueue<ElasticsearchReindexRequest> requestQueue = new LinkedBlockingQueue<ElasticsearchReindexRequest>();
	private ElasticsearchReindexRequest reindexRequest;
	
	private final Log logger = LogFactory.getLog(this.getClass());
	private ApplicationContext ctx;
	
	@Autowired
	private ElasticsearchTemplate elasticSearchTemplate;
	
	@Autowired
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;
	
	private Map<Class<?>, OpeniamElasticSearchRepository> repoMap = new HashMap<Class<?>, OpeniamElasticSearchRepository>();
	private Map<Class<?>, BaseDao> daoMap = new HashMap<Class<?>, BaseDao>();
	private Map<Class<?>, AbstractElasticSearchRepository> customRepoImplMap = new HashMap<>();
	
	@PostConstruct
	public void init() {
		ctx.getBeansOfType(BaseDao.class).forEach((beanName, bean) -> {
			daoMap.put(bean.getDomainClass(), bean);
		});
		
		ctx.getBeansOfType(AbstractElasticSearchRepository.class).forEach((beanName, bean) -> {
			customRepoImplMap.put(bean.getEntityClass(), bean);
		});
		
		ctx.getBeansOfType(OpeniamElasticSearchRepository.class).forEach((beanName, bean) -> {
			final Class<?> entityClass = bean.getEntityClass();
			repoMap.put(entityClass, bean);
			transactionTemplate.execute(new TransactionCallback<Void>() {

				@Override
				public Void doInTransaction(TransactionStatus arg0) {
					//elasticSearchTemplate.deleteIndex(entityClass);
					//if(!elasticSearchTemplate.indexExists(entityClass)) {
					reindex(entityClass);
					//}
					return null;
				}
			});
		});
	}
	

	private ElasticsearchReindexRequest pullFromQueue() throws InterruptedException {
		return requestQueue.take();
	}

    @Override
    public void run() {
        logger.debug("Thread ID:" + Thread.currentThread().getId() + ". Thread name:" + Thread.currentThread().getName());
        try {
            while ((reindexRequest = pullFromQueue()) != null) {
                try {
                	logger.debug(String.format("processing reindex request %s - starting", reindexRequest));
                    processingRequest(reindexRequest);
                    logger.debug(String.format("processing reindex request %s - finished", reindexRequest));
                } catch (Exception e) {
                	logger.error(e.getMessage(), e);
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private void processingRequest(ElasticsearchReindexRequest reindexRequest) throws Exception {
    	transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus arg0) {
		    	final OpeniamElasticSearchRepository repo = repoMap.get(reindexRequest.getEntityClass());
		    	if(repo != null && CollectionUtils.isNotEmpty(reindexRequest.getEntityIdList())) {
			        if(reindexRequest.isSaveOrUpdate()){
			        	reindex(reindexRequest.getEntityClass(), reindexRequest.getEntityIdList());
			        } else if(reindexRequest.isDeleteRequest()){
			        	reindexRequest.getEntityIdList().forEach(id -> {
			        		repo.delete(id);
			        	});
			        }
		    	}
		    	return null;
			}
    	});
    }

	@Override
	public void pushToQueue(ElasticsearchReindexRequest reindexRequest) {
		logger.debug(String.format("adding reindex request %s to queue - starting", reindexRequest));
        requestQueue.add(reindexRequest);
        logger.debug(String.format("adding reindex request %s to queue - finished", reindexRequest));
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}
	
	@Transactional
	/* returns the number of reindexed items */
	public int reindex(final Class<?> clazz) {
		elasticSearchTemplate.deleteIndex(clazz);
		elasticSearchTemplate.createIndex(clazz);
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("Attempting to fully re-index: %s", clazz));
		}
		final OpeniamElasticSearchRepository repo = repoMap.get(clazz);
		if(repo != null) {
			boolean reindex = true;
			if(customRepoImplMap.containsKey(clazz)) {
				reindex = customRepoImplMap.get(clazz).allowReindex();
			}
			if(reindex) {
				repo.deleteAll();
				return reindex(clazz, null);
			} else {
				logger.warn(String.format("Reindex not allowed for %s", clazz));
				return 0;
			}
		} else {
			throw new RuntimeException(String.format("No elastic search repo found for %s", clazz));
		}
	}
	
	private int reindex(final Class<?> clazz, final Collection<String> ids) {
		if(ids != null) {
			if(logger.isDebugEnabled()) {
				logger.debug(String.format("Hibernate listener re-index request for %s", clazz));
			}
		}
		int numOfReindexedItems = 0;
		final int maxSize = 1000;
		final ElasticsearchRepository repo = repoMap.get(clazz);
		final BaseDao baseDAO = daoMap.get(clazz);
		if(repo != null && baseDAO != null) {
			for (int from = 0; ; from += maxSize) {
        		try {
        			final List<IndexQuery> queries = new LinkedList<IndexQuery>();
        			if(logger.isDebugEnabled()) {
        				logger.debug(String.format("Fetching from %s, size: %s", from, maxSize));
        			}
        			List<Object> list = null;
        			if(CollectionUtils.isNotEmpty(ids)) {
        				list = baseDAO.findByIds(ids, from, maxSize);
        			} else {
        				list = baseDAO.find(from, maxSize);
        			}
        			if(logger.isDebugEnabled()) {
        				logger.debug(String.format("Fetched from %s, size: %s.  Indexing...", from, maxSize));
        			}
        			if(CollectionUtils.isNotEmpty(list)) {
        				//if(CollectionUtils.isEmpty(ids)) {
        				if(customRepoImplMap.containsKey(clazz)) {
        					list.forEach(e -> {
        						customRepoImplMap.get(clazz).prepare((BaseIdentity)e);
        					});
        				}
        				repo.save(list); /* same as index */
        				//} else {
        					//repo.index(null);
        				//}
        			}
        			numOfReindexedItems += list.size();

        			if(logger.isDebugEnabled()) {
        				logger.debug(String.format("Fetched from %s, size: %s.  Done indexing... committing", from, maxSize));
        				logger.debug(String.format("Fetched from %s, size: %s.  Done indexing... committed", from, maxSize));
        			}
                	if (list.isEmpty() || list.size() < maxSize) {
                		break;
                	}
            	} catch (Exception e) {
            		logger.error("Can't index - rolling back", e);
            	}
        	}
		}
		return numOfReindexedItems;
	}
}
