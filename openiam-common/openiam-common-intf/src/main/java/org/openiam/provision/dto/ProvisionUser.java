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
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * ProvisionUser is the user object used by the provisioning service.
 *
 * @author suneet
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionUser", propOrder = {
        "memberOfGroups",
        "requestId",
        "sessionId",
        "memberOfRoles",
        "userResourceList",
        "userAffiliations",
        "srcSystemId",
        "provisionModel",
        "securityDomain",
        "notifyTargetSystems",
        "emailCredentialsToNewUsers",
        "emailCredentialsToSupervisor",
        "provisionOnStartDate",
        "addInitialPasswordToHistory",
        "passwordPolicy",
        "password",
        "skipPreprocessor",
        "skipPostProcessor"
})
public class ProvisionUser extends GenericProvisionObject<User> {
    /**
     *
     */
    private static final long serialVersionUID = 6441635701870724194L;
  //  protected List<Login> principalList;
    protected List<Group> memberOfGroups;
    protected List<Role> memberOfRoles;
    protected List<Organization> userAffiliations;

    protected List<UserResourceAssociation> userResourceList;

    public ProvisionModelEnum provisionModel;
    public String securityDomain;

    boolean emailCredentialsToNewUsers = false;
    boolean emailCredentialsToSupervisor = false;
    boolean addInitialPasswordToHistory = false;

    // default behaviour - you dont have to wait till the start date to provision a user
    // if this is set to true, the system will wait till the start date to provision the user
    boolean provisionOnStartDate = false;

    protected String requestId;
    protected String sessionId;

    // flags to skip over the service level pre and post processors
    boolean skipPreprocessor = false;
    boolean skipPostProcessor = false;

    /* ID of the system where this request came from.  If this value is set, then in the modify operation, that resource will not
     * be updated. */
    protected String srcSystemId;
    /* Flag that indicates if target systems should be updated or not
     */
    protected boolean notifyTargetSystems = true;

    protected Policy passwordPolicy = null;

    protected String password = null;

    public ProvisionUser() {
        this(new User());
    }

    public ProvisionUser(User user) {
        super(user);
        this.provisionObjectType = ProvisionObjectType.USER;
//        birthdate = user.getBirthdate();
//        companyId = user.getCompanyId();
//        companyOwnerId = user.getCompanyOwnerId();
//        createDate = user.getCreateDate();
//        createdBy = user.getCreatedBy();
//        deptCd = user.getDeptCd();
//        deptName = user.getDeptName();
//        employeeId = user.getEmployeeId();
//        employeeType = user.getEmployeeType();
//
//        firstName = user.getFirstName();
//        jobCode = user.getJobCode();
//        lastName = user.getLastName();
//        lastUpdate = user.getLastUpdate();
//        this.lastUpdatedBy = user.getLastUpdatedBy();
//        this.locationCd = user.getLocationCd();
//        this.locationName = user.getLocationName();
//        this.managerId = user.getManagerId();
//        this.metadataTypeId = user.getMetadataTypeId();
//        this.classification = user.getClassification();
//        this.middleInit = user.getMiddleInit();
//        this.prefix = user.getPrefix();
//        this.sex = user.getSex();
//        this.status = user.getStatus();
//        this.secondaryStatus = user.getSecondaryStatus();
//        this.suffix = user.getSuffix();
//        this.title = user.getTitle();
//        this.userId = user.getUserId();
//        this.userTypeInd = user.getUserTypeInd();
//        this.division = user.getDivision();
//        this.mailCode = user.getMailCode();
//        this.costCenter = user.getCostCenter();
//        this.startDate = user.getStartDate();
//        this.lastDate = user.getLastDate();
//        this.nickname = user.getNickname();
//        this.maidenName = user.getMaidenName();
//        this.passwordTheme = user.getPasswordTheme();
//        this.email = user.getEmail();
//        this.showInSearch = user.getShowInSearch();
//        this.alternateContactId = user.getAlternateContactId();
//
//        this.createdBy = user.getCreatedBy();
//        this.startDate = user.getStartDate();
//        this.lastDate = user.getLastDate();
//
//        this.userOwnerId = user.getUserOwnerId();
//        this.dateChallengeRespChanged = user.getDateChallengeRespChanged();
//        this.datePasswordChanged = user.getDatePasswordChanged();
//
//        userNotes = user.getUserNotes();
//        userAttributes = user.getUserAttributes();
//        phones = user.getPhones();
//        addresses = user.getAddresses();
        // set the email address in a hibernate friendly manner


    }

