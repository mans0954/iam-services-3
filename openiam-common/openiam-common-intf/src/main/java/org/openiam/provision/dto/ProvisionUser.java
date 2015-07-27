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
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * ProvisionUser is the user object used by the provisioning service.
 *
 * @author suneet
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionUser", propOrder = {"requestId", "sessionId",
        "superiors", "srcSystemId", "provisionModel", "notifyTargetSystems",
        "emailCredentialsToNewUsers", "emailCredentialsToSupervisor", "provisionOnStartDate",
        "addInitialPasswordToHistory", "passwordPolicy", "skipPreprocessor",
        "skipPostProcessor", "parentAuditLogId", "notProvisioninResourcesIds"})
public class ProvisionUser extends org.openiam.idm.srvc.user.dto.User {
    /**
     *
     */
    private static final long serialVersionUID = 6441635701870724194L;
    // protected List<Login> principalList;
//    protected List<Group> memberOfGroups;
//    protected List<Role> memberOfRoles;
//    protected List<Organization> userAffiliations;
//    protected List<UserResourceAssociation> userResourceList;
    protected Set<User> superiors = new HashSet<User>(0);
    protected Set<String> notProvisioninResourcesIds = new HashSet<String>();

    public ProvisionModelEnum provisionModel;

    boolean emailCredentialsToNewUsers = false;
    boolean emailCredentialsToSupervisor = false;
    boolean addInitialPasswordToHistory = false;

    // default behaviour - you dont have to wait till the start date to
    // provision a user
    // if this is set to true, the system will wait till the start date to
    // provision the user
    boolean provisionOnStartDate = false;

    protected String requestId;
    protected String sessionId;

    // flags to skip over the service level pre and post processors
    boolean skipPreprocessor = false;
    boolean skipPostProcessor = false;

    //AuditLogEntity ID of parent AuditLog
    private String parentAuditLogId;
    /*
     * ID of the system where this request came from. If this value is set, then
     * in the modify operation, that ManagedSys ID will not be updated.
     */
    protected String srcSystemId;
    /*
     * Flag that indicates if target systems should be updated or not
     */
    protected boolean notifyTargetSystems = true;

    protected Policy passwordPolicy = null;

    public ProvisionUser() {
    }

    public ProvisionUser(User user) {
        birthdate = user.getBirthdate();
        companyOwnerId = user.getCompanyOwnerId();
        createDate = user.getCreateDate();
        createdBy = user.getCreatedBy();
        employeeId = user.getEmployeeId();
        employeeTypeId = user.getEmployeeTypeId();
        firstName = user.getFirstName();
        jobCodeId = user.getJobCodeId();
        lastName = user.getLastName();
        lastUpdate = user.getLastUpdate();
        this.lastUpdatedBy = user.getLastUpdatedBy();
        this.locationCd = user.getLocationCd();
        this.locationName = user.getLocationName();
        this.setMdTypeId(user.getMdTypeId());
        this.classification = user.getClassification();
        this.middleInit = user.getMiddleInit();
        this.prefix = user.getPrefix();
        this.sex = user.getSex();
        this.status = user.getStatus();
        this.secondaryStatus = user.getSecondaryStatus();
        this.suffix = user.getSuffix();
        this.title = user.getTitle();
        this.id = user.getId();
        this.userTypeInd = user.getUserTypeInd();
        this.mailCode = user.getMailCode();
        this.costCenter = user.getCostCenter();
        this.startDate = user.getStartDate();
        this.lastDate = user.getLastDate();
        this.claimDate = user.getClaimDate();
        this.nickname = user.getNickname();
        this.maidenName = user.getMaidenName();
        this.passwordTheme = user.getPasswordTheme();
        this.email = user.getEmail();
        this.showInSearch = user.getShowInSearch();
        this.alternateContactId = user.getAlternateContactId();
        this.createdBy = user.getCreatedBy();
        this.userOwnerId = user.getUserOwnerId();
        this.dateChallengeRespChanged = user.getDateChallengeRespChanged();
        this.datePasswordChanged = user.getDatePasswordChanged();
        this.dateITPolicyApproved = user.getDateITPolicyApproved();
        userNotes = user.getUserNotes();
        userAttributes = user.getUserAttributes();
        phones = user.getPhones();
        addresses = user.getAddresses();
        emailAddresses = user.getEmailAddresses();
        principalList = user.getPrincipalList();
        roles = user.getRoles();
        groups = user.getGroups();
        organizationUserDTOs = user.getOrganizationUserDTOs();
        resources = user.getResources();
        setPassword(user.getPassword());
        setLogin(user.getLogin());
        if (user instanceof ProvisionUser) {
            this.parentAuditLogId = ((ProvisionUser) user).getParentAuditLogId();
        }
        this.setIsFromActivitiCreation(user.getIsFromActivitiCreation());
        if (user instanceof ProvisionUser) {
            this.setRequestId(((ProvisionUser) user).getRequestId());
        }
        this.setRequestorUserId(user.getRequestorUserId());
        this.setRequestClientIP(user.getRequestClientIP());
        this.setRequestorLogin(user.getRequestorLogin());
        this.setRequestorSessionID(user.getRequestorSessionID());

    }

