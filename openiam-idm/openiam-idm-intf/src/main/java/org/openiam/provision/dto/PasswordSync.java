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
package org.openiam.provision.dto;

import org.openiam.base.BaseObject;
import org.openiam.provision.type.ExtensibleAttribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Password object used for synchronization
 *
 * @author suneet
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PasswordSync", propOrder = {
        "managedSystemId",
        "principal",
        "userId",
        "password",
        "requestorId",
        "sendPasswordToUser",
        "passThruAttributes",
        "attributeList",
        "preventChangeCountIncrement",
        "userActivateFlag",
        //skip validation
        "resyncMode",
        "forceChange"
})
public class PasswordSync extends BaseObject {

    /**
     *
     */
    private static final long serialVersionUID = 2746720616086920826L;

    private String managedSystemId;
    private String principal;
    private String userId;
    private String password;
    private String requestorId;
    private boolean sendPasswordToUser = false;
    private boolean forceChange = false;
    private boolean passThruAttributes = true;
    private List<ExtensibleAttribute> attributeList = new ArrayList<ExtensibleAttribute>();
    private boolean preventChangeCountIncrement;
    private boolean userActivateFlag;
    private boolean resyncMode;

    public PasswordSync() {

    }

    public String getManagedSystemId() {
        return managedSystemId;
    }

    public void setManagedSystemId(String managedSystemId) {
        this.managedSystemId = managedSystemId;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    public boolean isPassThruAttributes() {
        return passThruAttributes;
    }

    public void setPassThruAttributes(boolean passThruAttributes) {
        this.passThruAttributes = passThruAttributes;
    }

    public List<ExtensibleAttribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<ExtensibleAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    public boolean getSendPasswordToUser() {
        return sendPasswordToUser;
    }

    public void setSendPasswordToUser(boolean sendPasswordToUser) {
        this.sendPasswordToUser = sendPasswordToUser;
    }

    public boolean isPreventChangeCountIncrement() {
        return preventChangeCountIncrement;
    }

    public void setPreventChangeCountIncrement(boolean preventChangeCountIncrement) {
        this.preventChangeCountIncrement = preventChangeCountIncrement;
    }

    public boolean getUserActivateFlag() {
        return userActivateFlag;
    }

    public void setUserActivateFlag(boolean userActivateFlag) {
        this.userActivateFlag = userActivateFlag;
    }

    public boolean getResyncMode() {
        return resyncMode;
    }

    public void setResyncMode(boolean resyncMode) {
        this.resyncMode = resyncMode;
    }

    public boolean getForceChange() {        return forceChange;     }
    public void setForceChange(boolean forceChange) {         this.forceChange = forceChange;    }
}

