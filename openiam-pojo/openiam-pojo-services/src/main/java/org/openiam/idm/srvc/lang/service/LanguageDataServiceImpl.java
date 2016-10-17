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

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.cache.CacheKeyEvict;
import org.openiam.cache.CacheKeyEviction;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.dozer.converter.LanguageLocaleDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageLocaleEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageLocale;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suneet
 * 
 */
@Service("languageDataService")
public class LanguageDataServiceImpl implements LanguageDataService {

    @Autowired
    private LanguageDozerConverter languageDozerConverter;

    @Autowired
    @Qualifier("languageDAO")
    private LanguageDAO languageDao;

    @Autowired
    @Qualifier("languageLocaleDAO")
    private LanguageLocaleDAO languageLocaleDao;

    @Autowired
    @Qualifier("languageMappingDAO")
    private LanguageMappingDAO languageMappingDAO;

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    @Cacheable(value="localizedLanguages", key="{#language}")
    public List<Language> getUsedLanguages(Language language) {
        List<LanguageEntity> languageEntities = languageDao.getUsedLanguages();
        return languageEntities != null ? languageDozerConverter.convertToDTOList(languageEntities, true) : null;
    }

    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public Language getLanguage(String languageId) {

        if (languageId == null) {
            throw new NullPointerException("languageCd is null");
        }
        LanguageEntity languageEntity = languageDao.findById(languageId);
        return languageEntity != null ? languageDozerConverter.convertToDTO(languageEntity, true) : null;
    }

    @Transactional
    @CacheKeyEviction(
        	evictions={
            	@CacheKeyEvict("resources"),
            	@CacheKeyEvict("resourceEntities")
            }
        )
    public void removeLanguage(final String languageId) {
        if (languageId == null) {
            throw new NullPointerException("languageCd is null");
        }
        final LanguageEntity lg = languageDao.findById(languageId);
        languageDao.delete(lg);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value="languages",key="{ #searchBean, #from, #size}", condition="{#searchBean != null and #searchBean.findInCache}")
    public List<Language> findBeans(final LanguageSearchBean searchBean, final int from, final int size) {
        List<LanguageEntity> languageEntities = languageDao.getByExample(searchBean, from, size);
        return languageEntities != null ? languageDozerConverter.convertToDTOList(languageEntities, true) : null;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    @Cacheable(value="languages", key="{ #searchBean, #from, #size,#language}", condition="{#searchBean != null and #searchBean.findInCache}")
    public List<Language> findBeans(final LanguageSearchBean searchBean, int from, int size,
            final Language language) {
        return this.findBeans(searchBean, from, size);
    }

    private void updateLanguageLocale(LanguageLocaleEntity lgl) {
        if (lgl == null) {
            throw new NullPointerException("lgl is null");
        }
        final LanguageLocaleEntity l = languageLocaleDao.findById(lgl.getId());
        if (l != null) {
            languageLocaleDao.merge(lgl);
        }
    }

    private Map<String, LanguageLocaleEntity> getAllLocales() {
        List<LanguageLocaleEntity> locales = languageLocaleDao.findAll();
        Map<String, LanguageLocaleEntity> result = new HashMap<String, LanguageLocaleEntity>();
        for (LanguageLocaleEntity lle : locales) {
            result.put(lle.getLocale().toLowerCase(), lle);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public int count(LanguageSearchBean searchBean) {
        return languageDao.count(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public Language getDefaultLanguage() {
        LanguageEntity languageEntity =  languageDao.getDefaultLanguage();
        return languageEntity != null ? languageDozerConverter.convertToDTO(languageEntity, true) : null;
    }

    @Override
    @Transactional
    @CacheKeyEviction(
        	evictions={
            	@CacheKeyEvict("resources"),
            	@CacheKeyEvict("resourceEntities")
            }
        )
    public String save(final Language language) throws BasicDataServiceException {
        if (language == null) {
            throw new BasicDataServiceException(ResponseCode.INTERNAL_ERROR);
        }
        boolean isAdd = StringUtils.isEmpty(language.getId());
        if (!MapUtils.isEmpty(language.getLocales())) {
            Map<String, LanguageLocaleEntity> allLocales = getAllLocales();
            for (LanguageLocale ll : language.getLocales().values()) {
                if (ll.getId() == null && allLocales.get(ll.getLocale().toLowerCase()) != null) {
                    throw new BasicDataServiceException(ResponseCode.LOCALE_ALREADY_EXISTS, ll.getLocale());
                }
            }
        }
        for (LanguageMapping lm : language.getDisplayNameMap().values()) {
            if (StringUtils.isEmpty(lm.getValue())) {
                throw new BasicDataServiceException(ResponseCode.DISPLAY_NAME_REQUIRED);
            }
        }
        if (isAdd && StringUtils.isEmpty(language.getName())) {
            throw new BasicDataServiceException(ResponseCode.NAME_MISSING);
        }
        if (StringUtils.isEmpty(language.getLanguageCode())) {
            throw new BasicDataServiceException(ResponseCode.LANGUAGE_CODE_MISSING);
        }
        final LanguageEntity defaultLanguage = languageDao.getDefaultLanguage();

        if ((defaultLanguage == null && !language.getIsDefault())
                || (defaultLanguage != null && defaultLanguage.getId().equals(language.getId()) && !language.getIsDefault()))
            throw new BasicDataServiceException(ResponseCode.NO_DEFAULT_LANGUAGE);

        final LanguageEntity entity = languageDozerConverter.convertToEntity(language, true);
        
        if (defaultLanguage != null && language.getIsDefault()) {
            defaultLanguage.setIsDefault(false);
            languageDao.update(defaultLanguage);
        }
        
        if(MapUtils.isNotEmpty(entity.getLocales())) {
        	for(final Map.Entry<String, LanguageLocaleEntity> entry : entity.getLocales().entrySet()) {
        		final LanguageLocaleEntity locale = languageLocaleDao.getByLocale(entry.getValue().getLocale());
        		if(StringUtils.isBlank(entity.getId()) && locale != null) {
        			throw new BasicDataServiceException(ResponseCode.LOCALE_ALREADY_EXISTS);
        		}
        		
        		if(locale != null) {
        			entry.setValue(locale);
        		}
        		entry.getValue().setLanguage(entity);
        	}
        }
        
        if (StringUtils.isBlank(entity.getId())) {
        	languageDao.save(entity);
        } else {
        	languageDao.update(entity);
        }
        return entity.getId();
    }
}