    public User getUser() {
        return this.getObject();
//        User user = new User();
//
//        user.setBirthdate(birthdate);
//        user.setCompanyId(companyId);
//        user.setCompanyOwnerId(companyOwnerId);
//        user.setCreateDate(createDate);
//        user.setCreatedBy(createdBy);
//        user.setDeptCd(deptCd);
//        user.setDeptName(deptName);
//        user.setEmployeeId(employeeId);
//        user.setEmployeeType(employeeType);
//
//        user.setFirstName(firstName);
//        user.setJobCode(jobCode);
//        user.setLastName(lastName);
//        user.setLastUpdate(lastUpdate);
//        user.setLastUpdatedBy(lastUpdatedBy);
//        user.setLocationCd(locationCd);
//        user.setLocationName(locationName);
//        user.setManagerId(managerId);
//        user.setMetadataTypeId(metadataTypeId);
//        user.setClassification(classification);
//        user.setMiddleInit(middleInit);
//        user.setPrefix(prefix);
//        user.setSex(sex);
//        user.setStatus(status);
//        user.setSecondaryStatus(secondaryStatus);
//        user.setSuffix(suffix);
//        user.setTitle(title);
//        user.setUserId(userId);
//        user.setUserTypeInd(userTypeInd);
//        user.setDivision(division);
//        user.setMailCode(mailCode);
//        user.setCostCenter(costCenter);
//        user.setStartDate(startDate);
//        user.setLastDate(lastDate);
//        user.setNickname(nickname);
//        user.setMaidenName(maidenName);
//        user.setPasswordTheme(passwordTheme);
//        user.setEmail(email);
//
//        user.setUserNotes(userNotes);
//        user.setUserAttributes(userAttributes);
//        user.setPhones(phones);
//        user.setAddresses(addresses);
//        user.setEmailAddresses(emailAddresses);
//        user.setAlternateContactId(alternateContactId);
//        user.setShowInSearch(showInSearch);
//
//        user.setUserOwnerId(userOwnerId);
//        user.setDateChallengeRespChanged(dateChallengeRespChanged);
//        user.setDatePasswordChanged(datePasswordChanged);
//
//        return user;
    }


