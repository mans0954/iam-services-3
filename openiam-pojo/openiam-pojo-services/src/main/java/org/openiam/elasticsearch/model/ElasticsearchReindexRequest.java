package org.openiam.elasticsearch.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 9/18/14.
 */
public class ElasticsearchReindexRequest implements Serializable {
    private boolean saveOrUpdate = false;
    private boolean delete = false;
    private List<String> entityList;
    private Class<?> entityClass;

    private ElasticsearchReindexRequest(boolean saveOrUpdate, boolean delete, List<String> entityList, Class<?> entityClass){
        this.saveOrUpdate=saveOrUpdate;
        this.delete=delete;
        this.entityList=entityList;
        this.entityClass = entityClass;
    }

    public boolean isDeleteRequest(){
        return this.delete;
    }

    public boolean isSaveOrUpdate(){
        return this.saveOrUpdate;
    }

    public List<String> getEntityList(){
        return this.entityList;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public static ElasticsearchReindexRequest getDeleteReindexRequest(List<String> entityList, Class<?> entityClass){
        return new ElasticsearchReindexRequest(false, true, entityList, entityClass);
    }

    public static ElasticsearchReindexRequest getUpdateReindexRequest(List<String> entityList, Class<?> entityClass){
        return new ElasticsearchReindexRequest(true, false, entityList, entityClass);
    }
}
