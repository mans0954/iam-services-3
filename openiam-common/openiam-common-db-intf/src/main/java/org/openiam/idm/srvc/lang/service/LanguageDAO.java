package org.openiam.idm.srvc.lang.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;

import java.util.List;


public interface LanguageDAO  extends BaseDao<LanguageEntity, String> {
	LanguageEntity getByLocale(final String locale);
	LanguageEntity getByCode(final String languageCode);
	LanguageEntity getDefaultLanguage();
    List<LanguageEntity> getUsedLanguages();
}