    public User getUser() {
        User user = new User();

        user.setBirthdate(birthdate);
        user.setCompanyOwnerId(companyOwnerId);
        user.setCreateDate(createDate);
        user.setCreatedBy(createdBy);
        user.setEmployeeId(employeeId);
        user.setEmployeeTypeId(employeeTypeId);
        user.setFirstName(firstName);
        user.setJobCodeId(jobCodeId);
        user.setLastName(lastName);
        user.setLastUpdate(lastUpdate);
        user.setLastUpdatedBy(lastUpdatedBy);
        user.setLocationCd(locationCd);
        user.setLocationName(locationName);
        user.setMdTypeId(this.getMdTypeId());
        user.setClassification(classification);
        user.setMiddleInit(middleInit);
        user.setPrefix(prefix);
        user.setSex(sex);
        user.setStatus(status);
        user.setSecondaryStatus(secondaryStatus);
        user.setSuffix(suffix);
        user.setTitle(title);
        user.setId(id);
        user.setUserTypeInd(userTypeInd);
        user.setMailCode(mailCode);
        user.setCostCenter(costCenter);
        user.setStartDate(startDate);
        user.setLastDate(lastDate);
        user.setClaimDate(claimDate);
        user.setNickname(nickname);
        user.setMaidenName(maidenName);
        user.setPasswordTheme(passwordTheme);
        user.setEmail(email);
        user.setUserNotes(userNotes);
        user.setUserAttributes(userAttributes);
        user.setPhones(phones);
        user.setAddresses(addresses);
        user.setEmailAddresses(emailAddresses);
        user.setAlternateContactId(alternateContactId);
        user.setShowInSearch(showInSearch);
        user.setPrincipalList(principalList);
        user.setRoles(roles);
        user.setGroups(groups);
        user.setOrganizationUserDTOs(organizationUserDTOs);
        user.setResources(resources);
        user.setUserOwnerId(userOwnerId);
        user.setDateChallengeRespChanged(dateChallengeRespChanged);
        user.setDatePasswordChanged(datePasswordChanged);
        user.setDateITPolicyApproved(dateITPolicyApproved);
        user.setIsFromActivitiCreation(this.getIsFromActivitiCreation());
        return user;
    }

