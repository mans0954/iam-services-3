package org.openiam.idm.srvc.lang.service;

import java.util.List;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.lang.domain.LanguageLocaleEntity;

public interface LanguageLocaleDAO extends BaseDao<LanguageLocaleEntity, String> {

	LanguageLocaleEntity getByLocale(String languageId);
}
