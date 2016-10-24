package org.openiam.elasticsearch.dao.impl;

import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.GroupElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository("gropuElasticSearchRepositoryImpl")
public class GroupElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<GroupEntity, String, GroupSearchBean> implements GroupElasticSearchRepositoryCustom {

	@Override
	protected CriteriaQuery getCriteria(GroupSearchBean searchBean) {
		CriteriaQuery query = null;
		if(searchBean != null) {
			SearchParam param = null;
			
			param = searchBean.getNameToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("name", param.getValue(), param.getMatchType());
				if(criteria != null) {
					query = new CriteriaQuery(criteria);
				}
			}
			
			Criteria criteria = exactCriteria("managedSysId", searchBean.getManagedSysId());
			if(criteria != null) {
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
		}
		return query;
	}

	@Override
	public Class<GroupEntity> getEntityClass() {
		return GroupEntity.class;
	}

	@Override
	public void prepare(GroupEntity entity) {
		// TODO Auto-generated method stub
		
	}

}
