package org.openiam.elasticsearch.dao.impl;

import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.MetadataTypeElasticSearchRepositoryCustom;
import org.openiam.elasticsearch.model.MetadataTypeDoc;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class MetadataTypeElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<MetadataTypeDoc, String, MetadataTypeSearchBean> implements MetadataTypeElasticSearchRepositoryCustom {

	@Override
	public Class<MetadataTypeDoc> getDocumentClass() {
		return MetadataTypeDoc.class;
	}

	@Override
	public void prepare(MetadataTypeDoc entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected CriteriaQuery getCriteria(MetadataTypeSearchBean searchBean) {
		CriteriaQuery query = null;
		if(searchBean != null) {
			final SearchParam param = searchBean.getNameToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("name", param.getValue(), param.getMatchType());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			final MetadataTypeGrouping grouping = searchBean.getGrouping();
			if(grouping != null) {
				final Criteria criteria = getWhereCriteria("grouping", grouping.name(), MatchType.EXACT);
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
		}
		return query;
	}


}
