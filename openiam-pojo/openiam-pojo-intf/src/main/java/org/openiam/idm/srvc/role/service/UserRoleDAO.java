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
import org.openiam.idm.srvc.role.dto.UserRole;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;

import java.util.List;

/**
 * DAO Interface for UserRole. Manages the relationship between user and role.
 *
 * @author Suneet Shah
 */
public interface UserRoleDAO extends BaseDao<UserRoleEntity, String> {

    public void removeUserFromRole(String roleId, String userId);

    public void removeAllUsersInRole(String roleId);

    /**
     * Get all the UserRole objects for this user
     *
     * @param userId
     * @return
     */
    public List<UserRoleEntity> findUserRoleByUser(String userId);

    /**
     * Returns a list of users in a role.
     *
     * @param roleId
     * @return
     */
    List<UserEntity> findUserByRole(String roleId);

}