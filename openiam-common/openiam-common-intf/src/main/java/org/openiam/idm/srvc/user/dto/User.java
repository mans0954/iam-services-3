package org.openiam.idm.srvc.user.dto;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseConstants;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

/**
 * User domain object.  This object is used to transfer data between the service layer
 * and the client layer.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "user", propOrder = {
        "operation",
        "addresses",
        "birthdate",
        "companyOwnerId",
        "createDate",
        "createdBy",
        "emailAddresses",
        "employeeId",
        "employeeType",
        //"expirationDate",
        "firstName",
        "jobCode",
        "lastName",
        "lastUpdate",
        "lastUpdatedBy",
        "locationCd",
        "locationName",
        "metadataTypeId",
        "classification",
        "middleInit",
        "phones",
        "prefix",
        "sex",
        "status",
        "secondaryStatus",
        "suffix",
        "title",
        "userAttributes",
        "userId",
        "userTypeInd",
        "userNotes",
        "costCenter",
        "startDate",
        "lastDate",
        "mailCode",
        "nickname",
        "maidenName",
        "passwordTheme",
        "email",
        "showInSearch",
        "principalList",
        "alternateContactId",
        "securityDomain",
        "userOwnerId",
        "datePasswordChanged",
        "dateChallengeRespChanged",
        "dateITPolicyApproved",
        "login",
        "password",
        "notifyUserViaEmail",
        "roles",
        "resources",
        "groups",
        "affiliations",
        "supervisors",
        "subordinates"
})
@XmlSeeAlso({
        Login.class,
        UserNote.class,
        Phone.class,
        Address.class,
        EmailAddress.class,
        UserAttribute.class,
        Role.class,
        Resource.class,
        Group.class,
        Organization.class
})
@DozerDTOCorrespondence(UserEntity.class)
public class User extends org.openiam.base.BaseObject {

    private AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    protected static final Log log = LogFactory.getLog(User.class);
    // Fields
    protected String userId;

    //protected AddressMap addresses; see below
    @XmlSchemaType(name = "dateTime")
    protected Date birthdate;

    protected String companyOwnerId;

    @XmlSchemaType(name = "dateTime")
    protected Date createDate;

    protected String createdBy;

    protected String employeeId;

    protected String employeeType;

    protected String firstName;

    protected String jobCode;

    protected String lastName;

    @XmlSchemaType(name = "dateTime")
    protected Date lastUpdate;

    protected String lastUpdatedBy;

    protected String locationCd;

    protected String locationName;

    protected String metadataTypeId;

    protected String classification;

    protected String middleInit;

    protected String prefix;

    protected String sex;

    @Enumerated(EnumType.STRING)
    protected UserStatusEnum status;

    @Enumerated(EnumType.STRING)
    protected UserStatusEnum secondaryStatus;

    protected String suffix;

    protected String title;

    protected String userTypeInd;

    protected String mailCode;

    protected String costCenter;

    @XmlSchemaType(name = "dateTime")
    protected Date startDate;

    @XmlSchemaType(name = "dateTime")
    protected Date lastDate;

    protected String nickname;

    protected String maidenName;

    protected String passwordTheme;

    protected String email;

    protected Integer showInSearch = new Integer(0);

    protected List<Login> principalList = new LinkedList<Login>();

    protected String alternateContactId;

    protected String securityDomain;

    protected String userOwnerId;

    @XmlSchemaType(name = "dateTime")
    protected Date datePasswordChanged;

    @XmlSchemaType(name = "dateTime")
    protected Date dateChallengeRespChanged;

    @XmlSchemaType(name = "dateTime")
    protected Date dateITPolicyApproved;

    @XmlJavaTypeAdapter(UserNoteSetAdapter.class)
    protected Set<UserNote> userNotes = new HashSet<UserNote>(0);


    @XmlJavaTypeAdapter(UserAttributeMapAdapter.class)
    protected HashMap<String, UserAttribute> userAttributes = new HashMap<String, UserAttribute>(0);

    protected Set<Address> addresses = new HashSet<Address>(0);

    protected Set<Phone> phones = new HashSet<Phone>(0);

    protected Set<EmailAddress> emailAddresses = new HashSet<EmailAddress>(0);

    protected Set<Role> roles = new HashSet<Role>(0);

    protected Set<Organization> affiliations = new HashSet<Organization>(0);

    protected Set<Group> groups = new HashSet<Group>(0);

    protected Set<Resource> resources = new HashSet<Resource>(0);

    // these fields are used only when userWS is used directly without provision
    private String login;
    private String password;
    private Boolean notifyUserViaEmail=true;

    private Set<Supervisor> supervisors;

    private Set<Supervisor> subordinates;

    // Constructors

    /**
     * default constructor
     */
    public User() {
    }

    /**
     * minimal constructor
     */
    public User(String userId) {
        this.userId = userId;
    }

    // Property accessors
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
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

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleInit() {
        return this.middleInit;
    }

    public void setMiddleInit(String middleInit) {
        this.middleInit = middleInit;
    }

    public Set<Organization> getAffiliations() {
        return affiliations;
    }
    
    public void addAffiliation(final Organization org) {
    	if(org != null) {
    		if(affiliations == null) {
    			affiliations = new HashSet<Organization>();
    		}
    		org.setOperation(AttributeOperationEnum.ADD);
    		affiliations.add(org);
    	}
    }
    
    public void markAffiliateAsDeleted(final String id) {
    	if(id != null) {
    		if(affiliations != null) {
    			for(final Organization organization : affiliations) {
    				if(StringUtils.equals(organization.getId(), id)) {
    					organization.setOperation(AttributeOperationEnum.DELETE);
    					break;
    				}
    			}
    		}
    	}
    }

    public void setAffiliations(Set<Organization> affiliations) {
        this.affiliations = affiliations;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getUserTypeInd() {
        return this.userTypeInd;
    }

    public void setUserTypeInd(String userTypeInd) {
        this.userTypeInd = userTypeInd;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeType() {
        return this.employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getLocationCd() {
        return this.locationCd;
    }

    public void setLocationCd(String locationId) {
        this.locationCd = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCompanyOwnerId() {
        return this.companyOwnerId;
    }

    public void setCompanyOwnerId(String companyOwnerId) {
        this.companyOwnerId = companyOwnerId;
    }

    public String getJobCode() {
        return this.jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getCostCenter() {
        return this.costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getLastDate() {
        return this.lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public Set<UserNote> getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(Set<UserNote> userNotes) {
        this.userNotes = userNotes;
    }

    /**
     * Updates the underlying collection with the UserNote object that is being passed in.
     * The note is added if its does note exist and updated if its does exist.
     *
     * @param note
     */
    public void saveNote(UserNote note) {
        userNotes.add(note);
    }

    /**
     * Removes the note object from the underlying collection.
     *
     * @param note
     */
    public void removeNote(UserNote note) {
        userNotes.remove(note);
    }

    /**
     * Returns the note object for the specified noteId.
     *
     * @param noteId
     * @return
     */
    public UserNote getUserNote(String noteId) {
        UserNote nt = null;

        Iterator<UserNote> it = this.userNotes.iterator();
        while (it.hasNext()) {
            nt = it.next();
            if (nt.getUserNoteId().equals(noteId))
                return nt;
        }

        return nt;
    }


    public HashMap<String, UserAttribute> getUserAttributes() {
        return this.userAttributes;
    }

    public void setUserAttributes(HashMap<String, UserAttribute> userAttributes) {
        this.userAttributes = userAttributes;
    }


    /**
     * Updates the underlying collection with the UserAttribute object that is being passed in.
     * The attribute is added if its does not exist and updated if its does exist.
     *
     * @param attr
     */
    public void saveAttribute(UserAttribute attr) {
        userAttributes.put(attr.getName(), attr);
    }

    /**
     * Removes the attribute object from the underlying collection.
     *
     * @param attr
     */
    public void removeAttributes(UserAttribute attr) {
        userAttributes.remove(attr.getName());
    }

    /**
     * Returns the attribute object that is specified by the NAME parameter.
     *
     * @param name - The attribute map is keyed on the NAME property.
     * @return
     */
    public UserAttribute getAttribute(String name) {

        return userAttributes.get(name);

    }

    /**
     * Returns a Set of addresses. Map is keyed on the Address.description value. This
     * value should indicate the type of address; HOME, SHIPPING, BILLING, etc.
     *
     * @return
     */
    public Set<Address> getAddresses() {
        return addresses;
    }

    /**
     * Sets a Set of addresses with a user. Map is keyed on the Address.description value.
     *
     * @param addresses
     */
    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public Address getAddressByName(String name) {
        Iterator<Address> addressIt = addresses.iterator();
        while (addressIt.hasNext()) {
            Address adr = addressIt.next();
            if (adr.getName() != null && adr.getName().equalsIgnoreCase(name)) {
                return adr;
            }
        }
        return null;
    }

    public Set<EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(Set<EmailAddress> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public EmailAddress getEmailByName(String name) {
        Iterator<EmailAddress> emailIt = emailAddresses.iterator();
        while (emailIt.hasNext()) {
            EmailAddress em = emailIt.next();
            if (em.getName() != null && em.getName().equalsIgnoreCase(name)) {
                return em;
            }
        }
        return null;
    }

    public Set<Phone> getPhones() {
        return phones;
    }

    public void setPhones(Set<Phone> phones) {
        this.phones = phones;
    }

    public Phone getPhoneByName(String name) {
        Iterator<Phone> phoneIt = phones.iterator();
        while (phoneIt.hasNext()) {
            Phone ph = phoneIt.next();
            if (ph.getName() != null && ph.getName().equalsIgnoreCase(name)) {
                return ph;
            }
        }
        return null;
    }

    public Phone getPhoneById(String id) {
        Iterator<Phone> phoneIt = phones.iterator();
        while (phoneIt.hasNext()) {
            Phone ph = phoneIt.next();
            if (ph.getName() != null && ph.getName().equalsIgnoreCase(id)) {
                return ph;
            }
        }
        return null;
    }

    public Address getAddressById(String id) {
        Iterator<Address> addressIt = addresses.iterator();
        while (addressIt.hasNext()) {
            Address adr = addressIt.next();
            if (adr.getName() != null && adr.getName().equalsIgnoreCase(id)) {
                return adr;
            }
        }
        return null;
    }

    public EmailAddress getEmailAddressById(String id) {
        Iterator<EmailAddress> emailIt = emailAddresses.iterator();
        while (emailIt.hasNext()) {
            EmailAddress em = emailIt.next();
            if (em.getName() != null && em.getName().equalsIgnoreCase(id)) {
                return em;
            }
        }
        return null;
    }

    public String getMailCode() {
        return mailCode;
    }

    public void setMailCode(String mailCode) {
        this.mailCode = mailCode;
    }

    public Set<Role> getRoles() {
        return roles;
    }
    
    public void markRoleAsDeleted(final String id) {
    	if(id != null) {
    		if(roles != null) {
    			for(final Role role : roles) {
    				if(StringUtils.equals(role.getId(), id)) {
    					role.setOperation(AttributeOperationEnum.DELETE);
    					break;
    				}
    			}
    		}
    	}
    }
    
    public void addRole(final Role role) {
    	if(role != null) {
    		if(roles == null) {
    			roles = new HashSet<Role>();
    		}
    		role.setOperation(AttributeOperationEnum.ADD);
    		roles.add(role);
    	}
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Group> getGroups() {
        return groups;
    }
    
    public void addGroup(final Group group) {
    	if(group != null) {
    		if(groups == null) {
    			groups = new HashSet<Group>();
    		}
    		group.setOperation(AttributeOperationEnum.ADD);
    		groups.add(group);
    	}
    }
    
    public void markGroupAsDeleted(final String groupId) {
    	if(groupId != null) {
    		if(groups != null) {
    			for(final Group group : groups) {
    				if(StringUtils.equals(group.getId(), groupId)) {
    					group.setOperation(AttributeOperationEnum.DELETE);
    					break;
    				}
    			}
    		}
    	}
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<Resource> getResources() {
        return resources;
    }
    
    public void markResourceAsDeleted(final String resourceId) {
    	if(resourceId != null) {
    		if(resources != null) {
    			for(final Resource resource : resources) {
    				if(StringUtils.equals(resource.getResourceId(), resourceId)) {
    					resource.setOperation(AttributeOperationEnum.DELETE);
    					break;
    				}
    			}
    		}
    	}
    }
    
    public void addResource(final Resource resource) {
    	if(resource != null) {
    		if(resources == null) {
    			resources = new HashSet<Resource>();
    		}
    		resource.setOperation(AttributeOperationEnum.ADD);
    		resources.add(resource);
    	}
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Deprecated
    public Phone getDefaultPhone() {
        if(this.phones!=null && !this.phones.isEmpty()){
            for (Phone p: this.phones){
                if(p.getIsDefault()){
                    return p;
                }
            }
        }
        return null;
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

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }


    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public String getPasswordTheme() {
        return passwordTheme;
    }

    public void setPasswordTheme(String passwordTheme) {
        this.passwordTheme = passwordTheme;
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

    public Integer getShowInSearch() {
        return showInSearch;
    }

    public void setShowInSearch(Integer showInSearch) {
        this.showInSearch = showInSearch;
    }

    public List<Login> getPrincipalList() {
        return principalList;
    }
    
    public boolean containsLogin(final String loginId) {
    	boolean retVal = false;
        if(principalList != null) {
            for(final Login login : principalList) {
                if(StringUtils.equals(loginId, login.getLoginId())) {
                    retVal = true;
                }
            }
        }
    	return retVal;
    }
    
    public void addPrincipal(final Login login) {
    	if(login != null) {
    		if(this.principalList == null) {
    			this.principalList = new LinkedList<Login>();
    		}
    		login.setOperation(AttributeOperationEnum.ADD);
    		this.principalList.add(login);
    	}
    }

    public void setPrincipalList(List<Login> principalList) {
        this.principalList = principalList;
    }

    public String getAlternateContactId() {
        return alternateContactId;
    }

    public void setAlternateContactId(String alternateContactId) {
        this.alternateContactId = alternateContactId;
    }

    public String getSecurityDomain() {
        return securityDomain;
    }

    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
    }

	public void updateUser(User newUser) {
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
        if (newUser.getEmail() != null) {
            if (newUser.getEmail().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                this.email = null;
            } else {
                this.email = newUser.getEmail();
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
        if (newUser.dateITPolicyApproved != null) {
            if (newUser.dateITPolicyApproved.equals(BaseConstants.NULL_DATE)) {
                this.dateITPolicyApproved = null;
            } else {
                this.dateITPolicyApproved = newUser.getDateITPolicyApproved();
            }
        }

        if (newUser.getDatePasswordChanged() != null) {
            if (newUser.getDatePasswordChanged().equals(BaseConstants.NULL_DATE)) {
                this.datePasswordChanged = null;
            } else {
                this.datePasswordChanged = newUser.getDatePasswordChanged();
            }
        }

        // check the attributes
        if (newUser.getUserAttributes() != null) {
            log.debug("UserAttributes are NOT NULL in newUser object");
            updateAttributes(newUser.getUserAttributes());
        } else {
            log.debug("UserAttributes are NULL in newUser");
        }
    }

    protected void updateAttributes(Map<String, UserAttribute> attrMap) {
        if (attrMap == null || attrMap.isEmpty()) {
            return;
        }

        Set<String> keySet = attrMap.keySet();

        for (String s : keySet) {
            UserAttribute origAttr = userAttributes.get(s);
            UserAttribute newAttr = attrMap.get(s);
            if (newAttr.getOperation() == AttributeOperationEnum.NO_CHANGE) {
                log.debug("- updateAttributes: key=" + " " + s + " = NO_CHANGE");

            } else if (newAttr.getOperation() == AttributeOperationEnum.ADD) {
                log.debug("- updateAttributes: key=" + " " + s + " = ADD");
                userAttributes.put(newAttr.getName(), newAttr);

            } else if (newAttr.getOperation() == AttributeOperationEnum.DELETE) {
                log.debug("- updateAttributes: key=" + " " + s + " = DELETE");
                userAttributes.remove(origAttr.getName());

            } else if (newAttr.getOperation() == AttributeOperationEnum.REPLACE) {
                log.debug("- updateAttributes: key=" + " " + s + " = REPLACE");
                origAttr.setOperation(AttributeOperationEnum.REPLACE);
                origAttr.setValue(newAttr.getValue());
                userAttributes.put(origAttr.getName(), origAttr);

            } else {
                // Operation Attribute was not set
                if (origAttr == null && newAttr != null) {
                    // new attribute
                    log.debug("- updateAttributes: key=" + " " + s + " = DETERMINED ADD");
                    newAttr.setOperation(AttributeOperationEnum.ADD);
                    userAttributes.put(newAttr.getName(), newAttr);
                } else {
                    log.debug("- updateAttributes: key=" + " " + s + " = DETERMINED REPLACE");
                    origAttr.setOperation(AttributeOperationEnum.REPLACE);
                    origAttr.setValue(newAttr.getValue());
                    userAttributes.put(origAttr.getName(), origAttr);
                }
            }

        }
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getNotifyUserViaEmail() {
        return notifyUserViaEmail;
    }

    public void setNotifyUserViaEmail(Boolean notifyUserViaEmail) {
        this.notifyUserViaEmail = notifyUserViaEmail;
    }

    public Set<Supervisor> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(Set<Supervisor> supervisorsSet) {
        this.supervisors = supervisorsSet;
    }

    public Set<Supervisor> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(Set<Supervisor> subordinatesSet) {
        this.subordinates = subordinatesSet;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (companyOwnerId != null ? !companyOwnerId.equals(user.companyOwnerId) : user.companyOwnerId != null)
            return false;
        if (costCenter != null ? !costCenter.equals(user.costCenter) : user.costCenter != null) return false;
        if (createDate != null ? !createDate.equals(user.createDate) : user.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(user.createdBy) : user.createdBy != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (employeeId != null ? !employeeId.equals(user.employeeId) : user.employeeId != null) return false;
        if (employeeType != null ? !employeeType.equals(user.employeeType) : user.employeeType != null) return false;
        if (lastDate != null ? !lastDate.equals(user.lastDate) : user.lastDate != null) return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        if (maidenName != null ? !maidenName.equals(user.maidenName) : user.maidenName != null) return false;
        if (nickname != null ? !nickname.equals(user.nickname) : user.nickname != null) return false;
        if (securityDomain != null ? !securityDomain.equals(user.securityDomain) : user.securityDomain != null)
            return false;
        if (startDate != null ? !startDate.equals(user.startDate) : user.startDate != null) return false;
        if (title != null ? !title.equals(user.title) : user.title != null) return false;
        if (userId != null ? !userId.equals(user.userId) : user.userId != null) return false;
        if (userOwnerId != null ? !userOwnerId.equals(user.userOwnerId) : user.userOwnerId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (companyOwnerId != null ? companyOwnerId.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (employeeId != null ? employeeId.hashCode() : 0);
        result = 31 * result + (employeeType != null ? employeeType.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (costCenter != null ? costCenter.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (lastDate != null ? lastDate.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (maidenName != null ? maidenName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (securityDomain != null ? securityDomain.hashCode() : 0);
        result = 31 * result + (userOwnerId != null ? userOwnerId.hashCode() : 0);
        result = 31 * result + (login != null ? login.hashCode() : 0);
        return result;
    }
}
