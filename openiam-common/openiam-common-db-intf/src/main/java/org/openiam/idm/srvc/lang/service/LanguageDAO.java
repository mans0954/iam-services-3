package org.openiam.idm.srvc.lang.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.springframework.stereotype.Repository;


public interface LanguageDAO  extends BaseDao<LanguageEntity, String> {
	public LanguageEntity getByLocale(final String locale);
	public LanguageEntity getByCode(final String languageCode);
	public LanguageEntity getDefaultLanguage();
}
