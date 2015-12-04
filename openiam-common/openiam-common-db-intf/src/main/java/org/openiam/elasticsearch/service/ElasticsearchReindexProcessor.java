package org.openiam.elasticsearch.service;

import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;

/**
 * Created by: Alexander Duckardt
 * Date: 10/3/14.
 */
public interface ElasticsearchReindexProcessor  extends Runnable {
    void pushToQueue(ElasticsearchReindexRequest reindexRequest);
    public int reindex(final Class<?> clazz);
}
