package org.openiam.elasticsearch.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.dao.EmailElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Criteria;
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
				String emailUsername = null;
				String emailDomain = null;
				final String[] value = StringUtils.split(param.getValue(), "@");
				if(value == null || value.length == 0) {
					emailUsername = StringUtils.trimToNull(param.getValue());
				} else {
					emailUsername = StringUtils.trimToNull(value[0]);
					if(value.length > 1) {
						emailDomain = StringUtils.trimToNull(value[1]);
					}
				}
				if(emailUsername != null) {
					final Criteria criteria = getWhereCriteria("emailUsername", emailUsername, param.getMatchType());
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
				if(emailDomain != null) {
					final Criteria criteria = startsWith("emailDomain", emailDomain);
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
		}
		return query;
	}
	
	@Override
	public void prepare(final EmailAddressEntity entity) {
		if(entity != null) {
			if(entity.getEmailAddress() != null) {
				final String[] emailSplit = StringUtils.split(entity.getEmailAddress(), "@");
				if(emailSplit != null && emailSplit.length > 0) {
					entity.setEmailUsername(StringUtils.trimToNull(emailSplit[0]));
					if(emailSplit.length > 1) {
						entity.setEmailDomain(StringUtils.trimToNull(emailSplit[1]));
					}
				}
			}
		}
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
	public Class<EmailAddressEntity> getEntityClass() {
		return EmailAddressEntity.class;
	}

}
