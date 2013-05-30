/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
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
package org.openiam.idm.srvc.org.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.UserAffiliationEntity;

import java.util.List;
import java.util.Set;

public interface UserAffiliationDAO extends BaseDao<UserAffiliationEntity, String> {

    public List<OrganizationEntity> findOrgAffiliationsByUser(String userId, Set<String> filter);

    public UserAffiliationEntity getRecord(final String userId, final String organizationId);
    
    public void deleteByOrganizationId(String organizationId);
}