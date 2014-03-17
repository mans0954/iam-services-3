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
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.springframework.stereotype.Repository;

@Repository("languageMappingDAO")
public class LanguageMappingDAOImpl extends BaseDaoImpl<LanguageMappingEntity, String> implements LanguageMappingDAO {
	
	
	
	@Override
	protected Criteria getExampleCriteria(final LanguageMappingEntity example) {
		final Criteria criteria = getCriteria();
		if(example != null) {
			if(StringUtils.isNotBlank(example.getId())) {
				criteria.add(Restrictions.eq(getPKfieldName(), example.getId()));
			} else {
				if(StringUtils.isNotBlank(example.getLanguageId())) {
					criteria.add(Restrictions.eq("languageId", example.getLanguageId()));
				}
				if(StringUtils.isNotBlank(example.getReferenceId())) {
					criteria.add(Restrictions.eq("referenceId", example.getReferenceId()));
				}
				if(StringUtils.isNotBlank(example.getReferenceType())) {
					criteria.add(Restrictions.eq("referenceType", example.getReferenceType()));
				}
				if(StringUtils.isNotBlank(example.getValue())) {
					criteria.add(Restrictions.eq("value", example.getValue()));
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
		final LanguageMappingEntity example = new LanguageMappingEntity();
		example.setReferenceId(referenceId);
		example.setReferenceType(referenceType);
		return getByExample(example);
	}
}
