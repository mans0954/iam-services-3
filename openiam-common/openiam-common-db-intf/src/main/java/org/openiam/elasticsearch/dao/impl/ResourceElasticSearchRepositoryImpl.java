package org.openiam.elasticsearch.dao.impl;

import org.openiam.elasticsearch.dao.ResourceElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<ResourceEntity, String, ResourceSearchBean> implements ResourceElasticSearchRepositoryCustom {

	@Override
	protected CriteriaQuery getCriteria(ResourceSearchBean searchBean) {
		throw new RuntimeException("Method not yet implemented");
	}

	@Override
	public Class<ResourceEntity> getDocumentClass() {
		return ResourceEntity.class;
	}

	@Override
	public void prepare(ResourceEntity entity) {
		// TODO Auto-generated method stub
		
	}

}
