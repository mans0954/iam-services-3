package org.openiam.idm.srvc.user.domain;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.openiam.base.BaseConstants;
import org.openiam.base.domain.KeyEntity;
import org.openiam.core.dao.lucene.LuceneLastUpdate;
import org.openiam.core.domain.UserKey;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.annotation.*;
import org.openiam.elasticsearch.bridge.MetadataTypeBridge;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.constants.ElasticsearchStore;
import org.openiam.elasticsearch.constants.Index;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RoleToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.role.domain.RoleToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.internationalization.Internationalized;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import java.util.*;
import java.util.Map.Entry;

//import org.hibernate.search.annotations.*;
//import org.hibernate.search.annotations.Index;

@Entity
@FilterDef(name = "parentTypeFilter", parameters = @ParamDef(name = "parentFilter", type = "string"))
@Table(name = "USERS")
@DozerDTOCorrespondence(User.class)
//@Indexed
@Internationalized
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ElasticsearchIndex(indexName = ESIndexName.USERS)
@ElasticsearchMapping(typeName = ESIndexType.USER)
@AttributeOverride(name = "id", column = @Column(name = "USER_ID"))
public class UserEntity extends KeyEntity {

    @Column(name = "BIRTHDATE", length = 19)
    private Date birthdate;

    @Column(name = "COMPANY_OWNER_ID", length = 32)
    private String companyOwnerId;

    @Column(name = "CREATE_DATE", length = 19)
    private Date createDate;

    @Column(name = "CREATED_BY", length = 32)
    private String createdBy;

