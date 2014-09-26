package org.openiam.elasticsearch.service;

import org.openiam.core.dao.lucene.HibernateSearchDao;
import org.openiam.elasticsearch.factory.ESSearchDAOFactory;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by: Alexander Duckardt
 * Date: 9/19/14.
 */
@Service("elasticsearchReindexService")
public class ElasticsearchReindexServiceImpl implements ElasticsearchReindexService {

    @Autowired
    private ESSearchDAOFactory searchDAOFactory;

    @Override
    public void reindex(ElasticsearchReindexRequest reindexRequest) throws Exception {
        HibernateSearchDao searchDao = searchDAOFactory.getSearchDAO(reindexRequest.getEntityClass().getName());

        if(reindexRequest.isSaveOrUpdate()){
//            searchDao.updateIndecies(reindexRequest.getEntityList());
        } else if(reindexRequest.isDeleteRequest()){

        }
    }
}
