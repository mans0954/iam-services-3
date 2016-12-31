package org.openiam.idm.srvc.lang.service;

import java.util.List;
import java.util.Map;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageLocaleEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.dto.Language;

/**
 * <code>LanguageDataService</code> provides a service to manage the list of
 * languages stored in OpenIAM.
 * 
 * @author Suneet Shah
 * @version 2.0
 */

public interface LanguageDataService {
    /**
     * Returns an list of those languages that are in use
     * 
     * @return
     */
    List<Language> getUsedLanguages();

    /**
     * Returns the lang specified by the lang
     * 
     * @param id
     * @return
     */
    Language getLanguage(String id);

    List<Language> findBeans(final LanguageSearchBean searchBean, int from, int size);


    Language getDefaultLanguage();

    String save(Language language) throws BasicDataServiceException;

    int count(final LanguageSearchBean searchBean);
}