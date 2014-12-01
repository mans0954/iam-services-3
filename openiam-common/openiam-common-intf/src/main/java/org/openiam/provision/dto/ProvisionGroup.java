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

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleSetAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

/**
 * @author suneet
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionGroup", propOrder = {
        "requestId",
        "sessionId",
        "srcSystemId",
        "skipPreprocessor",
        "skipPostProcessor",
        "parentAuditLogId",
        "identityList",
        "notProvisioninResourcesIds",
        "updateManagedSystemMembers"
})
public class ProvisionGroup extends org.openiam.idm.srvc.grp.dto.Group {
	private static final long serialVersionUID = -33009889049229700L;


    // flags to skip over the service level pre and post processors
    private boolean skipPreprocessor = false;
    private boolean skipPostProcessor = false;

    // IDs of Managed systems whose users being the group members need to be provisioned
    private Set<String> updateManagedSystemMembers = new HashSet<>();

    private String sessionId;

    private String requestId;
	/* ID of the system where this request came from */
    private String srcSystemId;

    protected List<IdentityDto> identityList = new LinkedList<IdentityDto>();

    protected Set<String> notProvisioninResourcesIds = new HashSet<String>();

    //AuditLogEntity ID of parent AuditLog
    private String parentAuditLogId;

    @XmlTransient
    protected List<String> membersIds;

    public ProvisionGroup() {
        super();
    }

    public ProvisionGroup(Group group) {
        setAdminResourceId(group.getAdminResourceId());
        setAdminResourceName(group.getAdminResourceName());

        this.name = group.getName();
        this.id = group.getId();

        this.operation = group.getOperation();
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

    public void addMemberId(final String id) {
        if(CollectionUtils.isEmpty(membersIds)) {
            membersIds = new LinkedList<String>();
        }
        membersIds.add(id);
    }

    public List<String> getMembersIds() {
        return membersIds;
    }

    public void setMembersIds(List<String> membersIds) {
        this.membersIds = membersIds;
    }

    public String getParentAuditLogId() {
        return parentAuditLogId;
    }

    public void setParentAuditLogId(String parentAuditLogId) {
        this.parentAuditLogId = parentAuditLogId;
    }

    public boolean isSkipPreprocessor() {
        return skipPreprocessor;
    }

    public void setSkipPreprocessor(boolean skipPreprocessor) {
        this.skipPreprocessor = skipPreprocessor;
    }

    public boolean isSkipPostProcessor() {
        return skipPostProcessor;
    }

    public void setSkipPostProcessor(boolean skipPostProcessor) {
        this.skipPostProcessor = skipPostProcessor;
    }

    public Set<String> getUpdateManagedSystemMembers() {
        return updateManagedSystemMembers;
    }

    public void setUpdateManagedSystemMembers(Set<String> updateManagedSystemMembers) {
        this.updateManagedSystemMembers = updateManagedSystemMembers;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public List<IdentityDto> getIdentityList() {
        return identityList;
    }

    public void addIdentity(IdentityDto identity) {
        if(identity != null) {
            if(this.identityList == null) {
                this.identityList = new LinkedList<>();
            }
            identity.setOperation(AttributeOperationEnum.ADD);
            this.identityList.add(identity);
        }
    }

    public void setIdentityList(List<IdentityDto> identityList) {
        this.identityList = identityList;
    }

}
