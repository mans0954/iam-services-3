package org.openiam.elasticsearch.service;

import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;

/**
 * Created by: Alexander Duckardt
 * Date: 10/3/14.
 */
public interface ElasticsearchReindexProcessor  extends Runnable {
    public void pushToQueue(ElasticsearchReindexRequest reindexRequest);
}
