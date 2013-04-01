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

import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author suneet
 *
 */
@Service("languageDataService")
public class LanguageDataServiceImpl implements LanguageDataService {

    @Autowired
    @Qualifier("languageDAO")
	private LanguageDAO languageDao;
	
	
    @Transactional
	public void addLanguage(LanguageEntity lg) {
		if (lg == null) {
			throw new NullPointerException("lg is null");
		}
		languageDao.add(lg);

	}

	public List<LanguageEntity> allLanguages() {
		return languageDao.findAll();
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
        LanguageEntity lg = getLanguage(languageId);
		languageDao.delete(lg);
	}

    @Transactional
	public void updateLanguage(LanguageEntity lg) {
		if (lg == null) {
			throw new NullPointerException("lg is null");
		}
        LanguageEntity l = getLanguage(lg.getLanguageId());
        if(l!=null){
		    l.setName(lg.getName());
            l.setLocale(lg.getLocale());
            l.setIsUsed(lg.getIsUsed());

            languageDao.update(l);
        }
	}
}
