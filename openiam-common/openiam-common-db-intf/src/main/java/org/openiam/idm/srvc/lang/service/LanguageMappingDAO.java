package org.openiam.idm.srvc.lang.service;

import java.util.Collection;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;

public interface LanguageMappingDAO extends BaseDao<LanguageMappingEntity, String> {

	public void deleteByReferenceTypeAndIds(final Collection<String> referenceIds, final String referencetype);
}
