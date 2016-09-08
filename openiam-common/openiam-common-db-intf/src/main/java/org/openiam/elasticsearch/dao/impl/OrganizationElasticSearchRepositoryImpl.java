package org.openiam.elasticsearch.dao.impl;

import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.OrganizationElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<OrganizationEntity, String, OrganizationSearchBean> implements OrganizationElasticSearchRepositoryCustom {

	@Override
	protected CriteriaQuery getCriteria(OrganizationSearchBean searchBean) {
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
		}
		return query;
	}

	@Override
	protected Class<OrganizationEntity> getEntityClass() {
		return OrganizationEntity.class;
	}

}
