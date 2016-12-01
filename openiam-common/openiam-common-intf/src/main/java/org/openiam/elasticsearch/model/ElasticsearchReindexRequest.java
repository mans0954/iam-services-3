package org.openiam.elasticsearch.model;

import org.openiam.base.request.BaseServiceRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 9/18/14.
 */
public class ElasticsearchReindexRequest extends BaseServiceRequest {
    private boolean saveOrUpdate = false;
    private boolean delete = false;
    private List<String> entityIdList;
    private Class<?> entityClass;

    private ElasticsearchReindexRequest(boolean saveOrUpdate, boolean delete, List<String> entityIdList, Class<?> entityClass){
        this.saveOrUpdate=saveOrUpdate;
        this.delete=delete;
        this.entityIdList=entityIdList;
        this.entityClass = entityClass;
    }

    public boolean isDeleteRequest(){
        return this.delete;
    }

    public boolean isSaveOrUpdate(){
        return this.saveOrUpdate;
    }

    public List<String> getEntityIdList(){
        return this.entityIdList;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public static ElasticsearchReindexRequest getDeleteReindexRequest(Class<?> entityClass){
        return getDeleteReindexRequest(null, entityClass);
    }
    public static ElasticsearchReindexRequest getDeleteReindexRequest(List<String> entityList, Class<?> entityClass){
        return new ElasticsearchReindexRequest(false, true, entityList, entityClass);
    }
    public static ElasticsearchReindexRequest getUpdateReindexRequest(Class<?> entityClass){
        return getUpdateReindexRequest(null, entityClass);
    }
    public static ElasticsearchReindexRequest getUpdateReindexRequest(List<String> entityList, Class<?> entityClass){
        return new ElasticsearchReindexRequest(true, false, entityList, entityClass);
    }

    public void addEntityId(String entityId){
        if(entityIdList==null)
            this.entityIdList = new ArrayList<>();
        this.entityIdList.add(entityId);
    }
}
