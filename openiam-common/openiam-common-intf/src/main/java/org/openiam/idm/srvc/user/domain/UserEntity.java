package org.openiam.idm.srvc.user.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.openiam.core.dao.lucene.LuceneId;
import org.openiam.core.dao.lucene.LuceneLastUpdate;
import org.openiam.core.dao.lucene.bridge.OrganizationBridge;
import org.openiam.core.domain.UserKey;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;
import org.openiam.idm.srvc.grp.dto.UserGroup;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.ResourceUser;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserAttributeMapAdapter;
import org.openiam.idm.srvc.user.dto.UserNote;
import org.openiam.idm.srvc.user.dto.UserNoteSetAdapter;
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

    @Column(name = "COMPANY_ID", length = 32)
    private String companyId;

    @Column(name = "COMPANY_OWNER_ID", length = 32)
    private String companyOwnerId;

    @Column(name = "CREATE_DATE", length = 19)
    private Date createDate;

    @Column(name = "CREATED_BY", length = 32)
    private String createdBy;

    @Column(name = "DEPT_CD", length = 50)
    private String deptCd;

    @Column(name = "DEPT_NAME", length = 100)
    private String deptName;

    @Column(name = "EMPLOYEE_ID", length = 32)
    private String employeeId;

    @Column(name = "EMPLOYEE_TYPE", length = 20)
    private String employeeType;

    @Column(name = "FIRST_NAME", length = 50)
    @Fields ({
        @Field(index = Index.TOKENIZED),
        @Field(name = "firstName", index = Index.TOKENIZED, store = Store.YES)
    })
    private String firstName;

    @Column(name = "JOB_CODE", length = 50)
    private String jobCode;

    @Column(name = "LAST_NAME", length = 50)
    @Fields ({
        @Field(index = Index.TOKENIZED),
        @Field(name = "lastName", index = Index.TOKENIZED, store = Store.YES)
    })
    private String lastName;

    @Column(name = "LAST_UPDATE", length = 19)
    @LuceneLastUpdate
    private Date lastUpdate;

    @Column(name = "LAST_UPDATED_BY", length = 32)
    private String lastUpdatedBy;

    @Column(name = "LOCATION_CD", length = 50)
    private String locationCd;

    @Column(name = "LOCATION_NAME", length = 100)
    private String locationName;

    @Column(name = "MANAGER_ID", length = 32)
    private String managerId;

    @Column(name = "TYPE_ID", length = 20)
    private String metadataTypeId;

    @Column(name = "CLASSIFICATION", length = 20)
    private String classification;

    @Column(name = "MIDDLE_INIT", length = 50)
    private String middleInit;

    @Column(name = "PREFIX", length = 4)
    private String prefix;

    @Column(name = "SEX", length = 1)
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
    private String suffix;

    @Column(name = "TITLE", length = 30)
    private String title;

    @Column(name = "USER_TYPE_IND", length = 20)
    private String userTypeInd;

    @Column(name = "DIVISION", length = 50)
    private String division;

    @Column(name = "MAIL_CODE", length = 10)
    private String mailCode;

    @Column(name = "COST_CENTER", length = 20)
    private String costCenter;

    @Column(name = "START_DATE", length = 10)
    private Date startDate;

    @Column(name = "LAST_DATE", length = 10)
    private Date lastDate;

    @Column(name = "NICKNAME", length = 40)
    private String nickname;

    @Column(name = "MAIDEN_NAME", length = 40)
    private String maidenName;

    @Column(name = "PASSWORD_THEME", length = 20)
    private String passwordTheme;

    @Column(name = "SHOW_IN_SEARCH")
    private Integer showInSearch = new Integer(0);

    @Column(name = "DEL_ADMIN")
    private Integer delAdmin = new Integer(0);

    @Column(name = "ALTERNATE_ID", length = 32)
    private String alternateContactId;

    @Column(name = "USER_OWNER_ID")
    private String userOwnerId;

    @Column(name = "DATE_PASSWORD_CHANGED", length = 10)
    private Date datePasswordChanged;

    @Column(name = "DATE_CHALLENGE_RESP_CHANGED", length = 10)
    private Date dateChallengeRespChanged;

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

    @ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="COMPANY_ID", referencedColumnName="COMPANY_ID", insertable = false, updatable = false)
    @Field(name="organization", bridge=@FieldBridge(impl=OrganizationBridge.class), store=Store.YES)
    private OrganizationEntity organization;


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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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

    public String getDeptCd() {
        return deptCd;
    }

    public void setDeptCd(String deptCd) {
        this.deptCd = deptCd;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
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

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
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

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
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

    public Integer getDelAdmin() {
        return delAdmin;
    }

    public void setDelAdmin(Integer delAdmin) {
        this.delAdmin = delAdmin;
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
				+ ((companyId == null) ? 0 : companyId.hashCode());
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
				+ ((datePasswordChanged == null) ? 0 : datePasswordChanged
						.hashCode());
		result = prime * result
				+ ((delAdmin == null) ? 0 : delAdmin.hashCode());
		result = prime * result + ((deptCd == null) ? 0 : deptCd.hashCode());
		result = prime * result
				+ ((deptName == null) ? 0 : deptName.hashCode());
		result = prime * result
				+ ((division == null) ? 0 : division.hashCode());
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
				+ ((managerId == null) ? 0 : managerId.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime * result
				+ ((middleInit == null) ? 0 : middleInit.hashCode());
		result = prime * result
				+ ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result
				+ ((organization == null) ? 0 : organization.hashCode());
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
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
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
		if (datePasswordChanged == null) {
			if (other.datePasswordChanged != null)
				return false;
		} else if (!datePasswordChanged.equals(other.datePasswordChanged))
			return false;
		if (delAdmin == null) {
			if (other.delAdmin != null)
				return false;
		} else if (!delAdmin.equals(other.delAdmin))
			return false;
		if (deptCd == null) {
			if (other.deptCd != null)
				return false;
		} else if (!deptCd.equals(other.deptCd))
			return false;
		if (deptName == null) {
			if (other.deptName != null)
				return false;
		} else if (!deptName.equals(other.deptName))
			return false;
		if (division == null) {
			if (other.division != null)
				return false;
		} else if (!division.equals(other.division))
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
		if (managerId == null) {
			if (other.managerId != null)
				return false;
		} else if (!managerId.equals(other.managerId))
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
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
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
