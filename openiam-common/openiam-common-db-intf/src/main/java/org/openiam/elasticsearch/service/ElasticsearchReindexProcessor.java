package org.openiam.elasticsearch.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;

/**
 * Created by: Alexander Duckardt
 * Date: 10/3/14.
 */
public interface ElasticsearchReindexProcessor  extends Runnable {
    void pushToQueue(ElasticsearchReindexRequest reindexRequest);
    int reindex(final Class<?> clazz);
    int reindex(final Class<?> entityClass, final Collection<String> ids);
    List<Class<?>> getIndexedClasses();
}
