package org.openiam.elasticsearch.service;

import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;

/**
 * Created by: Alexander Duckardt
 * Date: 9/19/14.
 */
public interface ElasticsearchReindexService {
    public void reindex(ElasticsearchReindexRequest reindexRequest) throws Exception;
}
