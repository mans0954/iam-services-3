package org.openiam.idm.srvc.lang.service;

import java.util.Collection;
import java.util.List;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;

public interface LanguageMappingDAO extends BaseDao<LanguageMappingEntity, String> {

	public List<LanguageMappingEntity> getByReferenceIdAndType(final String referenceId, final String referenceType);
}
