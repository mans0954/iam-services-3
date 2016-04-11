package org.openiam.elasticsearch.dao.impl;

import org.openiam.elasticsearch.dao.GroupElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class GroupElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<GroupEntity, String, GroupSearchBean> implements GroupElasticSearchRepositoryCustom {

	@Override
	protected CriteriaQuery getCriteria(GroupSearchBean searchBean) {
		throw new RuntimeException("Method not yet implemented");
	}

	@Override
	protected Class<GroupEntity> getEntityClass() {
		return GroupEntity.class;
	}

}
