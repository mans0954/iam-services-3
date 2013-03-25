package org.openiam.idm.srvc.lang.service;

import org.openiam.idm.srvc.lang.domain.LanguageEntity;

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
     * Returns an array of all languages
     *
     * @return
     */
    public LanguageEntity[] allLanguages();

    /**
     * Returns the language specified by the language
     *
     * @param languageCd
     * @return
     */
    public LanguageEntity getLanguage(String languageCd);


}