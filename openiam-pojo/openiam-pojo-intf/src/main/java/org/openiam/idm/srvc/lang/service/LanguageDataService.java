package org.openiam.idm.srvc.lang.service;

import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;

import java.util.List;

/**
 * <code>LanguageDataService</code> provides a service to manage the
 * list of languages stored in OpenIAM.
 *
 * @author Suneet Shah
 * @version 2.0
 */


public interface LanguageDataService {

    /**
     * Adds a new language to the list
     *
     * @param lg
     */
    public void addLanguage(LanguageEntity lg);

    /**
     * Updates an existing language in the list
     *
     * @param lg
     */
    public void updateLanguage(LanguageEntity lg);

    /**
     * Removes a languages from the list of languages
     *
     * @param langCd
     */
    public void removeLanguage(String langCd);

    /**
     * Returns an list of all languages
     *
     * @return
     */
    public List<LanguageEntity> allLanguages();
    /**
     * Returns an list of those languages that are in use
     *
     * @return
     */
    public List<LanguageEntity> getUsedLanguages();

    /**
     * Returns the language specified by the language
     *
     * @param languageCd
     * @return
     */
    public LanguageEntity getLanguage(String languageCd);


    public List<LanguageEntity> findBeans(final LanguageSearchBean searchBean, int from, int size);
}