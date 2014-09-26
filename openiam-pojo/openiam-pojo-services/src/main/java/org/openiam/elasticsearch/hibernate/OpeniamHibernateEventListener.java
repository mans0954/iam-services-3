package org.openiam.elasticsearch.hibernate;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.hibernate.SessionFactory;
import org.hibernate.event.spi.*;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.metadata.ClassMetadata;
import org.openiam.elasticsearch.annotation.ElasticsearchIndex;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.openiam.elasticsearch.service.ElasticsearchReindexService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private ExecutorService service;
    protected final long SHUTDOWN_TIME = 5000;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ElasticsearchReindexService elasticsearchReindexService;

    private Map<String, Class<?>> entityMapper = new HashMap<String, Class<?>>();

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if(event!=null && event.getEntity()!=null){
            Class<?> clazz =event.getEntity().getClass();
            if(isEntityMapped(clazz)){

                log.info(String.format("==== Hibernate Event: POST_COMMIT_DELETE for Entity: %s =====", event.getEntity().getClass().getSimpleName()));
            }
        }

    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if(event!=null && event.getEntity()!=null){
            Class<?> clazz =event.getEntity().getClass();
            if(isEntityMapped(clazz)){
                log.info(String.format("==== Hibernate Event: POST_COMMIT_INSERT for Entity: %s =====",  event.getEntity().getClass().getSimpleName()));
            }
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if(event!=null && event.getEntity()!=null){
            Class<?> clazz =event.getEntity().getClass();
            if(isEntityMapped(clazz)){
                log.info(String.format("==== Hibernate Event: POST_COMMIT_UPDATE for Entity: %s =====",  event.getEntity().getClass().getSimpleName()));
            }
        }

    }

    private void runReindexTask(final ElasticsearchReindexRequest request){
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

    private ElasticsearchReindexRequest buildSaveOrUpdateRequest(final JoinPoint joinpoint) throws Throwable {
        List<String> entityList = new ArrayList();
        Class<?> clazz = null;
        for(final Object obj : joinpoint.getArgs()) {
            if(obj instanceof Collection) {
                for(final Object o : (Collection)obj) {
                    if(clazz==null)
                        clazz=o.getClass();
                    addEntityId(entityList, o);
                }
            } else {
                clazz=obj.getClass();
                addEntityId(entityList, obj);
            }
        }
        return ElasticsearchReindexRequest.getUpdateReindexRequest(entityList,clazz);
    }

    private ElasticsearchReindexRequest buildDeleteRequest(final JoinPoint joinpoint) throws Throwable {
        List<String> entityList = new ArrayList();
        Class<?> clazz = null;
        for(final Object obj : joinpoint.getArgs()) {
            clazz=obj.getClass();
            addEntityId(entityList, obj);
        }
        return ElasticsearchReindexRequest.getDeleteReindexRequest(entityList, clazz);
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

    private boolean isEntityMapped(Class<?> clazz){
        return entityMapper.containsKey(clazz.getName());
    }

    private boolean isEntityIndexed(Class<?> clazz) {
        ElasticsearchIndex annotation =  clazz.getAnnotation(ElasticsearchIndex.class);
        if (annotation != null) {
            return true;
        }
        return false;
    }

    private void registerEntityHolder(Class<?> clazz) {
        entityMapper.put(clazz.getName(), clazz);
    }
}
