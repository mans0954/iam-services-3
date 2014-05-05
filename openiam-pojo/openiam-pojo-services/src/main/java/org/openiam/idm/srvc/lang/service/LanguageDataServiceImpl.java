/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.lang.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageLocaleEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suneet
 * 
 */
@Service("languageDataService")
public class LanguageDataServiceImpl implements LanguageDataService {

    @Autowired
    @Qualifier("languageDAO")
    private LanguageDAO languageDao;

    @Autowired
    @Qualifier("languageLocaleDAO")
    private LanguageLocaleDAO languageLocaleDao;

    @Autowired
    @Qualifier("languageMappingDAO")
    private LanguageMappingDAO languageMappingDAO;

    @Transactional
    public LanguageEntity addLanguage(LanguageEntity lg) {
        if (lg == null) {
            throw new NullPointerException("lg is null");
        }
        return languageDao.add(lg);

    }

    public List<LanguageEntity> allLanguages() {
        return languageDao.findAll();
    }

    public List<LanguageEntity> getUsedLanguages() {
        return languageDao.getUsedLanguages();
    }

    @Override
    @LocalizedServiceGet
    @Transactional
    public List<LanguageEntity> getUsedLanguages(final LanguageEntity language) {
        return languageDao.getUsedLanguages();
    }

    public LanguageEntity getLanguage(String languageId) {

        if (languageId == null) {
            throw new NullPointerException("languageCd is null");
        }
        return languageDao.findById(languageId);
    }

    @Transactional
    public void removeLanguage(String languageId) {
        if (languageId == null) {
            throw new NullPointerException("languageCd is null");
        }
        final LanguageEntity lg = getLanguage(languageId);
        languageDao.delete(lg);
    }

    @Transactional
    public void updateLanguage(LanguageEntity lg) {
        if (lg == null) {
            throw new NullPointerException("lg is null");
        }
        final LanguageEntity l = getLanguage(lg.getId());
        if (l != null) {
            languageDao.merge(lg);
        }
    }

    @Override
    public List<LanguageEntity> findBeans(final LanguageSearchBean searchBean, final int from, final int size) {
        return languageDao.getByExample(searchBean, from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional
    public List<LanguageEntity> findBeans(final LanguageSearchBean searchBean, int from, int size,
            final LanguageEntity language) {
        return languageDao.getByExample(searchBean, from, size);
    }

    @Override
    public LanguageLocaleEntity addLanguageLocale(LanguageLocaleEntity lgl) {
        if (lgl == null) {
            throw new NullPointerException("lg is null");
        }
        return languageLocaleDao.add(lgl);

    }

    @Override
    public LanguageMappingEntity addLanguageMapping(LanguageMappingEntity lgl) {
        if (lgl == null) {
            throw new NullPointerException("lg is null");
        }
        return languageMappingDAO.add(lgl);
    }

    @Override
    public void updateLanguageLocale(LanguageLocaleEntity lgl) {
        if (lgl == null) {
            throw new NullPointerException("lgl is null");
        }
        final LanguageLocaleEntity l = languageLocaleDao.findById(lgl.getId());
        if (l != null) {
            languageLocaleDao.merge(lgl);
        }
    }

    @Override
    public void removeLanguageLocale(LanguageLocaleEntity lgl) {
        if (lgl == null) {
            throw new NullPointerException("lgl is null");
        }
        languageLocaleDao.delete(lgl);
    }

    @Override
    public List<LanguageLocaleEntity> getLanguageLocaleByLanguage(String languageId) {
        return languageLocaleDao.getLocalesByLanguageId(languageId);
    }

    @Override
    public Map<String, LanguageLocaleEntity> getAllLocales() {
        List<LanguageLocaleEntity> locales = languageLocaleDao.findAll();
        Map<String, LanguageLocaleEntity> result = new HashMap<String, LanguageLocaleEntity>();
        for (LanguageLocaleEntity lle : locales) {
            result.put(lle.getLocale().toLowerCase(), lle);
        }
        return result;
    }

    @Override
    public LanguageEntity getDefaultLanguage() {
        return languageDao.getDefaultLanguage();
    }

}
