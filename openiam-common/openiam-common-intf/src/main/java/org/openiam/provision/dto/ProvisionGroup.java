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

import org.openiam.idm.srvc.grp.dto.Group;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * @author suneet
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionGroup", propOrder = {
        "requestId",
        "srcSystemId"
})
public class ProvisionGroup extends org.openiam.idm.srvc.grp.dto.Group {
	private static final long serialVersionUID = -33009889049229700L;
	
	private String requestId;
	/* ID of the system where this request came from */
    private String srcSystemId;

    @XmlTransient
    protected Set<String> notProvisioninResourcesIds = new HashSet<String>();

    public ProvisionGroup() {
        super();
    }

    public ProvisionGroup(Group group) {
        this.managedSysId = group.getManagedSysId();
        this.managedSysName = group.getManagedSysName();
        this.createDate = group.getCreateDate();
        this.createdBy = group.getCreatedBy();
        this.companyId = group.getCompanyId();
        this.description = group.getDescription();

        this.status = group.getStatus();
        this.lastUpdate = group.getLastUpdate();
        this.lastUpdatedBy = group.getLastUpdatedBy();

        this.parentGroups = group.getParentGroups();
        this.childGroups = group.getChildGroups();

        this.resources = group.getResources();
        this.roles = group.getRoles();
        this.attributes = group.getAttributes();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSrcSystemId() {
        return srcSystemId;
    }

    public void setSrcSystemId(String srcSystemId) {
        this.srcSystemId = srcSystemId;
    }

    public Set<String> getNotProvisioninResourcesIds() {
        return notProvisioninResourcesIds;
    }

    public void setNotProvisioninResourcesIds(Set<String> notProvisioninResourcesIds) {
        this.notProvisioninResourcesIds = notProvisioninResourcesIds;
    }

    public void addNotProvisioninResourcesId(final String notProvisioninResourceId) {
        if (notProvisioninResourceId != null) {
            notProvisioninResourcesIds.add(notProvisioninResourceId);
        }
    }
}
