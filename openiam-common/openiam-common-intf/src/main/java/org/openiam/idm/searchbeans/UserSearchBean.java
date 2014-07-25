package org.openiam.idm.searchbeans;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchMode;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.srvc.user.dto.SearchAttribute;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.*;

/**
 * Created by: Alexander Duckardt
 * Date: 19.11.12
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserSearchBean", propOrder = {
        "firstName",
        "lastName",
        "accountStatus",
        "maidenName",
        "jobCode",
        "userStatus",
        "nickName",
        "phoneAreaCd",
        "phoneNbr",
        "employeeId",
        "employeeType",
        "groupIdSet",
        "roleIdSet",
        "resourceIdSet",
        "emailAddress",
        "principal",
        "attributeName",
        "attributeValue",
        "attributeElementId",
        "userId",
        "showInSearch",
        "locationCd",
        "classification",
        "userTypeInd",
        "loggedIn",
        "createDate",
        "maxResultSize",
        "startDate",
        "lastDate",
        "dateOfBirth",
        "zipCode",
        "delAdmin",
        "organizationIdList",
        "attributeList",
        "requesterId",
        "updatedSince",
        "firstNameMatchToken",
        "lastNameMatchToken",
        "maidenNameMatchToken",
        "employeeIdMatchToken",
        "emailAddressMatchToken",
        "searchMode",
        "initDefaulLoginFlag",
        "userType"
})
public class UserSearchBean extends AbstractSearchBean<User, String> implements SearchBean<User, String>,
        Serializable {

	/**
	 * Job code of the user
	 */
	protected String jobCode = null;

    /**
     * Type of the user
     */
    protected String userType = null;
	
	@Deprecated
    protected String firstName = null;
	
	@Deprecated
    protected String lastName = null;
	
	/**
	 * Nickname of the user
	 */
    protected String nickName = null;
    
    @Deprecated
    protected String maidenName = null;
    
    /**
     * Account status of the user
     */
    protected String accountStatus = null;
    
    /**
     * Secondary Status of the user
     */
    protected String userStatus = null;
    
    /**
     * Area code of the phone number
     */
    protected String phoneAreaCd = null;
    
    /**
     * Phone number
     */
    protected String phoneNbr = null;
    
    /**
     * Employee Type
     */
    protected String employeeType = null;
    
    @Deprecated
    protected String employeeId = null;
    
    /**
     * Set of Group IDs that this user belongs to.  
     * The user must belong to at leat one of these groups
     */
    protected Set<String> groupIdSet = null;
    

    /**
     * Set of Role IDs that this user belongs to.  
     * The user must belong to at leat one of these roles
     */
    protected Set<String> roleIdSet = null;
    

    /**
     * Set of Resource IDs that this user is entitled to.  
     * The user must be entitled to at least one of these
     */
    protected Set<String> resourceIdSet = null;
    
    @Deprecated
    protected String emailAddress = null;
    
    /**
     * Email Address token to search by
     */
    private SearchParam emailAddressMatchToken = null;
    
    /**
     * Login Search Bean to search by
     */
    protected LoginSearchBean principal;
    
    @Deprecated
    protected String attributeName;
    
    @Deprecated
    protected String attributeValue;
    
    /**
     * If set, the user's attribute will be searched.  A match indicates that the user has an attribute with metadata element ID
     */
    protected String attributeElementId;
    
    /**
     * The unique identifier of the user
     */
    protected String userId;
    
    /**
     * The location code of the user
     */
    protected String locationCd;
    protected Integer showInSearch;
    protected Integer maxResultSize;

    /**
     * The user type identifier ID
     */
    protected String userTypeInd;
    
    /**
     * The classification of this user
     */
    protected String classification;
    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    @XmlSchemaType(name = "dateTime")
    protected Date startDate;
    @XmlSchemaType(name = "dateTime")
    protected Date lastDate;
    @XmlSchemaType(name = "dateTime")
    protected Date updatedSince;

    @XmlSchemaType(name = "dateTime")
    protected Date dateOfBirth;
    
    /**
     * The zipcode of this user
     */
    protected String zipCode;

    protected String loggedIn = null;
    protected boolean delAdmin = false;

    /**
     * Set of Organization IDs that this user belongs to.  
     * The user must belong to at leat one of these organizations
     */
    protected Set<String> organizationIdList = new HashSet<String>();
    
    /**
     * The attributes which belong to this user
     */
    protected List<SearchAttribute> attributeList = new ArrayList<SearchAttribute>();

    private String requesterId;
    
    /**
     * First name token to search by
     */
    private SearchParam firstNameMatchToken = null;
    
    /**
     * Last Name token to search by
     */
    private SearchParam lastNameMatchToken = null;
    
    /**
     * Maiden Name token to search by
     */
    private SearchParam maidenNameMatchToken = null;
    
    /**
     * Employee ID token to search by
     */
    private SearchParam employeeIdMatchToken = null;
    
    /**
     * The search mode which will determine the result set.
     */
    private SearchMode searchMode  = SearchMode.AND;

    private boolean initDefaulLoginFlag=false;

    public SearchParam getFirstNameMatchToken() {
		return firstNameMatchToken;
	}

	public void setFirstNameMatchToken(SearchParam firstNameMatchToken) {
		this.firstNameMatchToken = firstNameMatchToken;
	}

	public SearchParam getLastNameMatchToken() {
		return lastNameMatchToken;
	}

	public void setLastNameMatchToken(SearchParam lastNameMatchToken) {
		this.lastNameMatchToken = lastNameMatchToken;
	}

	public SearchParam getMaidenNameMatchToken() {
		return maidenNameMatchToken;
	}

	public void setMaidenNameMatchToken(SearchParam maidenNameMatchToken) {
		this.maidenNameMatchToken = maidenNameMatchToken;
	}

	public SearchParam getEmployeeIdMatchToken() {
		return employeeIdMatchToken;
	}

	public void setEmployeeIdMatchToken(SearchParam employeeIdMatchToken) {
		this.employeeIdMatchToken = employeeIdMatchToken;
	}

	public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public Set<String> getOrganizationIdList() {
        return organizationIdList;
    }

    public void setOrganizationIdList(Set<String> organizationIdList) {
        this.organizationIdList = organizationIdList;
    }

    public void addOrganizationIdList(final Collection<String> organizationIdList) {
        if(organizationIdList != null) {
            if(this.organizationIdList==null) {
                this.organizationIdList = new HashSet<String>();
            }
            this.organizationIdList.addAll(organizationIdList);
        }
    }

    public void addOrganizationId(String organizationId){
        if(organizationIdList==null) {
            organizationIdList = new HashSet<String>();
        }
        organizationIdList.add(organizationId);
    }

    public boolean isDelAdmin() {
        return delAdmin;
    }

    public void setDelAdmin(boolean delAdmin) {
        this.delAdmin = delAdmin;
    }

    @Deprecated
    public String getFirstName() {
        return (firstNameMatchToken != null) ? firstNameMatchToken.getValue() : null;
    }

    @Deprecated
    public void setFirstName(String firstName) {
        firstNameMatchToken = new SearchParam(firstName, MatchType.STARTS_WITH);
    }

    @Deprecated
    public String getLastName() {
        return (lastNameMatchToken != null) ? lastNameMatchToken.getValue() : null;
    }

    @Deprecated
    public void setLastName(String lastName) {
        lastNameMatchToken = new SearchParam(lastName, MatchType.STARTS_WITH);
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getPhoneAreaCd() {
        return phoneAreaCd;
    }

    public void setPhoneAreaCd(String phoneAreaCd) {
        this.phoneAreaCd = phoneAreaCd;
    }

    public String getPhoneNbr() {
        return phoneNbr;
    }

    public void setPhoneNbr(String phoneNbr) {
        this.phoneNbr = phoneNbr;
    }

    @Deprecated
    public String getEmployeeId() {
        return (employeeIdMatchToken != null) ? employeeIdMatchToken.getValue() : null;
    }

    @Deprecated
    public void setEmployeeId(String employeeId) {
        employeeIdMatchToken = new SearchParam(employeeId, MatchType.EXACT);
    }

    public Set<String> getGroupIdSet() {
        return groupIdSet;
    }

    public void setGroupIdSet(Set<String> groupIdSet) {
        this.groupIdSet=groupIdSet;
    }

    public void addGroupId(final String groupId) {
        if(StringUtils.isNotBlank(groupId)) {
            if(this.groupIdSet == null) {
                this.groupIdSet = new HashSet<String>();
            }
            this.groupIdSet.add(StringUtils.trimToNull(groupId));
        }

    }

    public Set<String> getRoleIdSet() {
        return roleIdSet;
    }

    public void setRoleIdSet(Set<String> roleIdSet) {
        this.roleIdSet=roleIdSet;
    }


    public void addRoleId(final String roleId) {
        if(StringUtils.isNotBlank(roleId)) {
            if(this.roleIdSet == null) {
                this.roleIdSet = new HashSet<String>();
            }
            this.roleIdSet.add(StringUtils.trimToNull(roleId));
        }
    }

    @Deprecated
    public String getEmailAddress() {
        return (emailAddressMatchToken != null) ? emailAddressMatchToken.getValue() : null;
    }

    @Deprecated
    public void setEmailAddress(String emailAddress) {
        emailAddressMatchToken = new SearchParam(emailAddress, MatchType.STARTS_WITH);
    }

    public SearchParam getEmailAddressMatchToken() {
		return emailAddressMatchToken;
	}

	public void setEmailAddressMatchToken(SearchParam emailAddressMatchToken) {
		this.emailAddressMatchToken = emailAddressMatchToken;
	}

	public LoginSearchBean getPrincipal() {
        return principal;
    }

    public void setPrincipal(LoginSearchBean principal) {
        this.principal = principal;
    }

    @Deprecated
    public String getAttributeName() {
        return attributeName;
    }

    @Deprecated
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Deprecated
    public String getAttributeValue() {
        return attributeValue;
    }

    @Deprecated
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getAttributeElementId() {
        return attributeElementId;
    }

    public void setAttributeElementId(String attributeElementId) {
        this.attributeElementId = attributeElementId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocationCd() {
        return locationCd;
    }

    public void setLocationCd(String locationCd) {
        this.locationCd = locationCd;
    }

    public Integer getShowInSearch() {
        return showInSearch;
    }

    public void setShowInSearch(Integer showInSearch) {
        this.showInSearch = showInSearch;
    }

    public Integer getMaxResultSize() {
        return maxResultSize;
    }

    public void setMaxResultSize(Integer maxResultSize) {
        this.maxResultSize = maxResultSize;
    }

    public String getUserTypeInd() {
        return userTypeInd;
    }

    public void setUserTypeInd(String userTypeInd) {
        this.userTypeInd = userTypeInd;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    public Date getUpdatedSince() {
        return updatedSince;
    }

    public void setUpdatedSince(Date updatedSince) {
        this.updatedSince = updatedSince;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(String loggedIn) {
        this.loggedIn = loggedIn;
    }

    public List<SearchAttribute> getAttributeList() {
        return attributeList;
    }
    
    public void addAttribute(final String name, final String value) {
    	if(this.attributeList == null) {
    		this.attributeList = new LinkedList<>();
    	}
    	this.attributeList.add(new SearchAttribute(name, value));
    }

    public void setAttributeList(List<SearchAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public Set<String> getResourceIdSet() {
        return resourceIdSet;
    }

    public void setResourceIdSet(Set<String> resourceIdSet) {
        this.resourceIdSet = resourceIdSet;
    }

    public void addResourceId(final String resourceId) {
        if(resourceId != null) {
            if(this.resourceIdSet == null) {
                this.resourceIdSet = new HashSet<String>();
            }
            this.resourceIdSet.add(resourceId);
        }
    }

    @Deprecated
	public String getMaidenName() {
		//return maidenName;
    	return (maidenNameMatchToken != null) ? maidenNameMatchToken.getValue() : null;
	}

    @Deprecated
	public void setMaidenName(String maidenName) {
		//this.maidenName = maidenName;
    	maidenNameMatchToken = new SearchParam(maidenName, MatchType.STARTS_WITH);
	}

    public boolean getInitDefaulLoginFlag() {
        return initDefaulLoginFlag;
    }

    public void setInitDefaulLogin(boolean initDefaulLoginFlag) {
        this.initDefaulLoginFlag = initDefaulLoginFlag;
    }

    public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public SearchMode getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
	}

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
