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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.lang.domain.LanguageLocaleEntity;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author zaporozhec
 * 
 */
@Repository("languageLocaleDAO")
public class LanguageLocaleDAOImpl extends BaseDaoImpl<LanguageLocaleEntity, String> implements LanguageLocaleDAO {
    private static final Log log = LogFactory.getLog(LanguageLocaleDAOImpl.class);

    @Override
    public List<LanguageLocaleEntity> getLocalesByLanguageId(String languageId) {
        return this.getCriteria().add(Restrictions.eq("language.id", languageId)).list();
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

}
