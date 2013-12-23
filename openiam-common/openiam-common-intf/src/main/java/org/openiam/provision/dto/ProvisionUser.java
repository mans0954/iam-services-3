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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.user.dto.User;

/**
 * ProvisionUser is the user object used by the provisioning service.
 * 
 * @author suneet
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionUser", propOrder = { "requestId", "sessionId",
                                              "superiors", "srcSystemId", "provisionModel", "securityDomain", "notifyTargetSystems",
                                              "emailCredentialsToNewUsers", "emailCredentialsToSupervisor", "provisionOnStartDate",
                                              "addInitialPasswordToHistory", "passwordPolicy", "skipPreprocessor",
                                              "skipPostProcessor"})
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
    @XmlTransient
    protected Set<String> notProvisioninResourcesIds = new HashSet<String>();


    public ProvisionModelEnum provisionModel;
    public String securityDomain;

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

    /*
     * ID of the system where this request came from. If this value is set, then
     * in the modify operation, that resource will not be updated.
     */
    protected String srcSystemId;
    /*
     * Flag that indicates if target systems should be updated or not
     */
    protected boolean notifyTargetSystems = true;

    protected Policy passwordPolicy = null;

    public ProvisionUser() {}

    public ProvisionUser(User user) {
        birthdate = user.getBirthdate();
        companyOwnerId = user.getCompanyOwnerId();
        createDate = user.getCreateDate();
        createdBy = user.getCreatedBy();
        employeeId = user.getEmployeeId();
        employeeType = user.getEmployeeType();
        firstName = user.getFirstName();
        jobCode = user.getJobCode();
        lastName = user.getLastName();
        lastUpdate = user.getLastUpdate();
        this.lastUpdatedBy = user.getLastUpdatedBy();
        this.locationCd = user.getLocationCd();
        this.locationName = user.getLocationName();
        this.metadataTypeId = user.getMetadataTypeId();
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
        this.nickname = user.getNickname();
        this.maidenName = user.getMaidenName();
        this.passwordTheme = user.getPasswordTheme();
        this.email = user.getEmail();
        this.showInSearch = user.getShowInSearch();
        this.alternateContactId = user.getAlternateContactId();
        this.createdBy = user.getCreatedBy();
        this.startDate = user.getStartDate();
        this.lastDate = user.getLastDate();
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
        affiliations = user.getAffiliations();
        resources = user.getResources();
        setPassword(user.getPassword());
        setLogin(user.getLogin());
    }

    public User getUser() {
        User user = new User();

        user.setBirthdate(birthdate);
        user.setCompanyOwnerId(companyOwnerId);
        user.setCreateDate(createDate);
        user.setCreatedBy(createdBy);
        user.setEmployeeId(employeeId);
        user.setEmployeeType(employeeType);
        user.setFirstName(firstName);
        user.setJobCode(jobCode);
        user.setLastName(lastName);
        user.setLastUpdate(lastUpdate);
        user.setLastUpdatedBy(lastUpdatedBy);
        user.setLocationCd(locationCd);
        user.setLocationName(locationName);
        user.setMetadataTypeId(metadataTypeId);
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
        user.setAffiliations(affiliations);
        user.setResources(resources);
        user.setUserOwnerId(userOwnerId);
        user.setDateChallengeRespChanged(dateChallengeRespChanged);
        user.setDatePasswordChanged(datePasswordChanged);
        user.setDateITPolicyApproved(dateITPolicyApproved);

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
    /*
    public List<Group> getMemberOfGroups() {
        return memberOfGroups;
    }

    public void setMemberOfGroups(List<Group> memberOfGroups) {
        this.memberOfGroups = memberOfGroups;
    }

    public List<Role> getMemberOfRoles() {
        return memberOfRoles;
    }

    public List<Role> getActiveMemberOfRoles() {
        List<Role> activeRoleList = new ArrayList<Role>();
        if (memberOfRoles != null) {
            for (Role r : memberOfRoles) {
                if (r.getOperation() != AttributeOperationEnum.DELETE) {
                    activeRoleList.add(r);
                }
            }
            return activeRoleList;
        }
        return null;
    }

    public void setMemberOfRoles(List<Role> memberOfRoles) {
        this.memberOfRoles = memberOfRoles;
    }
    */
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

    public String getSecurityDomain() {
        return securityDomain;
    }

    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
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
        return "ProvisionUser{ superiors=" + superiors + ", provisionModel=" + provisionModel + ", securityDomain='"
               + securityDomain + '\'' + ", emailCredentialsToNewUsers=" + emailCredentialsToNewUsers + ", emailCredentialsToSupervisor="
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
    /*
    public List<Organization> getUserAffiliations() {
        return userAffiliations;
    }

    public void addUserAffiliation(final Organization organization) {
        if (organization != null) {
            if (this.userAffiliations == null) {
                this.userAffiliations = new LinkedList<Organization>();
            }
            this.userAffiliations.add(organization);
        }
    }

    public void setUserAffiliations(List<Organization> userAffiliations) {
        this.userAffiliations = userAffiliations;
    }
    */
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

    /*
    public List<UserResourceAssociation> getUserResourceList() {
        return userResourceList;
    }

    public void setUserResourceList(List<UserResourceAssociation> userResourceList) {
        this.userResourceList = userResourceList;
    }
    */

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

    /*
    public void updateMissingUserAttributes(User user) {

        if (birthdate == null) {
            birthdate = user.getBirthdate();
        }
        if (companyOwnerId == null) {
            companyOwnerId = user.getCompanyOwnerId();
        }
        if (createDate == null) {
            createDate = user.getCreateDate();
        }
        if (createdBy == null) {
            createdBy = user.getCreatedBy();
        }
        if (employeeId == null) {
            employeeId = user.getEmployeeId();
        }
        if (employeeType == null) {
            employeeType = user.getEmployeeType();
        }

        if (firstName == null) {
            firstName = user.getFirstName();
        }
        if (jobCode == null) {
            jobCode = user.getJobCode();
        }
        if (lastName == null) {
            lastName = user.getLastName();
        }
        if (lastUpdate == null) {
            lastUpdate = user.getLastUpdate();
        }
        if (lastUpdatedBy == null) {
            lastUpdatedBy = user.getLastUpdatedBy();
        }
        if (locationCd == null) {
            locationCd = user.getLocationCd();
        }
        if (locationName == null) {
            locationName = user.getLocationName();
        }
        if (metadataTypeId == null) {
            metadataTypeId = user.getMetadataTypeId();
        }
        if (classification == null) {
            classification = user.getClassification();
        }
        if (middleInit == null) {
            middleInit = user.getMiddleInit();
        }
        if (prefix == null) {
            prefix = user.getPrefix();
        }
        if (sex == null) {
            sex = user.getSex();
        }
        if (status == null) {
            status = user.getStatus();
        }
        if (secondaryStatus == null) {
            secondaryStatus = user.getSecondaryStatus();
        }
        if (suffix == null) {
            suffix = user.getSuffix();
        }
        if (title == null) {
            title = user.getTitle();
        }
        if (userTypeInd == null) {
            userTypeInd = user.getUserTypeInd();
        }
        if (mailCode == null) {
            mailCode = user.getMailCode();
        }
        if (costCenter == null) {
            costCenter = user.getCostCenter();
        }
        if (startDate == null) {
            startDate = user.getStartDate();
        }
        if (lastDate == null) {
            lastDate = user.getLastDate();
        }
        if (nickname == null) {
            nickname = user.getNickname();
        }
        if (maidenName == null) {
            maidenName = user.getMaidenName();
        }
        if (passwordTheme == null) {
            passwordTheme = user.getPasswordTheme();
        }
        if (email == null) {
            email = user.getEmail();
        }
        if (showInSearch == null) {
            showInSearch = user.getShowInSearch();
        }

        if (alternateContactId == null) {
            alternateContactId = user.getAlternateContactId();
        }

        if (createdBy == null) {
            createdBy = user.getCreatedBy();
        }
        if (startDate == null) {
            startDate = user.getStartDate();
        }
        if (lastDate == null) {
            lastDate = user.getLastDate();
        }

        if (userOwnerId == null) {
            userOwnerId = user.getUserOwnerId();
        }
        if (dateChallengeRespChanged == null) {
            dateChallengeRespChanged = user.getDateChallengeRespChanged();
        }
        if (datePasswordChanged == null) {
            datePasswordChanged = user.getDatePasswordChanged();
        }
        if (dateITPolicyApproved == null) {
            dateITPolicyApproved = user.getDateITPolicyApproved();
        }
    }
    */
    /*
    public void addMemberGroup(final Group group) {
        if (group != null) {
            if (this.memberOfGroups == null) {
                this.memberOfGroups = new LinkedList<Group>();
            }
            this.memberOfGroups.add(group);
        }
    }

    public void addMemberRole(final Role role) {
        if (role != null) {
            if (this.memberOfRoles == null) {
                this.memberOfRoles = new LinkedList<Role>();
            }
            this.memberOfRoles.add(role);
        }
    }
    */
    /*
    public void addResourceUserAssociation(final UserResourceAssociation association) {
        if (association != null) {
            if (this.userResourceList == null) {
                this.userResourceList = new LinkedList<UserResourceAssociation>();
            }
            this.userResourceList.add(association);
        }
    }
    */

    // HACK
    public Organization getPrimaryOrganization() {
        Organization retVal = null;
        if (CollectionUtils.isNotEmpty(affiliations)) {
            for (final Organization organization : affiliations) {
                if (!AttributeOperationEnum.DELETE.equals(organization.getOperation())) {
                    if (organization.isOrganization()) {
                        retVal = organization;
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    public boolean isOrganizationMarkedAsDeleted(final String organizationId) {
        boolean retVal = false;
        if (CollectionUtils.isNotEmpty(affiliations)) {
            for (final Organization organization : affiliations) {
                if (AttributeOperationEnum.DELETE.equals(organization.getOperation())) {
                    if (StringUtils.equalsIgnoreCase(organizationId, organization.getId())) {
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
