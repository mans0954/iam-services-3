package org.openiam.elasticsearch.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.openiam.elasticsearch.annotation.ElasticsearchIndex;
import org.openiam.elasticsearch.annotation.ElasticsearchReindexOperation;
import org.openiam.elasticsearch.model.ElasticsearchFieldMetadata;
import org.openiam.elasticsearch.model.ElasticsearchMetadata;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 9/18/14.
 */
@Aspect
@Component
public class ElasticsearchJoinpointProvider implements InitializingBean, ApplicationContextAware {
    private static Logger LOG = Logger.getLogger(ElasticsearchJoinpointProvider.class);

    private ApplicationContext ctx;

    @Autowired
    private ElasticsearchSender elasticsearchSender;
    @Autowired
    private ElasticsearchProvider elasticsearchProvider;

    @AfterReturning(value="@annotation(org.openiam.elasticsearch.annotation.ElasticsearchReindexOperation)")
    public void afterReindexCRUD(final JoinPoint joinpoint) throws Throwable {
        final String methodName = joinpoint.getSignature().getName();
        final MethodSignature methodSignature = (MethodSignature)joinpoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            method = joinpoint.getTarget().getClass().getMethod(methodName, method.getParameterTypes());
        }
        final ElasticsearchReindexOperation annotation = method.getAnnotation(ElasticsearchReindexOperation.class);
        if(annotation == null) {
            throw new IllegalStateException(String.format("Method does not have %s annotation", ElasticsearchReindexOperation.class.getCanonicalName()));
        }
        ElasticsearchReindexRequest reindexRequest=null;
        if(annotation.saveOrUpdate()) {
            reindexRequest=buildSaveOrUpdateRequest(joinpoint);
        } else if(annotation.delete()) {
            reindexRequest=buildDeleteRequest(joinpoint);
        } else {
            throw new IllegalStateException(String.format("Method has %s annotation, but no flag is set", ElasticsearchReindexOperation.class.getCanonicalName()));
        }

        if(reindexRequest!=null && CollectionUtils.isNotEmpty(reindexRequest.getEntityList())){
            elasticsearchSender.send(reindexRequest);
        }
    }

    private void checkArguments(final JoinPoint joinpoint) throws Throwable {
        if(joinpoint.getArgs() == null || joinpoint.getArgs().length == 0) {
            throw new IllegalStateException("No arguments specified on joinpoint");
        }
    }

    private ElasticsearchReindexRequest buildSaveOrUpdateRequest(final JoinPoint joinpoint) throws Throwable {
        checkArguments(joinpoint);
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
        checkArguments(joinpoint);
        List<String> entityList = new ArrayList();
        Class<?> clazz = null;
        for(final Object obj : joinpoint.getArgs()) {
            clazz=obj.getClass();
            addEntityId(entityList, obj);
        }
        return ElasticsearchReindexRequest.getDeleteReindexRequest(entityList, clazz);
    }

    private void addEntityId(List<String> entityList, Object o) throws Exception {
        if(checkObject(o)){
            String id=getEntityId(o);
            if(StringUtils.isNotBlank(id))
                entityList.add(id);
        }
    }

    private String getEntityId(Object o) throws Exception {
        ElasticsearchMetadata metadata = elasticsearchProvider.getIndexMetadata(o.getClass());
        String id=null;
        if(CollectionUtils.isNotEmpty(metadata.getIndexedFields())){
            for(ElasticsearchFieldMetadata md : metadata.getIndexedFields()){
                if(md.isId()){
                    id = (String)md.getField().get(o);
                    break;
                }
            }

        }
        return id;
    }

    private boolean checkObject(Object o) {
        Class clazz = o.getClass();
        ElasticsearchIndex annotation = (ElasticsearchIndex) clazz.getAnnotation(ElasticsearchIndex.class);
        return annotation!=null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