    @Column(name = "EMPLOYEE_ID", length = 32)
    @ElasticsearchField(name = "employeeId", store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    @Size(max = 32, message = "validator.user.employee.id.toolong")
    private String employeeId;

//    @Column(name = "EMPLOYEE_TYPE", length = 20)
//    @Size(max = 20, message = "validator.user.employee.type.toolong")
//    @Field(index=Index.UN_TOKENIZED, name="employeeType", store=Store.YES)
//    private String employeeType;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_TYPE", insertable = true, updatable = true, nullable=true)
    @Internationalized
    @ElasticsearchField(name = "employeeType", bridge=@ElasticsearchFieldBridge(impl = MetadataTypeBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
//    @IndexedEmbedded
    private MetadataTypeEntity employeeType;

    @Column(name = "FIRST_NAME", length = 50)
//    @ElasticsearchField(name = "firstName", store = ElasticsearchStore.Yes, index = Index.Analyzed)
    @ElasticsearchFields(fields = {@ElasticsearchField(name = "firstName", store = ElasticsearchStore.Yes, index = Index.Not_Analyzed),
                                   @ElasticsearchField(name = "firstNameTokenized", store = ElasticsearchStore.Yes, index = Index.Analyzed)})
    @Size(max = 50, message = "validator.user.first.name.toolong")
    private String firstName;

//    @Column(name = "JOB_CODE", length = 50)
//    @Size(max = 50, message = "validator.user.job.code.toolong")
//    private String jobCode;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "JOB_CODE", insertable = true, updatable = true, nullable=true)
    @Internationalized
    @ElasticsearchField(name = "jobCode", bridge=@ElasticsearchFieldBridge(impl = MetadataTypeBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
//    @IndexedEmbedded
    private MetadataTypeEntity jobCode;

    @Column(name = "LAST_NAME", length = 50)
//    @ElasticsearchField(name = "lastName", store = ElasticsearchStore.Yes, index = Index.Analyzed)
    @ElasticsearchFields(fields = {@ElasticsearchField(name = "lastName", store = ElasticsearchStore.Yes, index = Index.Not_Analyzed),
                                   @ElasticsearchField(name = "lastNameTokenized", store = ElasticsearchStore.Yes, index = Index.Analyzed)})
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

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID", insertable = true, updatable = true, nullable=true)
    @Internationalized
//    @IndexedEmbedded
    @ElasticsearchField(name = "type", bridge=@ElasticsearchFieldBridge(impl = MetadataTypeBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    protected MetadataTypeEntity type;

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
    @ElasticsearchField(name = "userStatus", store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    private UserStatusEnum status;

    @Column(name = "SECONDARY_STATUS", length = 40)
    @Enumerated(EnumType.STRING)
    @ElasticsearchField(name = "accountStatus", store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    private UserStatusEnum secondaryStatus;

    @Column(name = "SUFFIX", length = 20)
    @Size(max = 20, message = "validator.user.suffix.toolong")
    private String suffix;

    @Column(name = "TITLE", length = 100)
    @Size(max = 100, message = "validator.user.title.toolong")
    private String title;

    @Column(name = "USER_TYPE_IND", length = 20)
    @Size(max = 20, message = "validator.user.type.identifier.toolong")
    private String userTypeInd;

    @Column(name = "MAIL_CODE", length = 100)
    @Size(max = 100, message = "validator.user.mailcode.toolong")
    private String mailCode;

    @Column(name = "COST_CENTER", length = 20)
    @Size(max = 20, message = "validator.user.cost.center.toolong")
    private String costCenter;

    @Column(name = "START_DATE", length = 10)
    private Date startDate;

    @Column(name = "LAST_DATE", length = 10)
    private Date lastDate;

    @Column(name = "CLAIM_DATE", length = 10)
    private Date claimDate;

    @Column(name = "NICKNAME", length = 40)
    @Size(max = 100, message = "validator.user.nick.name.toolong")
    private String nickname;

    @Column(name = "MAIDEN_NAME", length = 40)
    @Size(max = 40, message = "validator.user.maiden.name.toolong")
//    @ElasticsearchField(name = "maidenName", store = ElasticsearchStore.Yes, index = Index.Analyzed)
    @ElasticsearchFields(fields = {@ElasticsearchField(name = "maidenName", store = ElasticsearchStore.Yes, index = Index.Not_Analyzed),
                                   @ElasticsearchField(name = "maidenNameTokenized", store = ElasticsearchStore.Yes, index = Index.Analyzed)})
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKeyColumn(name = "name")
    @JoinColumn(name="USER_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Map<String, UserAttributeEntity> userAttributes = new HashMap<String, UserAttributeEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<AddressEntity> addresses = new HashSet<AddressEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<PhoneEntity> phones = new HashSet<PhoneEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<EmailAddressEntity> emailAddresses = new HashSet<EmailAddressEntity>(0);

    @Column(name = "SYSTEM_FLAG",length = 1)
    private String systemFlag;

    //@IndexedEmbedded(prefix="principal.", depth=1)
    @OneToMany(orphanRemoval = true, cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID", referencedColumnName="USER_ID")
    @Fetch(FetchMode.SUBSELECT)
    private List<LoginEntity> principalList = new LinkedList<LoginEntity>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    protected Set<UserKey> userKeys = new HashSet<UserKey>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<UserToRoleMembershipXrefEntity> roles = new HashSet<UserToRoleMembershipXrefEntity>(0);
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
	private Set<UserToOrganizationMembershipXrefEntity> affiliations = new HashSet<UserToOrganizationMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<UserToResourceMembershipXrefEntity> resources = new HashSet<UserToResourceMembershipXrefEntity>(0);
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<UserToGroupMembershipXrefEntity> groups = new HashSet<UserToGroupMembershipXrefEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "employee", fetch = FetchType.LAZY)
    // @Fetch(FetchMode.SUBSELECT)
    private Set<SupervisorEntity> supervisors;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "supervisor", fetch = FetchType.LAZY)
    // @Fetch(FetchMode.SUBSELECT)
    private Set<SupervisorEntity> subordinates;

    @Transient
    private String defaultLogin;

    public UserEntity() {
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

    public MetadataTypeEntity getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(MetadataTypeEntity employeeType) {
        this.employeeType = employeeType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public MetadataTypeEntity getJobCode() {
        return jobCode;
    }

    public void setJobCode(MetadataTypeEntity jobCode) {
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

    public MetadataTypeEntity getType() {
        return type;
    }

    public void setType(MetadataTypeEntity type) {
        this.type = type;
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
        if(defaultEmail == null) {
        	if(this.emailAddresses!=null && !this.emailAddresses.isEmpty()){
        		final EmailAddressEntity entity = this.emailAddresses.iterator().next();
        		if(entity != null) {
        			defaultEmail = entity.getEmailAddress();
        		}
        	}
        }
        return defaultEmail;
    }

    public PhoneEntity getDefaultPhone() {
        PhoneEntity defaultPhone = null;
        if(this.phones!=null && !this.phones.isEmpty()){
            for (PhoneEntity phoneEntity: this.phones){
                if(phoneEntity.getIsDefault()){
                    defaultPhone = phoneEntity;
                    break;
                }
            }
        }
        return defaultPhone;
    }
    public AddressEntity getDefaultAddress() {
        AddressEntity defaultAddress = null;
        if(this.addresses!=null && !this.addresses.isEmpty()){
            for (AddressEntity addressEntity: this.addresses){
                if(addressEntity.getIsDefault()){
                    defaultAddress = addressEntity;
                    break;
                }
            }
        }
        return defaultAddress;
    }
    public String getDefaultLogin() {
        return defaultLogin;
    }
    public void setDefaultLogin(String managedSys) {
        if(this.principalList!=null && !this.principalList.isEmpty()){
            for (LoginEntity principal: this.principalList){
                if(StringUtils.equals(principal.getManagedSysId(), managedSys)){
                    defaultLogin = principal.getLogin();
                    break;
                }
            }
        }
    }

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

    public Set<UserToGroupMembershipXrefEntity> getGroups() {
        return groups;
    }

    public void setGroups(Set<UserToGroupMembershipXrefEntity> groups) {
        this.groups = groups;
    }
    
    public UserToGroupMembershipXrefEntity getGroup(final String groupId) {
		final Optional<UserToGroupMembershipXrefEntity> xref = 
    			this.getGroups()
    				.stream()
    				.filter(e -> groupId.equals(e.getEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
    
    public void removeGroup(final GroupEntity entity) {
    	if(entity != null) {
			if(this.groups != null) {
				this.groups.removeIf(e -> e.getEntity().getId().equals(entity.getId()));
			}
		}
    }
    
    public void addGroup(final GroupEntity entity, final Collection<AccessRightEntity> rights) {
		if(entity != null) {
			if(this.groups == null) {
				this.groups = new LinkedHashSet<UserToGroupMembershipXrefEntity>();
			}
			UserToGroupMembershipXrefEntity theXref = null;
			for(final UserToGroupMembershipXrefEntity xref : this.groups) {
				if(xref.getEntity().getId().equals(entity.getId()) && xref.getMemberEntity().getId().equals(getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new UserToGroupMembershipXrefEntity();
				theXref.setEntity(entity);
				theXref.setMemberEntity(this);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.groups.add(theXref);
		}
	}

    public Set<UserToRoleMembershipXrefEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserToRoleMembershipXrefEntity> roles) {
        this.roles = roles;
    }
    
    public UserToRoleMembershipXrefEntity getRole(final String roleId) {
		final Optional<UserToRoleMembershipXrefEntity> xref = 
    			this.getRoles()
    				.stream()
    				.filter(e -> roleId.equals(e.getEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
    
    public void removeRole(final RoleEntity entity) {
    	if(entity != null) {
			if(this.roles != null) {
				this.roles.removeIf(e -> e.getEntity().getId().equals(entity.getId()));
			}
		}
    }
    
    public void addRole(final RoleEntity entity, final Collection<AccessRightEntity> rights) {
		if(entity != null) {
			if(this.roles == null) {
				this.roles = new LinkedHashSet<UserToRoleMembershipXrefEntity>();
			}
			UserToRoleMembershipXrefEntity theXref = null;
			for(final UserToRoleMembershipXrefEntity xref : this.roles) {
				if(xref.getEntity().getId().equals(entity.getId()) && xref.getMemberEntity().getId().equals(getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new UserToRoleMembershipXrefEntity();
				theXref.setEntity(entity);
				theXref.setMemberEntity(this);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.roles.add(theXref);
		}
	}

    public Set<UserToOrganizationMembershipXrefEntity> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(Set<UserToOrganizationMembershipXrefEntity> affiliations) {
        this.affiliations = affiliations;
    }
    
    public UserToOrganizationMembershipXrefEntity getAffiliation(final String organizationId) {
		final Optional<UserToOrganizationMembershipXrefEntity> xref = 
    			this.getAffiliations()
    				.stream()
    				.filter(e -> organizationId.equals(e.getEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
    
    public void removeAffiliation(final OrganizationEntity entity) {
    	if(entity != null) {
			if(this.affiliations != null) {
				this.affiliations.removeIf(e -> e.getEntity().getId().equals(entity.getId()));
			}
		}
    }
    
    public void addAffiliation(final OrganizationEntity entity, final Collection<AccessRightEntity> rights) {
		if(entity != null) {
			if(this.affiliations == null) {
				this.affiliations = new LinkedHashSet<UserToOrganizationMembershipXrefEntity>();
			}
			UserToOrganizationMembershipXrefEntity theXref = null;
			for(final UserToOrganizationMembershipXrefEntity xref : this.affiliations) {
				if(xref.getEntity().getId().equals(entity.getId()) && xref.getMemberEntity().getId().equals(getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new UserToOrganizationMembershipXrefEntity();
				theXref.setEntity(entity);
				theXref.setMemberEntity(this);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.affiliations.add(theXref);
		}
	}

    public Set<UserToResourceMembershipXrefEntity> getResources() {
        return resources;
    }

    public void setResources(Set<UserToResourceMembershipXrefEntity> resources) {
        this.resources = resources;
    }
    
    public UserToResourceMembershipXrefEntity getResource(final String resourceId) {
		final Optional<UserToResourceMembershipXrefEntity> xref = 
    			this.getResources()
    				.stream()
    				.filter(e -> resourceId.equals(e.getEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
    
    public void removeResource(final ResourceEntity entity) {
    	if(entity != null) {
			if(this.resources != null) {
				this.resources.removeIf(e -> e.getEntity().getId().equals(entity.getId()));
			}
		}
    }
    
    public void addResource(final ResourceEntity entity, final Collection<AccessRightEntity> rights) {
		if(entity != null) {
			if(this.resources == null) {
				this.resources = new LinkedHashSet<UserToResourceMembershipXrefEntity>();
			}
			UserToResourceMembershipXrefEntity theXref = null;
			for(final UserToResourceMembershipXrefEntity xref : this.resources) {
				if(xref.getEntity().getId().equals(entity.getId()) && xref.getMemberEntity().getId().equals(getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new UserToResourceMembershipXrefEntity();
				theXref.setEntity(entity);
				theXref.setMemberEntity(this);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.resources.add(theXref);
		}
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
	       this.employeeType = newUser.getEmployeeType();
	    } else {
            employeeType=null;
        }

	    if (newUser.getFirstName() != null) {
	        if (newUser.getFirstName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
	            this.firstName = null;
	        } else {
	            this.firstName = newUser.getFirstName();
	        }
	    }
	    if (newUser.getJobCode() != null) {
	        this.jobCode = newUser.getJobCode();
	    } else {
            this.jobCode = null;
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
        if (newUser.getClaimDate() != null) {
            if (newUser.getClaimDate().equals(BaseConstants.NULL_DATE)) {
                this.claimDate = null;
            } else {
                this.claimDate = newUser.getClaimDate();
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
	            this.mailCode = null;
	        } else {
	            this.mailCode = newUser.getMailCode();
	        }
	    }
	    if (newUser.getType() != null) {
            this.setType(newUser.getType());
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

    public Set<SupervisorEntity> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(Set<SupervisorEntity> supervisorsSet) {
        this.supervisors = supervisorsSet;
    }

    public Set<SupervisorEntity> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(Set<SupervisorEntity> subordinatesSet) {
        this.subordinates = subordinatesSet;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (birthdate != null ? !birthdate.equals(that.birthdate) : that.birthdate != null) return false;
        if (companyOwnerId != null ? !companyOwnerId.equals(that.companyOwnerId) : that.companyOwnerId != null)
            return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (employeeId != null ? !employeeId.equals(that.employeeId) : that.employeeId != null) return false;
        if (nickname != null ? !nickname.equals(that.nickname) : that.nickname != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (birthdate != null ? birthdate.hashCode() : 0);
        result = 31 * result + (companyOwnerId != null ? companyOwnerId.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (employeeId != null ? employeeId.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        return result;
    }
}
