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
	
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.lang.service.LanguageDataService#addLanguage(org.openiam.idm.srvc.lang.dto.Language)
	 */
    @Transactional
	public void addLanguage(LanguageEntity lg) {
		if (lg == null) {
			throw new NullPointerException("lg is null");
		}
		languageDao.add(lg);

	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.lang.service.LanguageDataService#allLanguages()
	 */
	public LanguageEntity[] allLanguages() {
		List<LanguageEntity> lgList = languageDao.findAll();
		if (lgList == null || lgList.isEmpty())
			return null;
		int size = lgList.size();
        LanguageEntity[] lgAry = new LanguageEntity[size];
		lgList.toArray(lgAry);
		return lgAry;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.lang.service.LanguageDataService#getLanguage(java.lang.String)
	 */
	public LanguageEntity getLanguage(String languageId) {

		if (languageId == null) {
			throw new NullPointerException("languageCd is null");
		}
		return languageDao.findById(languageId);
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.lang.service.LanguageDataService#removeLanguage(java.lang.String)
	 */
    @Transactional
	public void removeLanguage(String languageId) {
		if (languageId == null) {
			throw new NullPointerException("languageCd is null");
		}
        LanguageEntity lg = getLanguage(languageId);
		languageDao.delete(lg);
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.lang.service.LanguageDataService#updateLanguage(org.openiam.idm.srvc.lang.dto.Language)
	 */
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
