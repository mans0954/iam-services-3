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
package org.openiam.idm.srvc.synch.service;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.context.ApplicationContextAware;

/**
 * Interface that all transformation scripts in the synchronization process must implement
 * @author suneet
 *
 */
public interface TransformScript extends ApplicationContextAware {
    static int SKIP = -1;
    static int NO_DELETE = 0;
    static int DELETE = 1;
    static int DISABLE = 2;
    static int ENABLE = 3;
    static int SKIP_TO_REVIEW = 4;

    int execute(LineObject rowObj, ProvisionUser pUser);

    public void init();

    User getUser();
    void setUser(User user) ;
    List<Login> getPrincipalList() ;
    void setPrincipalList(List<Login> principalList);
    List<Role> getUserRoleList() ;
    void setUserRoleList(List<Role> userRoleList) ;
    boolean isNewUser();
    void setNewUser(boolean isNewUser);
    void setSynchConfigId(String synchConfigId);
}
