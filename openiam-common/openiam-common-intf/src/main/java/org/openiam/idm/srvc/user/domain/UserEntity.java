package org.openiam.idm.srvc.user.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseConstants;
import org.openiam.core.dao.lucene.LuceneId;
import org.openiam.core.dao.lucene.LuceneLastUpdate;
import org.openiam.core.dao.lucene.bridge.OrganizationBridge;
import org.openiam.core.domain.UserKey;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.UserAffiliationEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

@Entity
@FilterDef(name = "parentTypeFilter", parameters = @ParamDef(name = "parentFilter", type = "string"))
@Table(name = "USERS")
@DozerDTOCorrespondence(User.class)
@Indexed
public class UserEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "USER_ID", length = 32, nullable = false)
    @LuceneId
    @DocumentId
    private String userId;

    @Column(name = "BIRTHDATE", length = 19)
    private Date birthdate;

    @Column(name = "COMPANY_OWNER_ID", length = 32)
    private String companyOwnerId;

    @Column(name = "CREATE_DATE", length = 19)
    private Date createDate;

    @Column(name = "CREATED_BY", length = 32)
    private String createdBy;

    @Column(name = "EMPLOYEE_ID", length = 32)
    @Fields ({
            @Field(index = Index.TOKENIZED),
            @Field(name = "employeeId", index = Index.TOKENIZED, store = Store.YES)
    })
    @Size(max = 32, message = "validator.user.employee.id.toolong")
    private String employeeId;

    @Column(name = "EMPLOYEE_TYPE", length = 20)
    @Size(max = 20, message = "validator.user.employee.type.toolong")
    private String employeeType;

    @Column(name = "FIRST_NAME", length = 50)
    @Fields ({
        @Field(index = Index.TOKENIZED),
        @Field(name = "firstName", index = Index.TOKENIZED, store = Store.YES)
    })
    @Size(max = 50, message = "validator.user.first.name.toolong")
    private String firstName;

    @Column(name = "JOB_CODE", length = 50)
    @Size(max = 50, message = "validator.user.job.code.toolong")
    private String jobCode;

    @Column(name = "LAST_NAME", length = 50)
    @Fields ({
        @Field(index = Index.TOKENIZED),
        @Field(name = "lastName", index = Index.TOKENIZED, store = Store.YES)
    })
    @Size(max = 50, message = "validator.user.last.name.toolong")
    private String lastName;

    @Column(name = "LAST_UPDATE", length = 19)
    @LuceneLastUpdate
    private Date lastUpdate;

    @Column(name = "LAST_UPDATED_BY", length = 32)
    private String lastUpdatedBy;

    @Column(name = "LOCATION_CD", length = 50)
    @Size(max = 50, message = "validator.user.location.code.toolong")
    private String locationCd;

    @Column(name = "LOCATION_NAME", length = 100)
    @Size(max = 100, message = "validator.user.location.name.toolong")
    private String locationName;

    @Column(name = "TYPE_ID", length = 20)
    @Size(max = 20, message = "validator.user.metadata.type.id.toolong")
    private String metadataTypeId;

    @Column(name = "CLASSIFICATION", length = 20)
    @Size(max = 20, message = "validator.user.classification.toolong")
    private String classification;

    @Column(name = "MIDDLE_INIT", length = 50)
    @Size(max = 50, message = "validator.user.middle.init.toolong")
    private String middleInit;

    @Column(name = "PREFIX", length = 4)
    @Size(max = 4, message = "validator.user.prefix.toolong")
    private String prefix;

    @Column(name = "SEX", length = 1)
    @Size(max = 1, message = "validator.user.sex.toolong")
    private String sex;

    @Column(name = "STATUS", length = 40)
    @Enumerated(EnumType.STRING)
    @Field(index=Index.UN_TOKENIZED, name="userStatus", store=Store.YES)
    private UserStatusEnum status;

    @Column(name = "SECONDARY_STATUS", length = 40)
    @Enumerated(EnumType.STRING)
    @Field(index=Index.UN_TOKENIZED ,name="accountStatus", store=Store.YES)
    private UserStatusEnum secondaryStatus;

    @Column(name = "SUFFIX", length = 20)
    @Size(max = 20, message = "validator.user.suffix.toolong")
    private String suffix;

    @Column(name = "TITLE", length = 30)
    @Size(max = 30, message = "validator.user.title.toolong")
    private String title;

    @Column(name = "USER_TYPE_IND", length = 20)
    @Size(max = 20, message = "validator.user.type.identifier.toolong")
    private String userTypeInd;

    @Column(name = "MAIL_CODE", length = 10)
    @Size(max = 10, message = "validator.user.mailcode.toolong")
    private String mailCode;

    @Column(name = "COST_CENTER", length = 20)
    @Size(max = 20, message = "validator.user.cost.center.toolong")
    private String costCenter;

    @Column(name = "START_DATE", length = 10)
    private Date startDate;

    @Column(name = "LAST_DATE", length = 10)
    private Date lastDate;

    @Column(name = "NICKNAME", length = 40)
    @Size(max = 40, message = "validator.user.nick.name.toolong")
    private String nickname;

    @Column(name = "MAIDEN_NAME", length = 40)
    @Size(max = 40, message = "validator.user.maiden.name.toolong")
    private String maidenName;

    @Column(name = "PASSWORD_THEME", length = 20)
    private String passwordTheme;

    @Column(name = "SHOW_IN_SEARCH")
    private Integer showInSearch = new Integer(0);

    @Column(name = "ALTERNATE_ID", length = 32)
    private String alternateContactId;

    @Column(name = "USER_OWNER_ID")
    private String userOwnerId;

    @Column(name = "DATE_PASSWORD_CHANGED", length = 10)
    private Date datePasswordChanged;

    @Column(name = "DATE_CHALLENGE_RESP_CHANGED", length = 10)
    private Date dateChallengeRespChanged;

    @Column(name = "DATE_IT_POLICY_APPROVED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateITPolicyApproved;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserNoteEntity> userNotes = new HashSet<UserNoteEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @MapKey(name = "name")
    private Map<String, UserAttributeEntity> userAttributes = new HashMap<String, UserAttributeEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    /*
    @Filter(
            name = "parentTypeFilter",
            condition = ":parentFilter = PARENT_TYPE"
    )
    */
    private Set<AddressEntity> addresses = new HashSet<AddressEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    /*
    @Filter(
            name = "parentTypeFilter",
            condition = ":parentFilter = PARENT_TYPE"
    )
    */
    private Set<PhoneEntity> phones = new HashSet<PhoneEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    /*
    @Filter(
            name = "parentTypeFilter",
            condition = ":parentFilter = PARENT_TYPE"
    )
    */
    private Set<EmailAddressEntity> emailAddresses = new HashSet<EmailAddressEntity>(0);

    @Column(name = "SYSTEM_FLAG",length = 1)
    private String systemFlag;

    //@IndexedEmbedded(prefix="principal.", depth=1)
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID", referencedColumnName="USER_ID")
    @Fetch(FetchMode.SUBSELECT)
    private List<LoginEntity> principalList = new LinkedList<LoginEntity>();

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID", referencedColumnName="USER_ID")
    @Fetch(FetchMode.SUBSELECT)
    protected Set<UserKey> userKeys = new HashSet<UserKey>(0);

    //@IndexedEmbedded(prefix="groups.")
    @OneToMany(mappedBy = "user")
    @Fetch(FetchMode.SUBSELECT)
    private Set<UserGroupEntity> userGroups = new HashSet<UserGroupEntity>(0);
    
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID", referencedColumnName="USER_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceUserEntity> resourceUsers = new HashSet<ResourceUserEntity>();

    //@IndexedEmbedded(prefix="roles.")
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID", referencedColumnName="USER_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Set<UserRoleEntity> userRoles = new HashSet<UserRoleEntity>(0);
    
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "user")
	private Set<UserAffiliationEntity> affiliations;

    public UserEntity() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getCompanyOwnerId() {
        return companyOwnerId;
    }

    public void setCompanyOwnerId(String companyOwnerId) {
        this.companyOwnerId = companyOwnerId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getLocationCd() {
        return locationCd;
    }

    public void setLocationCd(String locationCd) {
        this.locationCd = locationCd;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getMiddleInit() {
        return middleInit;
    }

    public void setMiddleInit(String middleInit) {
        this.middleInit = middleInit;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public UserStatusEnum getStatus() {
        return status;
    }

    public void setStatus(UserStatusEnum status) {
        this.status = status;
    }

    public UserStatusEnum getSecondaryStatus() {
        return secondaryStatus;
    }

    public void setSecondaryStatus(UserStatusEnum secondaryStatus) {
        this.secondaryStatus = secondaryStatus;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserTypeInd() {
        return userTypeInd;
    }

    public void setUserTypeInd(String userTypeInd) {
        this.userTypeInd = userTypeInd;
    }

    public String getMailCode() {
        return mailCode;
    }

    public void setMailCode(String mailCode) {
        this.mailCode = mailCode;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public String getPasswordTheme() {
        return passwordTheme;
    }

    public void setPasswordTheme(String passwordTheme) {
        this.passwordTheme = passwordTheme;
    }
    
    public String getDisplayName() {
    	String displayName = null;
    	if(StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
    		displayName = String.format("%s %s", firstName, lastName);
    	} else if(StringUtils.isNotBlank(firstName)) {
    		displayName = firstName;
    	} else if(StringUtils.isNotBlank(lastName)) {
    		displayName = lastName;
    	}
    	return displayName;
    }

//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public String getBldgNum() {
//        return bldgNum;
//    }
//
//    public void setBldgNum(String bldgNum) {
//        this.bldgNum = bldgNum;
//    }
//
//    public String getStreetDirection() {
//        return streetDirection;
//    }
//
//    public void setStreetDirection(String streetDirection) {
//        this.streetDirection = streetDirection;
//    }
//
//    public String getSuite() {
//        return suite;
//    }
//
//    public void setSuite(String suite) {
//        this.suite = suite;
//    }
//
//    public String getAddress1() {
//        return address1;
//    }
//
//    public void setAddress1(String address1) {
//        this.address1 = address1;
//    }
//
//    public String getAddress2() {
//        return address2;
//    }
//
//    public void setAddress2(String address2) {
//        this.address2 = address2;
//    }
//
//    public String getAddress3() {
//        return address3;
//    }
//
//    public void setAddress3(String address3) {
//        this.address3 = address3;
//    }
//
//    public String getAddress4() {
//        return address4;
//    }
//
//    public void setAddress4(String address4) {
//        this.address4 = address4;
//    }
//
//    public String getAddress5() {
//        return address5;
//    }
//
//    public void setAddress5(String address5) {
//        this.address5 = address5;
//    }
//
//    public String getAddress6() {
//        return address6;
//    }
//
//    public void setAddress6(String address6) {
//        this.address6 = address6;
//    }
//
//    public String getAddress7() {
//        return address7;
//    }
//
//    public void setAddress7(String address7) {
//        this.address7 = address7;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getPostalCd() {
//        return postalCd;
//    }
//
//    public void setPostalCd(String postalCd) {
//        this.postalCd = postalCd;
//    }
    @Deprecated
    public String getEmail() {
        String defaultEmail = null;
        if(this.emailAddresses!=null && !this.emailAddresses.isEmpty()){
            for (EmailAddressEntity email: this.emailAddresses){
                   if(email.getIsDefault()){
                       defaultEmail = email.getEmailAddress();
                       break;
                   }
            }
        }
        return defaultEmail;
    }

//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getAreaCd() {
//        return areaCd;
//    }
//
//    public void setAreaCd(String areaCd) {
//        this.areaCd = areaCd;
//    }
//
//    public String getCountryCd() {
//        return countryCd;
//    }
//
//    public void setCountryCd(String countryCd) {
//        this.countryCd = countryCd;
//    }
//
//    public String getPhoneNbr() {
//        return phoneNbr;
//    }
//
//    public void setPhoneNbr(String phoneNbr) {
//        this.phoneNbr = phoneNbr;
//    }
//
//    public String getPhoneExt() {
//        return phoneExt;
//    }
//
//    public void setPhoneExt(String phoneExt) {
//        this.phoneExt = phoneExt;
//    }

    public Integer getShowInSearch() {
        return showInSearch;
    }

    public void setShowInSearch(Integer showInSearch) {
        this.showInSearch = showInSearch;
    }

    public String getAlternateContactId() {
        return alternateContactId;
    }

    public void setAlternateContactId(String alternateContactId) {
        this.alternateContactId = alternateContactId;
    }

    public String getUserOwnerId() {
        return userOwnerId;
    }

    public void setUserOwnerId(String userOwnerId) {
        this.userOwnerId = userOwnerId;
    }

    public Date getDatePasswordChanged() {
        return datePasswordChanged;
    }

    public void setDatePasswordChanged(Date datePasswordChanged) {
        this.datePasswordChanged = datePasswordChanged;
    }

    public Date getDateChallengeRespChanged() {
        return dateChallengeRespChanged;
    }

    public void setDateChallengeRespChanged(Date dateChallengeRespChanged) {
        this.dateChallengeRespChanged = dateChallengeRespChanged;
    }

    public Date getDateITPolicyApproved() {
        return dateITPolicyApproved;
    }

    public void setDateITPolicyApproved(Date dateITPolicyApproved) {
        this.dateITPolicyApproved = dateITPolicyApproved;
    }

    public Set<UserNoteEntity> getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(Set<UserNoteEntity> userNotes) {
        this.userNotes = userNotes;
    }

    public Map<String, UserAttributeEntity> getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(Map<String, UserAttributeEntity> userAttributes) {
        this.userAttributes = userAttributes;
    }

    public Set<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<AddressEntity> addresses) {
        this.addresses = addresses;
    }

    public Set<PhoneEntity> getPhones() {
        return phones;
    }

    public void setPhones(Set<PhoneEntity> phones) {
        this.phones = phones;
    }

    public Set<EmailAddressEntity> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(Set<EmailAddressEntity> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public String getSystemFlag() {
        return systemFlag;
    }

    public void setSystemFlag(String systemFlag) {
        this.systemFlag = systemFlag;
    }

    public List<LoginEntity> getPrincipalList() {
        return principalList;
    }
    
    public void addLogin(final LoginEntity loginEntity) {
    	if(loginEntity != null) {
    		if(this.principalList == null) {
    			this.principalList = new LinkedList<LoginEntity>();
    		}
    		this.principalList.add(loginEntity);
    	}
    }

    public void setPrincipalList(List<LoginEntity> principalList) {
        this.principalList = principalList;
    }

    public Set<UserKey> getUserKeys() {
        return userKeys;
    }

    public void setUserKeys(Set<UserKey> userKeys) {
        this.userKeys = userKeys;
    }

    public Set<UserGroupEntity> getUserGroups() {
        return userGroups;
    }
    
    public boolean isUserInGroup(final String groupId) {
    	boolean retVal = false;
    	if(userGroups != null) {
    		for(final Iterator<UserGroupEntity> it = userGroups.iterator(); it.hasNext();) {
    			final UserGroupEntity entity = it.next();
    			if(entity != null) {
    				if(StringUtils.equals(entity.getGrpId(), groupId)) {
    					retVal = true;
    					break;
    				}
    			}
    		}
    	}
    	return retVal;
    }
    
    public void removeUserFromGroup(final String groupId) {
    	if(userGroups != null) {
    		for(final Iterator<UserGroupEntity> it = userGroups.iterator(); it.hasNext();) {
    			final UserGroupEntity entity = it.next();
    			if(entity != null) {
    				if(StringUtils.equals(entity.getGrpId(), groupId)) {
    					it.remove();
    					break;
    				}
    			}
    		}
    	}
    }
    
    public void addUserAttribute(final UserAttributeEntity entity) {
    	if(entity != null) {
    		if(this.userAttributes == null) {
    			this.userAttributes = new HashMap<String, UserAttributeEntity>();
    		}
    		entity.setUser(this);
    		this.userAttributes.put(entity.getName(), entity);
    	}
    }
    
    public void removeUserAttribute(final String id) {
    	if(id != null && this.userAttributes != null) {
    		final Set<Entry<String, UserAttributeEntity>> entrySet = this.userAttributes.entrySet();
    		if(entrySet != null) {
    			for(final Iterator<Entry<String, UserAttributeEntity>> it = entrySet.iterator(); it.hasNext();) {
    				final Entry<String, UserAttributeEntity> entry = it.next();
    				final UserAttributeEntity value = entry.getValue();
    				if(value != null && StringUtils.equals(value.getId(), id)) {
    					it.remove();
    					break;
    				}
    			}
    		}
    	}
    }
    
    public void updateUserAttribute(final UserAttributeEntity entity) {
    	if(entity != null && this.userAttributes != null) {
    		final UserAttributeEntity attribute = this.userAttributes.get(entity.getName());
    		if(attribute != null) {
    			attribute.setElement(entity.getElement());
    			attribute.setValue(entity.getValue());
    		}
    	}
    }

    public void setUserGroups(Set<UserGroupEntity> userGroups) {
        this.userGroups = userGroups;
    }

    public Set<UserRoleEntity> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRoleEntity> userRoles) {
        this.userRoles = userRoles;
    }

    public Set<ResourceUserEntity> getResourceUsers() {
		return resourceUsers;
	}

	public void setResourceUsers(Set<ResourceUserEntity> resourceUsers) {
		this.resourceUsers = resourceUsers;
	}

	public Set<UserAffiliationEntity> getAffiliations() {
		return affiliations;
	}

	public void setAffiliations(Set<UserAffiliationEntity> affiliations) {
		this.affiliations = affiliations;
	}

	public void updateUser(UserEntity newUser) {
	    if (newUser.getBirthdate() != null) {
	        if (newUser.getBirthdate().equals(BaseConstants.NULL_DATE)) {
	            this.birthdate = null;
	        } else {
	            this.birthdate = newUser.getBirthdate();
	        }
	    }
	    if (newUser.getClassification() != null) {
	        if (newUser.getClassification().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.classification = null;
	        } else {
	            this.classification = newUser.getClassification();
	        }
	    }
	    if (newUser.getCostCenter() != null) {
	        if (newUser.getCostCenter().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.costCenter = null;
	        } else {
	            this.costCenter = newUser.getCostCenter();
	        }
	    }
	   
	    if (newUser.getEmployeeId() != null) {
	        if (newUser.getEmployeeId().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.employeeId = null;
	        } else {
	            this.employeeId = newUser.getEmployeeId();
	        }
	    }
	    if (newUser.getEmployeeType() != null) {
	        if (newUser.getEmployeeType().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.employeeType = null;
	        } else {
	            this.employeeType = newUser.getEmployeeType();
	        }
	    }
	    if (newUser.getFirstName() != null) {
	        if (newUser.getFirstName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.firstName = null;
	        } else {
	            this.firstName = newUser.getFirstName();
	        }
	    }
	    if (newUser.getJobCode() != null) {
	        if (newUser.getJobCode().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.jobCode = null;
	        } else {
	            this.jobCode = newUser.getJobCode();
	        }
	    }
	    if (newUser.getLastName() != null) {
	        if (newUser.getLastName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.lastName = null;
	        } else {
	            this.lastName = newUser.getLastName();
	        }
	    }
	    if (newUser.getLastDate() != null) {
	        if (newUser.getLastDate().equals(BaseConstants.NULL_DATE)) {
	            this.lastDate = null;
	        } else {
	            this.lastDate = newUser.getLastDate();
	        }
	    }
	    if (newUser.getLocationCd() != null) {
	        if (newUser.getLocationCd().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.locationCd = null;
	        } else {
	            this.locationCd = newUser.getLocationCd();
	        }
	    }
	    if (newUser.getLocationName() != null) {
	        if (newUser.getLocationName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.locationName = null;
	        } else {
	            this.locationName = newUser.getLocationName();
	        }
	    }
	    if (newUser.getMaidenName() != null) {
	        if (newUser.getMaidenName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.maidenName = null;
	        } else {
	            this.maidenName = newUser.getMaidenName();
	        }
	    }
	    if (newUser.getMailCode() != null) {
	        if (newUser.getMailCode().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.mailCode = newUser.getMailCode();
	        } else {
	            this.mailCode = null;
	        }
	    }
	    if (newUser.getMetadataTypeId() != null) {
	        if (newUser.getMetadataTypeId().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.metadataTypeId = null;
	        } else {
	            this.metadataTypeId = newUser.getMetadataTypeId();
	        }
	    }
	    if (newUser.getMiddleInit() != null) {
	        if (newUser.getMiddleInit().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.middleInit = null;
	        } else {
	            this.middleInit = newUser.getMiddleInit();
	        }
	    }
	    if (newUser.getNickname() != null) {
	        if (newUser.getNickname().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.nickname = null;
	        } else {
	            this.nickname = newUser.getNickname();
	        }
	    }
	    if (newUser.getPasswordTheme() != null) {
	        if (newUser.getPasswordTheme().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.passwordTheme = null;
	        } else {
	            this.passwordTheme = newUser.getPasswordTheme();
	        }
	    }
	    if (newUser.getPrefix() != null) {
	        if (newUser.getPrefix().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.prefix = null;
	        } else {
	            this.prefix = newUser.getPrefix();
	        }
	    }
	    if (newUser.getSecondaryStatus() != null) {
	        this.secondaryStatus = newUser.getSecondaryStatus();
	    }
	    if (newUser.getSex() != null) {
	        if (newUser.getSex().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.sex = null;
	        } else {
	            this.sex = newUser.getSex();
	        }
	    }
	    if (newUser.getStartDate() != null) {
	        if (newUser.getStartDate().equals(BaseConstants.NULL_DATE)) {
	            this.startDate = null;
	        } else {
	            this.startDate = newUser.getStartDate();
	        }
	    }
	
	    if (newUser.getStatus() != null) {
	        this.status = newUser.getStatus();
	    }
	    if (newUser.getSuffix() != null) {
	        if (newUser.getSuffix().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.suffix = null;
	        } else {
	            this.suffix = newUser.getSuffix();
	        }
	    }
	    if (newUser.getShowInSearch() != null) {
	        if (newUser.getShowInSearch().equals(BaseConstants.NULL_INTEGER)) {
	            this.showInSearch = 0;
	        } else {
	            this.showInSearch = newUser.getShowInSearch();
	        }
	    }
	    if (newUser.getTitle() != null) {
	        if (newUser.getTitle().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.title = null;
	        } else {
	            this.title = newUser.getTitle();
	        }
	    }
	    if (newUser.getUserTypeInd() != null) {
	        if (newUser.getUserTypeInd().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.userTypeInd = null;
	        } else {
	            this.userTypeInd = newUser.getUserTypeInd();
	        }
	    }
	    if (newUser.getAlternateContactId() != null) {
	        if (newUser.getAlternateContactId().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.alternateContactId = null;
	        } else {
	            this.alternateContactId = newUser.getAlternateContactId();
	        }
	    }
	
	    if (newUser.getUserOwnerId() != null) {
	        if (newUser.getUserOwnerId().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.userOwnerId = null;
	        } else {
	            this.userOwnerId = newUser.getUserOwnerId();
	        }
	    }
	    if (newUser.getDateChallengeRespChanged() != null) {
	        if (newUser.getDateChallengeRespChanged().equals(BaseConstants.NULL_DATE)) {
	            this.dateChallengeRespChanged = null;
	        } else {
	            this.dateChallengeRespChanged = newUser.getDateChallengeRespChanged();
	        }
	    }
	    if (newUser.getDatePasswordChanged() != null) {
	        if (newUser.getDatePasswordChanged().equals(BaseConstants.NULL_DATE)) {
	            this.datePasswordChanged = null;
	        } else {
	            this.datePasswordChanged = newUser.getDatePasswordChanged();
	        }
	    }
        if (newUser.getDateITPolicyApproved() != null) {
            if (newUser.getDateITPolicyApproved().equals(BaseConstants.NULL_DATE)) {
                this.dateITPolicyApproved = null;
            } else {
                this.dateITPolicyApproved = newUser.getDateITPolicyApproved();
            }
        }
	
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((alternateContactId == null) ? 0 : alternateContactId
						.hashCode());
		result = prime * result
				+ ((birthdate == null) ? 0 : birthdate.hashCode());
		result = prime * result
				+ ((classification == null) ? 0 : classification.hashCode());
		result = prime * result
				+ ((companyOwnerId == null) ? 0 : companyOwnerId.hashCode());
		result = prime * result
				+ ((costCenter == null) ? 0 : costCenter.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime
				* result
				+ ((dateChallengeRespChanged == null) ? 0
						: dateChallengeRespChanged.hashCode());
        result = prime
                * result
                + ((dateITPolicyApproved == null) ? 0
                        : dateITPolicyApproved.hashCode());
		result = prime
				* result
				+ ((datePasswordChanged == null) ? 0 : datePasswordChanged
						.hashCode());
		result = prime * result
				+ ((employeeId == null) ? 0 : employeeId.hashCode());
		result = prime * result
				+ ((employeeType == null) ? 0 : employeeType.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((jobCode == null) ? 0 : jobCode.hashCode());
		result = prime * result
				+ ((lastDate == null) ? 0 : lastDate.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result
				+ ((lastUpdatedBy == null) ? 0 : lastUpdatedBy.hashCode());
		result = prime * result
				+ ((locationCd == null) ? 0 : locationCd.hashCode());
		result = prime * result
				+ ((locationName == null) ? 0 : locationName.hashCode());
		result = prime * result
				+ ((maidenName == null) ? 0 : maidenName.hashCode());
		result = prime * result
				+ ((mailCode == null) ? 0 : mailCode.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime * result
				+ ((middleInit == null) ? 0 : middleInit.hashCode());
		result = prime * result
				+ ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result
				+ ((passwordTheme == null) ? 0 : passwordTheme.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result
				+ ((secondaryStatus == null) ? 0 : secondaryStatus.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		result = prime * result
				+ ((showInSearch == null) ? 0 : showInSearch.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		result = prime * result
				+ ((systemFlag == null) ? 0 : systemFlag.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result
				+ ((userOwnerId == null) ? 0 : userOwnerId.hashCode());
		result = prime * result
				+ ((userTypeInd == null) ? 0 : userTypeInd.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserEntity other = (UserEntity) obj;
		if (alternateContactId == null) {
			if (other.alternateContactId != null)
				return false;
		} else if (!alternateContactId.equals(other.alternateContactId))
			return false;
		if (birthdate == null) {
			if (other.birthdate != null)
				return false;
		} else if (!birthdate.equals(other.birthdate))
			return false;
		if (classification == null) {
			if (other.classification != null)
				return false;
		} else if (!classification.equals(other.classification))
			return false;
		if (companyOwnerId == null) {
			if (other.companyOwnerId != null)
				return false;
		} else if (!companyOwnerId.equals(other.companyOwnerId))
			return false;
		if (costCenter == null) {
			if (other.costCenter != null)
				return false;
		} else if (!costCenter.equals(other.costCenter))
			return false;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (dateChallengeRespChanged == null) {
			if (other.dateChallengeRespChanged != null)
				return false;
		} else if (!dateChallengeRespChanged
				.equals(other.dateChallengeRespChanged))
			return false;
        if (dateITPolicyApproved == null) {
            if (other.dateITPolicyApproved != null)
                return false;
        } else if (!dateITPolicyApproved
                .equals(other.dateITPolicyApproved))
            return false;
		if (datePasswordChanged == null) {
			if (other.datePasswordChanged != null)
				return false;
		} else if (!datePasswordChanged.equals(other.datePasswordChanged))
			return false;
		if (employeeId == null) {
			if (other.employeeId != null)
				return false;
		} else if (!employeeId.equals(other.employeeId))
			return false;
		if (employeeType == null) {
			if (other.employeeType != null)
				return false;
		} else if (!employeeType.equals(other.employeeType))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (jobCode == null) {
			if (other.jobCode != null)
				return false;
		} else if (!jobCode.equals(other.jobCode))
			return false;
		if (lastDate == null) {
			if (other.lastDate != null)
				return false;
		} else if (!lastDate.equals(other.lastDate))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (lastUpdatedBy == null) {
			if (other.lastUpdatedBy != null)
				return false;
		} else if (!lastUpdatedBy.equals(other.lastUpdatedBy))
			return false;
		if (locationCd == null) {
			if (other.locationCd != null)
				return false;
		} else if (!locationCd.equals(other.locationCd))
			return false;
		if (locationName == null) {
			if (other.locationName != null)
				return false;
		} else if (!locationName.equals(other.locationName))
			return false;
		if (maidenName == null) {
			if (other.maidenName != null)
				return false;
		} else if (!maidenName.equals(other.maidenName))
			return false;
		if (mailCode == null) {
			if (other.mailCode != null)
				return false;
		} else if (!mailCode.equals(other.mailCode))
			return false;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		if (middleInit == null) {
			if (other.middleInit != null)
				return false;
		} else if (!middleInit.equals(other.middleInit))
			return false;
		if (nickname == null) {
			if (other.nickname != null)
				return false;
		} else if (!nickname.equals(other.nickname))
			return false;
		if (passwordTheme == null) {
			if (other.passwordTheme != null)
				return false;
		} else if (!passwordTheme.equals(other.passwordTheme))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (secondaryStatus != other.secondaryStatus)
			return false;
		if (sex == null) {
			if (other.sex != null)
				return false;
		} else if (!sex.equals(other.sex))
			return false;
		if (showInSearch == null) {
			if (other.showInSearch != null)
				return false;
		} else if (!showInSearch.equals(other.showInSearch))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (status != other.status)
			return false;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		if (systemFlag == null) {
			if (other.systemFlag != null)
				return false;
		} else if (!systemFlag.equals(other.systemFlag))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (userOwnerId == null) {
			if (other.userOwnerId != null)
				return false;
		} else if (!userOwnerId.equals(other.userOwnerId))
			return false;
		if (userTypeInd == null) {
			if (other.userTypeInd != null)
				return false;
		} else if (!userTypeInd.equals(other.userTypeInd))
			return false;
		return true;
	}

	
}