    public Login getPrimaryPrincipal(String managedSysId) {
        if (principalList == null || managedSysId == null) {
            return null;
        }
        for (Login l : principalList) {
            if (managedSysId.equals(l.getManagedSysId())) {
                return l;
            }
        }
        return null;
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

    public String getParentAuditLogId() {
        return parentAuditLogId;
    }

    public void setParentAuditLogId(String parentAuditLogId) {
        this.parentAuditLogId = parentAuditLogId;
    }

    public Set<User> getSuperiors() {
        return superiors;
    }

    public void setSuperiors(Set<User> superiors) {
        this.superiors = superiors;
    }

    public void addSuperior(final User superior) {
        if (superior != null) {
            if (superiors == null) {
                superiors = new HashSet<User>();
            }
            superiors.add(superior);
        }
    }

    public void addSuperiors(final Collection<User> superiors) {
        if (superiors != null) {
            if (this.superiors == null) {
                this.superiors = new HashSet<User>();
            }
            this.superiors.addAll(superiors);
        }
    }

    public ProvisionModelEnum getProvisionModel() {
        return provisionModel;
    }

    public void setProvisionModel(ProvisionModelEnum provisionModel) {
        this.provisionModel = provisionModel;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isNotifyTargetSystems() {
        return notifyTargetSystems;
    }

    public void setNotifyTargetSystems(boolean notifyTargetSystems) {
        this.notifyTargetSystems = notifyTargetSystems;
    }

    @Override
    public String toString() {
        return "ProvisionUser{ superiors=" + superiors + ", provisionModel=" + provisionModel
                + ", emailCredentialsToNewUsers=" + emailCredentialsToNewUsers + ", emailCredentialsToSupervisor="
                + emailCredentialsToSupervisor + ", addInitialPasswordToHistory=" + addInitialPasswordToHistory + ", provisionOnStartDate="
                + provisionOnStartDate + ", requestId='" + requestId + '\'' + ", sessionId='" + sessionId + '\'' + ", skipPreprocessor="
                + skipPreprocessor + ", skipPostProcessor=" + skipPostProcessor + ", srcSystemId='" + srcSystemId + '\'' + ", notifyTargetSystems="
                + notifyTargetSystems + ", passwordPolicy=" + passwordPolicy + '}';
    }

    public boolean isEmailCredentialsToNewUsers() {
        return emailCredentialsToNewUsers;
    }

    public void setEmailCredentialsToNewUsers(boolean emailCredentialsToNewUsers) {
        this.emailCredentialsToNewUsers = emailCredentialsToNewUsers;
    }

    public boolean isEmailCredentialsToSupervisor() {
        return emailCredentialsToSupervisor;
    }

    public void setEmailCredentialsToSupervisor(boolean emailCredentialsToSupervisor) {
        this.emailCredentialsToSupervisor = emailCredentialsToSupervisor;
    }

    public boolean isProvisionOnStartDate() {
        return provisionOnStartDate;
    }

    public void setProvisionOnStartDate(boolean provisionOnStartDate) {
        this.provisionOnStartDate = provisionOnStartDate;
    }

    public boolean isAddInitialPasswordToHistory() {
        return addInitialPasswordToHistory;
    }

    public void setAddInitialPasswordToHistory(boolean addInitialPasswordToHistory) {
        this.addInitialPasswordToHistory = addInitialPasswordToHistory;
    }

    public Policy getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(Policy passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
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

    public Organization getPrimaryOrganization(String metadataType) {
        Organization retVal = null;
        if (CollectionUtils.isNotEmpty(organizationUserDTOs)) {
            for (final OrganizationUserDTO organizationUserDTO : organizationUserDTOs) {
                if (!AttributeOperationEnum.DELETE.equals(organizationUserDTO.getOperation())) {
                    if (organizationUserDTO.getOrganization() != null) {
                        retVal = organizationUserDTO.getOrganization();
                    }
                }
                if (metadataType.equals(organizationUserDTO.getMdTypeId())) {
                    break;
                }
            }

        }
        return retVal;
    }

    public boolean isOrganizationMarkedAsDeleted(final String organizationId) {
        boolean retVal = false;
        if (CollectionUtils.isNotEmpty(organizationUserDTOs)) {
            for (final OrganizationUserDTO organizationUserDTO : organizationUserDTOs) {
                if (AttributeOperationEnum.DELETE.equals(organizationUserDTO.getOperation())) {
                    if (organizationUserDTO.getOrganization() != null && StringUtils.equalsIgnoreCase(organizationId, organizationUserDTO.getOrganization().getId())) {
                        retVal = true;
                        break;
                    }
                }
            }
        }
        return retVal;
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
