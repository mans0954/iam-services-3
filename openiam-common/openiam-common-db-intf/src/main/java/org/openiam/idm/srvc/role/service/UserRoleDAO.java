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
package org.openiam.idm.srvc.role.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;

import java.util.Collection;
import java.util.List;

/**
 * DAO Interface for UserRole. Manages the relationship between user and role.
 *
 * @author Suneet Shah
 */
public interface UserRoleDAO extends BaseDao<UserRoleEntity, String> {
    
    public UserRoleEntity getRecord(final String userId, final String roleId);
    
    public void deleteByRoleId(final String roleId);
    public void deleteByUserId(final String userId);
    
    public List<String> getUserIdsInRole(final Collection<String> roleIdList, final int from, final int size);
}