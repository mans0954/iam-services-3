package org.openiam.elasticsearch.dao.impl;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.elasticsearch.common.netty.util.internal.ConcurrentHashMap;
import org.openiam.base.BaseIdentity;
import org.openiam.base.domain.KeyEntity;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.core.dao.BaseDao;
import org.openiam.elasticsearch.annotation.DocumentRepresentation;
import org.openiam.elasticsearch.annotation.EntityRepresentation;
import org.openiam.elasticsearch.converter.AbstractDocumentToEntityConverter;
import org.openiam.elasticsearch.dao.AbstractCustomElasticSearchRepository;
import org.openiam.elasticsearch.dao.OpeniamElasticSearchRepository;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.openiam.elasticsearch.service.ElasticsearchReindexProcessor;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.elasticsearch.annotations.Document;
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
	
	private Map<Class<?>, Class<?>> documentRepresentationClass = new ConcurrentHashMap<Class<?>, Class<?>>();
	
	private Map<Class<? extends KeyEntity>, BaseDao> daoMap = new HashMap<>();
	private Map<Class<? extends KeyEntity>, AbstractDocumentToEntityConverter> entity2DocConverterMap = new HashMap<>();
	private Map<Class<? extends KeyEntity>, Class<? extends BaseIdentity>> entity2DocumentClassMap = new HashMap<>();
	
	private Map<Class<?>, OpeniamElasticSearchRepository> documentRepositoryMap = new HashMap<Class<?>, OpeniamElasticSearchRepository>();
	private Map<Class<?>, AbstractCustomElasticSearchRepository> customDocumentRepositoryImplMap = new HashMap<>();
	
	public List<Class<?>> getIndexedClasses() {
		return new ArrayList<>(documentRepositoryMap.keySet());
	}
	
	@PostConstruct
	public void init() {
		ctx.getBeansOfType(AbstractDocumentToEntityConverter.class).forEach((beanName, bean) -> {
			entity2DocConverterMap.put(bean.getEntityClass(), bean);
			entity2DocumentClassMap.put(bean.getEntityClass(), bean.getDocumentClass());
		}); 
		
		ctx.getBeansOfType(BaseDao.class).forEach((beanName, bean) -> {
			daoMap.put(bean.getDomainClass(), bean);
		});
		
		ctx.getBeansOfType(AbstractCustomElasticSearchRepository.class).forEach((beanName, bean) -> {
			customDocumentRepositoryImplMap.put(bean.getDocumentClass(), bean);
		});
		
		ctx.getBeansOfType(OpeniamElasticSearchRepository.class).forEach((beanName, bean) -> {
			final Class<?> documentClass = bean.getDocumentClass();
			documentRepositoryMap.put(documentClass, bean);
			transactionTemplate.execute(new TransactionCallback<Void>() {

				@Override
				public Void doInTransaction(TransactionStatus arg0) {
					boolean reindex = false;
					if(customDocumentRepositoryImplMap.containsKey(documentClass)) {
						reindex = customDocumentRepositoryImplMap.get(documentClass).allowReindex(bean);
					}
					if(reindex) {
						reindex(documentClass);
					}
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
    	if(logger.isDebugEnabled()) {
    		logger.debug("Thread ID:" + Thread.currentThread().getId() + ". Thread name:" + Thread.currentThread().getName());
    	}
        try {
            while ((reindexRequest = pullFromQueue()) != null) {
                try {
                	if(logger.isDebugEnabled()) {
                		logger.debug(String.format("processing reindex request %s - starting", reindexRequest));
                	}
                    processingRequest(reindexRequest);
                    if(logger.isDebugEnabled()) {
                    	logger.debug(String.format("processing reindex request %s - finished", reindexRequest));
                    }
                } catch (Exception e) {
                	logger.error(e.getMessage(), e);
                }
                /*
                 * This was a copy/paste error.  Vitaly liked to do "multithreading" using Thread.sleep()...
                 * we are better than that.
                 */
                //Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private void processingRequest(ElasticsearchReindexRequest reindexRequest) throws Exception {
    	final Class<?> documentClass = getDocumentClass(reindexRequest.getEntityClass());
    	transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus arg0) {
		    	final OpeniamElasticSearchRepository repo = documentRepositoryMap.get(documentClass);
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
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("adding reindex request %s to queue - starting", reindexRequest));
		}
        requestQueue.add(reindexRequest);
        if(logger.isDebugEnabled()) {
        	logger.debug(String.format("adding reindex request %s to queue - finished", reindexRequest));
        }
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}
	
	@Transactional
	/* returns the number of reindexed items */
	public int reindex(final Class<?> documentClazz) {
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("Attempting to fully re-index: %s", documentClazz));
		}
		final OpeniamElasticSearchRepository repo = documentRepositoryMap.get(documentClazz);
		if(repo != null) {
			elasticSearchTemplate.deleteIndex(documentClazz);
			elasticSearchTemplate.createIndex(documentClazz);
			repo.deleteAll();
			
			Class<?> entityClass = documentClazz;
			if(documentClazz.isAnnotationPresent(EntityRepresentation.class)) {
				entityClass = documentClazz.getAnnotation(EntityRepresentation.class).value();
			}
			return reindex(entityClass, null);
		} else {
			throw new RuntimeException(String.format("No elastic search repo found for %s", documentClazz));
		}
	}
	
	private Class<?> getDocumentClass(final Class<?> entityClass) {
		return (entity2DocumentClassMap.containsKey(entityClass)) ? entity2DocumentClassMap.get(entityClass) : entityClass;
	}
	@Transactional
	public int reindex(final Class<?> entityClass, final Collection<String> ids) {
		if(ids != null) {
			if(logger.isDebugEnabled()) {
				logger.debug(String.format("Hibernate listener re-index request for %s", entityClass));
			}
		}
		final AbstractDocumentToEntityConverter converter = entity2DocConverterMap.get(entityClass);
		final Class<?> documentClass = getDocumentClass(entityClass);
		
		int numOfReindexedItems = 0;
		final int maxSize = 1000;
		final ElasticsearchRepository repo = documentRepositoryMap.get(documentClass);
		final BaseDao baseDAO = daoMap.get(entityClass);
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
        			//convert to *Doc form, if required
        			if(converter != null) {
        				if(logger.isDebugEnabled()) {
        					logger.debug(String.format("Converting %s to %s using dozer", converter.getEntityClass(), converter.getDocumentClass()));
        				}
        				if(list != null) {
        					list = list.stream().map(e -> converter.convertToDocument((BaseIdentity)e)).collect(Collectors.toList());
        				}
        			}
        			
        			if(CollectionUtils.isNotEmpty(list)) {
        				//if(CollectionUtils.isEmpty(ids)) {
        				if(customDocumentRepositoryImplMap.containsKey(documentClass)) {
        					list.forEach(e -> {
        						customDocumentRepositoryImplMap.get(documentClass).prepare((BaseIdentity)e);
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
