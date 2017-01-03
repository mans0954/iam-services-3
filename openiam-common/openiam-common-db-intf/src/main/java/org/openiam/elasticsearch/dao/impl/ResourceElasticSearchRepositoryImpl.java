package org.openiam.elasticsearch.dao.impl;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.ResourceElasticSearchRepositoryCustom;
import org.openiam.elasticsearch.model.ResourceDoc;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.dto.ResourceRisk;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<ResourceDoc, String, ResourceSearchBean> implements ResourceElasticSearchRepositoryCustom {

	@Override
	protected CriteriaQuery getCriteria(ResourceSearchBean searchBean) {
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
			
			final Set<String> childIds = searchBean.getChildIdSet();
			if(CollectionUtils.isNotEmpty(childIds)) {
				Criteria subcriteria = null;
				for(final String childId : childIds) {
					final Criteria criteria = eq("childIds", childId);
					subcriteria = (subcriteria != null) ? subcriteria.or(criteria) : criteria;
				}
				if(subcriteria != null) {
					query = (query != null) ? query.addCriteria(subcriteria) : new CriteriaQuery(subcriteria);
				}
			}
			
			final Set<String> resourceTypeIds = searchBean.getResourceTypeIdSet();
			if(CollectionUtils.isNotEmpty(resourceTypeIds)) {
				final Criteria criteria = inCriteria("resourceTypeId", resourceTypeIds);
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			if(CollectionUtils.isNotEmpty(searchBean.getExcludeResourceTypes())) {
				Criteria subcriteria = null;
				for(final String typeId : searchBean.getExcludeResourceTypes()) {
					final Criteria criteria = neq("resourceTypeId", typeId);
					subcriteria = (subcriteria != null) ? subcriteria.and(criteria) : criteria;
				}
				if(subcriteria != null) {
					query = (query != null) ? query.addCriteria(subcriteria) : new CriteriaQuery(subcriteria);
				}
			}
			
			final ResourceRisk risk = searchBean.getRisk();
			if(risk != null) {
				final Criteria criteria = eq("risk", risk.getValue());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			if(CollectionUtils.isNotEmpty(searchBean.getAttributes())) {
				Criteria subcriteria = getAttributeCriteria(searchBean.getAttributes());
				if(subcriteria != null) {
					query = (query != null) ? query.addCriteria(subcriteria) : new CriteriaQuery(subcriteria);
				}
			}
			
			if(StringUtils.isNotBlank(searchBean.getMetadataType())) {
				final Criteria criteria = eq("metadataTypeId", searchBean.getMetadataType());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			if(searchBean.getRootsOnly() != null) {
				final Criteria criteria = eq("root", Boolean.TRUE.equals(searchBean.getRootsOnly()));
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			//searchBean.getGroupIdSet()
			//searchBean.getOrganizationIdSet()
			//searchBean.getRoleIdSet()
			//searchBean.getUserIdSet()
			
		}
		return query;
	}

	@Override
	public Class<ResourceDoc> getDocumentClass() {
		return ResourceDoc.class;
	}

	@Override
	public void prepare(ResourceDoc entity) {
		// TODO Auto-generated method stub
		
	}

}
