package org.openiam.idm.srvc.lang.service;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.searchbean.converter.LanguageSearchBeanConverter;
import org.openiam.internationalization.LocalizedDatabaseGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO to manage the list of languages.
 * 
 * @see org.openiam.idm.srvc.lang.dto.Language
 * @author Suneet Shah
 */
@Repository("languageDAO")
public class LanguageDAOImpl extends BaseDaoImpl<LanguageEntity, String> implements LanguageDAO {
    private static final Log log = LogFactory.getLog(LanguageDAOImpl.class);

    @Autowired
    private LanguageSearchBeanConverter converter;

    @Override
    protected Criteria getExampleCriteria(final LanguageEntity t) {
        final Criteria criteria = getCriteria();
        if (t != null) {
            if (!StringUtils.isEmpty(t.getId())) {
                criteria.add(Restrictions.eq("id", t.getId()));
            } else {
                if (StringUtils.isNotBlank(t.getLanguageCode())) {
                    criteria.add(Restrictions.eq("languageCode", t.getLanguageCode()));
                }
            }
        }
        return criteria;
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if (searchBean != null && (searchBean instanceof LanguageSearchBean)) {
            final LanguageSearchBean sb = (LanguageSearchBean) searchBean;
            criteria = getExampleCriteria(converter.convert(sb));
        }
        return criteria;
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    @LocalizedDatabaseGet
    public LanguageEntity getByLocale(String locale) {
        final Criteria criteria = getCriteria();
        criteria.createAlias("locales", "locale").add(Restrictions.eq("locale.locale", locale));
        return (LanguageEntity) criteria.uniqueResult();
    }

    @Override
    @LocalizedDatabaseGet
    public LanguageEntity getByCode(String languageCode) {
        final Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("languageCode", languageCode));
        return (LanguageEntity) criteria.uniqueResult();
    }

    @Override
    @LocalizedDatabaseGet
    public LanguageEntity getDefaultLanguage() {
        final Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("isDefault", true));
        return (LanguageEntity) criteria.uniqueResult();
    }

    @Override
    @LocalizedDatabaseGet
    public List<LanguageEntity> getUsedLanguages() {
        final Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("isUsed", true));
        return (List<LanguageEntity>) criteria.list();
    }
}
