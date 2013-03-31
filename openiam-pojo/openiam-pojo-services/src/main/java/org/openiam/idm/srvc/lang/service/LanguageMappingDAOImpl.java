package org.openiam.idm.srvc.lang.service;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.springframework.stereotype.Repository;

@Repository("languageMappingDAO")
public class LanguageMappingDAOImpl extends BaseDaoImpl<LanguageMappingEntity, String> implements LanguageMappingDAO {
	
	private static String DELETE_BY_REFERENCE_IDS_AND_TYPE = "DELETE FROM %s le WHERE le.referenceType = :referenceType AND le.referenceId IN (:referenceIds)";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_REFERENCE_IDS_AND_TYPE = String.format(DELETE_BY_REFERENCE_IDS_AND_TYPE, domainClass.getSimpleName());
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	public void deleteByReferenceTypeAndIds(final Collection<String> referenceIds, final String referenceType) {
		if(CollectionUtils.isNotEmpty(referenceIds)) {
			final Query query = getSession().createQuery(DELETE_BY_REFERENCE_IDS_AND_TYPE);
			query.setParameter("referenceType", referenceType);
			query.setParameterList("referenceIds", referenceIds);
			query.executeUpdate();
		}
	}

}
