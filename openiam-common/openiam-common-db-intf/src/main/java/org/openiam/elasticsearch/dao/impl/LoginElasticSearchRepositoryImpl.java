package org.openiam.elasticsearch.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.LoginElasticSearchRepositoryCustom;
import org.openiam.elasticsearch.model.LoginDoc;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class LoginElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<LoginDoc, String, LoginSearchBean> implements LoginElasticSearchRepositoryCustom {

	public LoginElasticSearchRepositoryImpl() {
		super();
	}
	
	@Override
	protected CriteriaQuery getCriteria(final LoginSearchBean searchBean) {
		CriteriaQuery query = null;
		if(searchBean != null) {
			final SearchParam param = searchBean.getLoginMatchToken();
			if(param != null && param.isValid()) {
				final Criteria criteria = getWhereCriteria("login", param.getValue(), param.getMatchType());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			
			if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
				final Criteria criteria = exactCriteria("managedSysId", searchBean.getManagedSysId());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			if(StringUtils.isNotBlank(searchBean.getUserId())) {
				final Criteria criteria = exactCriteria("userId", searchBean.getUserId());
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
		}
		return query;
	}
	
	@Override
	public Page<String> findUserIds(final LoginSearchBean searchBean, final Pageable pageable) {
		final CriteriaQuery criteria = getCriteria(searchBean);
		Page<LoginDoc> page = null;
		if(criteria != null) {
			criteria.addFields("userId");
			criteria.setPageable(pageable);
			page = elasticSearchTemplate.queryForPage(criteria, LoginDoc.class);
		} else {
			page = new PageImpl<LoginDoc>(Collections.EMPTY_LIST);
		}
		final List<String> stringList = page.getContent().stream().map(e -> e.getUserId()).collect(Collectors.toList());
		return new PageImpl<String>(stringList, pageable, page.getTotalElements());
	}

	@Override
	public Class<LoginDoc> getDocumentClass() {
		return LoginDoc.class;
	}

	@Override
	public void prepare(LoginDoc entity) {
		// TODO Auto-generated method stub
		
	}
}
