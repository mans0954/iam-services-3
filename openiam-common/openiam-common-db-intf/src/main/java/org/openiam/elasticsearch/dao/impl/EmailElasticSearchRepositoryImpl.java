package org.openiam.elasticsearch.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.EmailElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

/* 
 * The name of the class is EXTREMELY important.
 * See  http://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#repositories.custom-implementations
 */
@Repository
public class EmailElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<EmailAddressEntity, String, EmailSearchBean> implements EmailElasticSearchRepositoryCustom {
	
	public EmailElasticSearchRepositoryImpl() {
		super();
	}

	@Override
	protected CriteriaQuery getCriteria(final EmailSearchBean searchBean) {
		CriteriaQuery query = null;
		if(searchBean != null) {
			final SearchParam param = searchBean.getEmailMatchToken();
			if(param != null && param.isValid()) {
				query = new CriteriaQuery(getWhereCriteria("emailAddress", param.getValue(), param.getMatchType()));
			}
		}
		return query;
	}

	@Override
	public Page<String> findUserIds(EmailSearchBean sb, Pageable pageable) {
		final CriteriaQuery criteria = getCriteria(sb);
		Page<EmailAddressEntity> page = null;
		if(criteria != null) {
			criteria.addFields("parent");
			criteria.setPageable(pageable);
			page = elasticSearchTemplate.queryForPage(criteria, EmailAddressEntity.class);
		} else {
			page = new PageImpl<EmailAddressEntity>(Collections.EMPTY_LIST);
		}
		final List<String> stringList = page.getContent().stream().map(e -> e.getParent().getId()).collect(Collectors.toList());
		return new PageImpl<String>(stringList, pageable, page.getTotalElements());
	}

	@Override
	protected Class<EmailAddressEntity> getEntityClass() {
		return EmailAddressEntity.class;
	}

}
