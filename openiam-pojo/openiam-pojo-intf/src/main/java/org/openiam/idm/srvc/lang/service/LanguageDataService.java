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
    public List<Language> getUsedLanguages(Language language);

    /**
     * Returns the language specified by the language
     * 
     * @param id
     * @return
     */
    public Language getLanguage(String id);

    public List<Language> findBeans(final LanguageSearchBean searchBean, int from, int size);

    public List<Language> findBeans(final LanguageSearchBean searchBean, int from, int size,
            final Language language);


    Language getDefaultLanguage();

    String save(Language language) throws BasicDataServiceException;

    int count(final LanguageSearchBean searchBean);
}