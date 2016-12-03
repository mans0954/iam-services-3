package org.openiam.elasticsearch.hibernate;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.openiam.base.domain.KeyEntity;
import org.openiam.elasticsearch.annotation.DocumentRepresentation;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.openiam.elasticsearch.service.ElasticsearchReindexService;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrgToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.mq.constants.api.EsAPI;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.constants.queue.common.EsReindexQueue;
import org.openiam.mq.utils.RabbitMQSender;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

/**
 * Created by: Alexander Duckardt
 * Date: 9/25/14.
 */
@Component
public class OpeniamHibernateEventListener implements InitializingBean,
                                                      PostDeleteEventListener,
                                                      PostUpdateEventListener,
                                                      PostInsertEventListener {

	private static final Log log = LogFactory.getLog(OpeniamHibernateEventListener.class);
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ElasticsearchReindexService elasticsearchReindexService;
    
    @Autowired
    protected RabbitMQSender rabbitMQSender;
    
    @Autowired
    private EsReindexQueue rabbitMqQueue;

    private Map<String, Class<?>> entityMapper = new HashMap<String, Class<?>>();

    @Override
    //TODO check implementation
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if(event!=null && event.getEntity()!=null){
            runReindexTask(EventType.POST_COMMIT_DELETE, event.getEntity());
        }
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if(event!=null && event.getEntity()!=null){
            runReindexTask(EventType.POST_COMMIT_INSERT, event.getEntity());
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if(event!=null && event.getEntity()!=null){
            runReindexTask(EventType.POST_COMMIT_UPDATE, event.getEntity());
        }
    }
    
    private ElasticsearchReindexRequest createUpdateReindexRequest(final KeyEntity entity, final Class<? extends KeyEntity> clazz) {
    	return ElasticsearchReindexRequest.getUpdateReindexRequest(clazz);
    }

    private void runReindexTask(EventType eventType, Object entity) {
        try {
            Class<?> clazz =entity.getClass();
            if(isEntityMapped(clazz) && (entity instanceof KeyEntity)){
                log.info(String.format("==== Hibernate Event: %s for Entity: %s =====",  eventType.eventName(), entity.getClass().getSimpleName()));
                final List<ElasticsearchReindexRequest> requestList = new LinkedList<ElasticsearchReindexRequest>();
                
                if(entity instanceof AbstractMembershipXrefEntity) {
                	final List<ElasticsearchReindexRequest> delayedRequestList = new LinkedList<ElasticsearchReindexRequest>();
                	final AbstractMembershipXrefEntity xref = (AbstractMembershipXrefEntity)entity;
                	if(xref.getMemberEntity() != null) {
                		ElasticsearchReindexRequest request = ElasticsearchReindexRequest.getUpdateReindexRequest(xref.getMemberClass());
                		request.addEntityId(xref.getMemberEntity().getId());
                		delayedRequestList.add(request);
                	}
                	if(xref.getEntity() != null) {
                		ElasticsearchReindexRequest request = ElasticsearchReindexRequest.getUpdateReindexRequest(xref.getEntityClass());
                		request.addEntityId(xref.getEntity().getId());
                		delayedRequestList.add(request);
                	}
                	if(xref.getStartDate() == null && xref.getEndDate() == null) {
                		requestList.addAll(delayedRequestList);
                	} else {
                		final Date now = new Date();
                		delayedRequestList.forEach(reindexRequest -> {
                			if(xref.getStartDate() != null) {
                				final Long delayMillis = Long.valueOf(xref.getStartDate().getTime() - now.getTime());
                				//System.out.println(String.format("Start date: Delay MS: %s", delayMillis));
                    			rabbitMQSender.schedule(rabbitMqQueue, EsAPI.Reindex, delayMillis, reindexRequest);
                    		}
                			if(xref.getEndDate() != null) {
                				final Long delayMillis = Long.valueOf(xref.getEndDate().getTime() - now.getTime());
                				//System.out.println(String.format("End date: Delay MS: %s", delayMillis));
                				rabbitMQSender.schedule(rabbitMqQueue, EsAPI.Reindex, delayMillis, reindexRequest);
                			}
                		});
                	}
                }
                	
                ElasticsearchReindexRequest request = null;
                if(EventType.POST_COMMIT_INSERT.equals(eventType) || EventType.POST_COMMIT_UPDATE.equals(eventType)){
                	request = ElasticsearchReindexRequest.getUpdateReindexRequest(entity.getClass());
                } else if(EventType.POST_COMMIT_DELETE.equals(eventType)){
                	request = ElasticsearchReindexRequest.getDeleteReindexRequest(entity.getClass());
                }
                request.addEntityId(((KeyEntity)entity).getId());
                requestList.add(request);
                
                if(CollectionUtils.isNotEmpty(requestList)) {
                	for(final ElasticsearchReindexRequest req : requestList) {
                		elasticsearchReindexService.reindex(req);
                	}
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Map<String, ClassMetadata> map =  sessionFactory.getAllClassMetadata();
        for(final String entityName : map.keySet()){
        	final Class<?> clazz = ReflectHelper.classForName(entityName);
            if(isEntityIndexed(clazz)){
                registerEntityHolder(clazz);
            }
        }
    }

    private boolean isEntityMapped(Class<?> clazz){
        return entityMapper.containsKey(clazz.getName());
    }

    private boolean isEntityIndexed(Class<?> clazz) {
    	return clazz.isAnnotationPresent(Document.class) || clazz.isAnnotationPresent(DocumentRepresentation.class);
    }

    private void registerEntityHolder(Class<?> clazz) {
        entityMapper.put(clazz.getName(), clazz);
    }
}