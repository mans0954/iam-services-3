package org.openiam.elasticsearch.dao.impl;

import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.RoleElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class RoleElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<RoleEntity, String, RoleSearchBean> implements RoleElasticSearchRepositoryCustom {

	@Override
	protected CriteriaQuery getCriteria(RoleSearchBean searchBean) {
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
	protected Class<RoleEntity> getEntityClass() {
		return RoleEntity.class;
	}

}
