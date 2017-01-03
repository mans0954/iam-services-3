package org.openiam.elasticsearch.dao.impl;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.OrganizationElasticSearchRepositoryCustom;
import org.openiam.elasticsearch.model.OrganizationDoc;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<OrganizationDoc, String, OrganizationSearchBean> implements OrganizationElasticSearchRepositoryCustom {
	
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
			if(StringUtils.isNotBlank(searchBean.getMetadataType())) {
				final Criteria criteria = eq("metadataTypeId", searchBean.getMetadataType());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			final Set<String> organizationTypeIdSet = searchBean.getOrganizationTypeIdSet();
			if(CollectionUtils.isNotEmpty(organizationTypeIdSet)) {
				final Criteria criteria = inCriteria("organizationTypeId", organizationTypeIdSet);
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			final Set<String> parentIds = searchBean.getParentIdSet();
			if(CollectionUtils.isNotEmpty(parentIds)) {
				Criteria subcriteria = null;
				for(final String parentId : parentIds) {
					final Criteria criteria = eq("parentIds", parentId);
					subcriteria = (subcriteria != null) ? subcriteria.or(criteria) : criteria;
				}
				if(subcriteria != null) {
					query = (query != null) ? query.addCriteria(subcriteria) : new CriteriaQuery(subcriteria);
				}
			}
			
			if(StringUtils.isNotBlank(searchBean.getValidParentTypeId())) {
				final Criteria criteria = eq("parentOrganizationTypeIds", searchBean.getValidParentTypeId());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
		}
		return query;
	}

	@Override
	public Class<OrganizationDoc> getDocumentClass() {
		return OrganizationDoc.class;
	}

	@Override
	public void prepare(OrganizationDoc entity) {
		// TODO Auto-generated method stub
		
	}

}
