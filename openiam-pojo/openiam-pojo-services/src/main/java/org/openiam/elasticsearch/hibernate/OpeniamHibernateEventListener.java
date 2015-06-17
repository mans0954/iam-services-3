package org.openiam.elasticsearch.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.event.spi.*;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.openiam.base.domain.KeyEntity;
import org.openiam.elasticsearch.annotation.ElasticsearchIndex;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.openiam.elasticsearch.service.ElasticsearchReindexService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Alexander Duckardt
 * Date: 9/25/14.
 */
@Component
public class OpeniamHibernateEventListener implements InitializingBean,
                                                      PostDeleteEventListener,
                                                      PostUpdateEventListener,
                                                      PostInsertEventListener {

    private static Logger log = Logger.getLogger(OpeniamHibernateEventListener.class);
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ElasticsearchReindexService elasticsearchReindexService;

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

    private void runReindexTask(EventType eventType, Object entity) {
        try {
            Class<?> clazz =entity.getClass();
            if(isEntityMapped(clazz) && (entity instanceof KeyEntity)){
                log.info(String.format("==== Hibernate Event: %s for Entity: %s =====",  eventType.eventName(), entity.getClass().getSimpleName()));
                ElasticsearchReindexRequest request = null;
                if(EventType.POST_COMMIT_INSERT.equals(eventType)
                        || EventType.POST_COMMIT_UPDATE.equals(eventType)){
                    request = ElasticsearchReindexRequest.getUpdateReindexRequest(entity.getClass());
                } else if(EventType.POST_COMMIT_DELETE.equals(eventType)){
                    request = ElasticsearchReindexRequest.getDeleteReindexRequest(entity.getClass());
                }

                if(request!=null){
                    request.addEntityId(((KeyEntity)entity).getId());
                    elasticsearchReindexService.reindex(request);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ClassMetadata> map =  sessionFactory.getAllClassMetadata();
        for(String entityName : map.keySet()){
            Class<?> clazz = ReflectHelper.classForName(entityName);
            if(isEntityIndexed(clazz)){
                registerEntityHolder(clazz);
            }
        }
    }

    private boolean isEntityMapped(Class<?> clazz){
        return entityMapper.containsKey(clazz.getName());
    }

    private boolean isEntityIndexed(Class<?> clazz) {
        ElasticsearchIndex annotation =  clazz.getAnnotation(ElasticsearchIndex.class);
        return annotation != null;
    }

    private void registerEntityHolder(Class<?> clazz) {
        entityMapper.put(clazz.getName(), clazz);
    }
}
