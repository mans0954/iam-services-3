package org.openiam.elasticsearch.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.GroupElasticSearchRepositoryCustom;
import org.openiam.elasticsearch.model.GroupDoc;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository("groupElasticSearchRepositoryImpl")
public class GroupElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<GroupDoc, String, GroupSearchBean> implements GroupElasticSearchRepositoryCustom {

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
			
			if(CollectionUtils.isNotEmpty(searchBean.getAttributes())) {
				Criteria subcriteria = getAttributeCriteria(searchBean.getAttributes());
				if(subcriteria != null) {
					query = (query != null) ? query.addCriteria(subcriteria) : new CriteriaQuery(subcriteria);
				}
			}
		}
		return query;
	}

	@Override
	public Class<GroupDoc> getDocumentClass() {
		return GroupDoc.class;
	}

	@Override
	public void prepare(GroupDoc entity) {
		// TODO Auto-generated method stub
		
	}

}
