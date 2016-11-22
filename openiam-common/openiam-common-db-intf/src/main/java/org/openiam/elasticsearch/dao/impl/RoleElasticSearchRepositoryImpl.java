package org.openiam.elasticsearch.dao.impl;

import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.RoleElasticSearchRepositoryCustom;
import org.openiam.elasticsearch.model.RoleDoc;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class RoleElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<RoleDoc, String, RoleSearchBean> implements RoleElasticSearchRepositoryCustom {

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
			
			if(StringUtils.isNotBlank(searchBean.getMetadataType())) {
				final Criteria criteria = eq("metadataTypeId", searchBean.getMetadataType());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
				final Criteria criteria = eq("managedSysId", searchBean.getManagedSysId());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
		}
		return query;
	}

	@Override
	public Class<RoleDoc> getDocumentClass() {
		return RoleDoc.class;
	}

	@Override
	public void prepare(RoleDoc entity) {
		// TODO Auto-generated method stub
		
	}

}
