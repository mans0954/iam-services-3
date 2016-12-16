package org.openiam.idm.searchbeans;

import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchMode;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.srvc.user.dto.SearchAttribute;
import org.openiam.idm.srvc.user.dto.User;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.bind.annotation.*;

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
        "claimDate",
        "dateOfBirth",
        "zipCode",
        "delAdmin",
        "attributeList",
        "updatedSince",
        "firstNameMatchToken",
        "nickNameMatchToken",
        "lastNameMatchToken",
        "maidenNameMatchToken",
        "employeeIdMatchToken",
        "emailAddressMatchToken",
        "searchMode",
        "initDefaulLoginFlag",
        "userType"
})
public class UserSearchBean extends EntitlementsSearchBean<User, String> {

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
    @Deprecated
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
    protected Date claimDate;
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
     * The attributes which belong to this user
     */
    protected List<SearchAttribute> attributeList = new ArrayList<SearchAttribute>();

    /**
     * First name token to search by
     */
    private SearchParam firstNameMatchToken = null;

    /**
     * Nick name token to search by
     */
    private SearchParam nickNameMatchToken = null;
    
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

    public SearchParam getNickNameMatchToken() {
        return nickNameMatchToken;
    }

    public void setNickNameMatchToken(SearchParam nickNameMatchToken) { this.nickNameMatchToken = nickNameMatchToken; }

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
    @Deprecated
    public String getAttributeElementId() {
        return attributeElementId;
    }
    @Deprecated
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

    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
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
    public void addAttribute(final String name, final String value, final String elementId) {
        if(this.attributeList == null) {
            this.attributeList = new LinkedList<>();
        }
        this.attributeList.add(new SearchAttribute(name, value, elementId));
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((accountStatus == null) ? 0 : accountStatus.hashCode());
		result = prime * result
				+ ((attributeList == null) ? 0 : attributeList.hashCode());
		result = prime * result
				+ ((claimDate == null) ? 0 : claimDate.hashCode());
		result = prime * result
				+ ((classification == null) ? 0 : classification.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result + (delAdmin ? 1231 : 1237);
		result = prime
				* result
				+ ((emailAddressMatchToken == null) ? 0
						: emailAddressMatchToken.hashCode());
		result = prime
				* result
				+ ((employeeIdMatchToken == null) ? 0 : employeeIdMatchToken
						.hashCode());
		result = prime * result
				+ ((employeeType == null) ? 0 : employeeType.hashCode());
		result = prime
				* result
				+ ((firstNameMatchToken == null) ? 0 : firstNameMatchToken
						.hashCode());
		result = prime * result + (initDefaulLoginFlag ? 1231 : 1237);
		result = prime * result + ((jobCode == null) ? 0 : jobCode.hashCode());
		result = prime * result
				+ ((lastDate == null) ? 0 : lastDate.hashCode());
		result = prime
				* result
				+ ((lastNameMatchToken == null) ? 0 : lastNameMatchToken
						.hashCode());
		result = prime * result
				+ ((locationCd == null) ? 0 : locationCd.hashCode());
		result = prime * result
				+ ((loggedIn == null) ? 0 : loggedIn.hashCode());
		result = prime
				* result
				+ ((maidenNameMatchToken == null) ? 0 : maidenNameMatchToken
						.hashCode());
		result = prime * result
				+ ((maxResultSize == null) ? 0 : maxResultSize.hashCode());
		result = prime * result
				+ ((nickName == null) ? 0 : nickName.hashCode());
		result = prime
				* result
				+ ((nickNameMatchToken == null) ? 0 : nickNameMatchToken
						.hashCode());
		result = prime * result
				+ ((phoneAreaCd == null) ? 0 : phoneAreaCd.hashCode());
		result = prime * result
				+ ((phoneNbr == null) ? 0 : phoneNbr.hashCode());
		result = prime * result
				+ ((principal == null) ? 0 : principal.hashCode());
		result = prime * result
				+ ((searchMode == null) ? 0 : searchMode.hashCode());
		result = prime * result
				+ ((showInSearch == null) ? 0 : showInSearch.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result
				+ ((updatedSince == null) ? 0 : updatedSince.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result
				+ ((userStatus == null) ? 0 : userStatus.hashCode());
		result = prime * result
				+ ((userType == null) ? 0 : userType.hashCode());
		result = prime * result
				+ ((userTypeInd == null) ? 0 : userTypeInd.hashCode());
		result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserSearchBean other = (UserSearchBean) obj;
		if (accountStatus == null) {
			if (other.accountStatus != null)
				return false;
		} else if (!accountStatus.equals(other.accountStatus))
			return false;
		if (attributeList == null) {
			if (other.attributeList != null)
				return false;
		} else if (!attributeList.equals(other.attributeList))
			return false;
		if (claimDate == null) {
			if (other.claimDate != null)
				return false;
		} else if (!claimDate.equals(other.claimDate))
			return false;
		if (classification == null) {
			if (other.classification != null)
				return false;
		} else if (!classification.equals(other.classification))
			return false;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (dateOfBirth == null) {
			if (other.dateOfBirth != null)
				return false;
		} else if (!dateOfBirth.equals(other.dateOfBirth))
			return false;
		if (delAdmin != other.delAdmin)
			return false;
		if (emailAddressMatchToken == null) {
			if (other.emailAddressMatchToken != null)
				return false;
		} else if (!emailAddressMatchToken.equals(other.emailAddressMatchToken))
			return false;
		if (employeeIdMatchToken == null) {
			if (other.employeeIdMatchToken != null)
				return false;
		} else if (!employeeIdMatchToken.equals(other.employeeIdMatchToken))
			return false;
		if (employeeType == null) {
			if (other.employeeType != null)
				return false;
		} else if (!employeeType.equals(other.employeeType))
			return false;
		if (firstNameMatchToken == null) {
			if (other.firstNameMatchToken != null)
				return false;
		} else if (!firstNameMatchToken.equals(other.firstNameMatchToken))
			return false;
		if (initDefaulLoginFlag != other.initDefaulLoginFlag)
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
		if (lastNameMatchToken == null) {
			if (other.lastNameMatchToken != null)
				return false;
		} else if (!lastNameMatchToken.equals(other.lastNameMatchToken))
			return false;
		if (locationCd == null) {
			if (other.locationCd != null)
				return false;
		} else if (!locationCd.equals(other.locationCd))
			return false;
		if (loggedIn == null) {
			if (other.loggedIn != null)
				return false;
		} else if (!loggedIn.equals(other.loggedIn))
			return false;
		if (maidenNameMatchToken == null) {
			if (other.maidenNameMatchToken != null)
				return false;
		} else if (!maidenNameMatchToken.equals(other.maidenNameMatchToken))
			return false;
		if (maxResultSize == null) {
			if (other.maxResultSize != null)
				return false;
		} else if (!maxResultSize.equals(other.maxResultSize))
			return false;
		if (nickName == null) {
			if (other.nickName != null)
				return false;
		} else if (!nickName.equals(other.nickName))
			return false;
		if (nickNameMatchToken == null) {
			if (other.nickNameMatchToken != null)
				return false;
		} else if (!nickNameMatchToken.equals(other.nickNameMatchToken))
			return false;
		if (phoneAreaCd == null) {
			if (other.phoneAreaCd != null)
				return false;
		} else if (!phoneAreaCd.equals(other.phoneAreaCd))
			return false;
		if (phoneNbr == null) {
			if (other.phoneNbr != null)
				return false;
		} else if (!phoneNbr.equals(other.phoneNbr))
			return false;
		if (principal == null) {
			if (other.principal != null)
				return false;
		} else if (!principal.equals(other.principal))
			return false;
		if (searchMode != other.searchMode)
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
		if (updatedSince == null) {
			if (other.updatedSince != null)
				return false;
		} else if (!updatedSince.equals(other.updatedSince))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (userStatus == null) {
			if (other.userStatus != null)
				return false;
		} else if (!userStatus.equals(other.userStatus))
			return false;
		if (userType == null) {
			if (other.userType != null)
				return false;
		} else if (!userType.equals(other.userType))
			return false;
		if (userTypeInd == null) {
			if (other.userTypeInd != null)
				return false;
		} else if (!userTypeInd.equals(other.userTypeInd))
			return false;
		if (zipCode == null) {
			if (other.zipCode != null)
				return false;
		} else if (!zipCode.equals(other.zipCode))
			return false;
		return true;
	}


}
