package org.openiam.idm.srvc.lang.service;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.dto.LanguageMappingSearchBean;
import org.springframework.stereotype.Repository;

@Repository("languageMappingDAO")
public class LanguageMappingDAOImpl extends BaseDaoImpl<LanguageMappingEntity, String> implements LanguageMappingDAO {
	
	
	
	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof LanguageMappingSearchBean) {
			final LanguageMappingSearchBean sb = (LanguageMappingSearchBean)searchBean;
			if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            } else {
				if(StringUtils.isNotBlank(sb.getLanguageId())) {
					criteria.add(Restrictions.eq("languageId", sb.getLanguageId()));
				}
				if(StringUtils.isNotBlank(sb.getReferenceId())) {
					criteria.add(Restrictions.eq("referenceId", sb.getReferenceId()));
				}
				if(StringUtils.isNotBlank(sb.getReferenceType())) {
					criteria.add(Restrictions.eq("referenceType", sb.getReferenceType()));
				}
				if(StringUtils.isNotBlank(sb.getValue())) {
					criteria.add(Restrictions.eq("value", sb.getValue()));
				}
			}
		}
		return criteria;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	public List<LanguageMappingEntity> getByReferenceIdAndType(final String referenceId, final String referenceType) {
		final LanguageMappingSearchBean sb = new LanguageMappingSearchBean();
		sb.setReferenceId(referenceId);
		sb.setReferenceType(referenceType);
		return getByExample(sb);
	}

	@Override
	public List<LanguageMappingEntity> getByReferenceIdsAndType(Collection<String> referenceIds, String referenceType) {
		final Criteria criteria = getCriteria();
		if(CollectionUtils.isNotEmpty(referenceIds)) {
			criteria.add(Restrictions.in("referenceId", referenceIds));
		}
		if(StringUtils.isNotBlank(referenceType)) {
			criteria.add(Restrictions.eq("referenceType", referenceType));
		}
		return criteria.list();
	}
}
