package org.openiam.elasticsearch.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.ws.MatchType;
import org.openiam.elasticsearch.dao.PhoneElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
public class PhoneElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<PhoneEntity, String, PhoneSearchBean> implements PhoneElasticSearchRepositoryCustom {

	public PhoneElasticSearchRepositoryImpl() {
		super();
	}

	@Override
	protected CriteriaQuery getCriteria(final PhoneSearchBean searchBean) {
		CriteriaQuery query = null;
		if(searchBean != null) {
			if(StringUtils.isNotBlank(searchBean.getPhoneAreaCd())) {
				final Criteria criteria = getWhereCriteria("areaCd", searchBean.getPhoneAreaCd(), MatchType.EXACT);
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
			if(StringUtils.isNotBlank(searchBean.getPhoneNbr())) {
				final Criteria criteria = getWhereCriteria("phoneNbr", searchBean.getPhoneNbr(), MatchType.STARTS_WITH);
				if(criteria != null) {
					query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
				}
			}
		}
		return query;
	}
	
	@Override
	public Page<String> findUserIds(final PhoneSearchBean searchBean, final Pageable pageable) {
		final CriteriaQuery criteria = getCriteria(searchBean);
		Page<PhoneEntity> page = null;
		if(criteria != null) {
			criteria.addFields("parent");
			criteria.setPageable(pageable);
			page = elasticSearchTemplate.queryForPage(criteria, PhoneEntity.class);
		} else {
			page = new PageImpl<PhoneEntity>(Collections.EMPTY_LIST);
		}
		final List<String> stringList = page.getContent().stream().map(e -> e.getParent().getId()).collect(Collectors.toList());
		return new PageImpl<String>(stringList, pageable, page.getTotalElements());
	}

	@Override
	public Class<PhoneEntity> getEntityClass() {
		return PhoneEntity.class;
	}

	@Override
	public void prepare(PhoneEntity entity) {
		// TODO Auto-generated method stub
		
	}

}