    public Login getPrimaryPrincipal(String managedSysId) {
        if (getObject().getPrincipalList() == null) {
            return null;
        }
        for (Login l : getObject().getPrincipalList()) {
            if (l.getManagedSysId().equals(managedSysId)) {
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
        return "ProvisionUser{" +
                "memberOfGroups=" + memberOfGroups +
                ", memberOfRoles=" + memberOfRoles +
                ", userAffiliations=" + userAffiliations +
                ", userResourceList=" + userResourceList +
                ", provisionModel=" + provisionModel +
                ", securityDomain='" + securityDomain + '\'' +
                ", emailCredentialsToNewUsers=" + emailCredentialsToNewUsers +
                ", emailCredentialsToSupervisor=" + emailCredentialsToSupervisor +
                ", addInitialPasswordToHistory=" + addInitialPasswordToHistory +
                ", provisionOnStartDate=" + provisionOnStartDate +
                ", requestId='" + requestId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", skipPreprocessor=" + skipPreprocessor +
                ", skipPostProcessor=" + skipPostProcessor +
                ", srcSystemId='" + srcSystemId + '\'' +
                ", notifyTargetSystems=" + notifyTargetSystems +
                ", passwordPolicy=" + passwordPolicy +
                ", password='" + password + '\'' +
                '}';
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

    public List<Organization> getUserAffiliations() {
        return userAffiliations;
    }
    
    public void addUserAffiliation(final Organization organization) {
    	if(organization != null) {
	    	if(this.userAffiliations == null) {
	    		this.userAffiliations = new LinkedList<Organization>();
	    	}
	    	this.userAffiliations.add(organization);
    	}
    }

    public void setUserAffiliations(List<Organization> userAffiliations) {
        this.userAffiliations = userAffiliations;
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

    public List<UserResourceAssociation> getUserResourceList() {
        return userResourceList;
    }

    public void setUserResourceList(List<UserResourceAssociation> userResourceList) {
        this.userResourceList = userResourceList;
    }

    public Policy getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(Policy passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    /**
     * Its possible for the user to send service request which is missing most
     * of the values that a User already has This can cause the provisioning
     * scripts to fail
     *
     * @param user
     */
    public void updateMissingUserAttributes(User user) {

        if (getObject().getBirthdate() == null) {
            getObject().setBirthdate(user.getBirthdate());
        }
        if (getObject().getCompanyOwnerId() == null) {
            getObject().setCompanyOwnerId(user.getCompanyOwnerId());
        }
        if (getObject().getCreateDate() == null) {
            getObject().setCreateDate(user.getCreateDate());
        }
        if (getObject().getCreatedBy() == null) {
            getObject().setCreatedBy(user.getCreatedBy());
        }
        if (getObject().getEmployeeId() == null) {
            getObject().setEmployeeId(user.getEmployeeId());
        }
        if (getObject().getEmployeeType() == null) {
            getObject().setEmployeeType(user.getEmployeeType());
        }

        if (getObject().getFirstName() == null) {
            getObject().setFirstName(user.getFirstName());
        }
        if (getObject().getJobCode() == null) {
            getObject().setJobCode(user.getJobCode());
        }
        if (getObject().getLastName() == null) {
            getObject().setLastName(user.getLastName());
        }
        if (getObject().getLastUpdate() == null) {
            getObject().setLastUpdate(user.getLastUpdate());
        }
        if (getObject().getLastUpdatedBy() == null) {
            getObject().setLastUpdatedBy(user.getLastUpdatedBy());
        }
        if (getObject().getLocationCd() == null) {
            getObject().setLocationCd(user.getLocationCd());
        }
        if (getObject().getLocationName() == null) {
            getObject().setLocationName(user.getLocationName());
        }
        if (getObject().getManagerId() == null) {
            getObject().setManagerId(user.getManagerId());
        }
        if (getObject().getMetadataTypeId() == null) {
            getObject().setMetadataTypeId(user.getMetadataTypeId());
        }
        if (getObject().getClassification() == null) {
            getObject().setClassification(user.getClassification());
        }
        if (getObject().getMiddleInit() == null) {
            getObject().setMiddleInit(user.getMiddleInit());
        }
        if (getObject().getPrefix() == null) {
            getObject().setPrefix(user.getPrefix());
        }
        if (getObject().getSex() == null) {
            getObject().setSex(user.getSex());
        }
        if (getObject().getStatus() == null) {
            getObject().setStatus(user.getStatus());
        }
        if (getObject().getSecondaryStatus() == null) {
            getObject().setSecondaryStatus(user.getSecondaryStatus());
        }
        if (getObject().getSuffix() == null) {
            getObject().setSuffix(user.getSuffix());
        }
        if (getObject().getTitle() == null) {
            getObject().setTitle(user.getTitle());
        }
        if (getObject().getUserTypeInd() == null) {
            getObject().setUserTypeInd(user.getUserTypeInd());
        }
        if (getObject().getMailCode() == null) {
            getObject().setMailCode(user.getMailCode());
        }
        if (getObject().getCostCenter() == null) {
            getObject().setCostCenter(user.getCostCenter());
        }
        if (getObject().getStartDate() == null) {
            getObject().setStartDate(user.getStartDate());
        }
        if (getObject().getLastDate() == null) {
            getObject().setLastDate(user.getLastDate());
        }
        if (getObject().getNickname() == null) {
            getObject().setNickname(user.getNickname());
        }
        if (getObject().getMaidenName() == null) {
            getObject().setMaidenName(user.getMaidenName());
        }
        if (getObject().getPasswordTheme() == null) {
            getObject().setPasswordTheme(user.getPasswordTheme());
        }
        if (getObject().getEmail() == null) {
            getObject().setEmail(user.getEmail());
        }
        if (getObject().getShowInSearch() == null) {
            getObject().setShowInSearch(user.getShowInSearch());
        }

        if (getObject().getAlternateContactId() == null) {
            getObject().setAlternateContactId(user.getAlternateContactId());
        }

        if (getObject().getCreatedBy() == null) {
            getObject().setCreatedBy(user.getCreatedBy());
        }
        if (getObject().getStartDate() == null) {
            getObject().setStartDate(user.getStartDate());
        }
        if (getObject().getLastDate() == null) {
            getObject().setLastDate(user.getLastDate());
        }

        if (getObject().getUserOwnerId() == null) {
            getObject().setUserOwnerId(user.getUserOwnerId());
        }
        if (getObject().getDateChallengeRespChanged() == null) {
            getObject().setDateChallengeRespChanged(user.getDateChallengeRespChanged());
        }
        if (getObject().getDatePasswordChanged() == null) {
            getObject().setDatePasswordChanged(user.getDatePasswordChanged());
        }
        if (dateITPolicyApproved == null) {
            dateITPolicyApproved = user.getDateITPolicyApproved();
        }
    }
    
    public void addMemberGroup(final Group group) {
    	if(group != null) {
    		if(this.memberOfGroups == null) {
    			this.memberOfGroups = new LinkedList<Group>();
    		}
    		this.memberOfGroups.add(group);
    	}
    }
    
    public void addMemberRole(final Role role) {
    	if(role != null) {
    		if(this.memberOfRoles == null) {
    			this.memberOfRoles = new LinkedList<Role>();
    		}
    		this.memberOfRoles.add(role);
    	}
    }
    
    public void addResourceUserAssociation(final UserResourceAssociation association) {
    	if(association != null) {
    		if(this.userResourceList == null) {
    			this.userResourceList = new LinkedList<UserResourceAssociation>();
    		}
    		this.userResourceList.add(association);
    	}
    }
    
    //HACK
    public Organization getPrimaryOrganization() {
    	Organization retVal = null;
    	if(CollectionUtils.isNotEmpty(userAffiliations)) {
    		for(final Organization organization : userAffiliations) {
    			if(!AttributeOperationEnum.DELETE.equals(organization.getOperation())) {
    				if(organization.isOrganization()) {
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
    	if(CollectionUtils.isNotEmpty(userAffiliations)) {
    		for(final Organization organization : userAffiliations) {
    			if(AttributeOperationEnum.DELETE.equals(organization.getOperation())) {
    				if(StringUtils.equalsIgnoreCase(organizationId, organization.getId())) {
    					retVal = true;
    					break;
    				}
    			}
    		}
    	}
    	return retVal;
    }
}
