package org.openiam.idm.searchbeans;

import org.apache.commons.lang.StringUtils;
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
        "updatedSince"
})
public class UserSearchBean extends AbstractSearchBean<User, String> implements SearchBean<User, String>,
        Serializable {

    protected String firstName = null;
    protected String lastName = null;
    protected String nickName = null;
    protected String maidenName = null;
    protected String accountStatus = null;
    protected String userStatus = null;
    protected String phoneAreaCd = null;
    protected String phoneNbr = null;
    protected String employeeType = null;
    protected String employeeId = null;
    protected Set<String> groupIdSet = null;
    protected Set<String> roleIdSet = null;
    protected Set<String> resourceIdSet = null;
    protected String emailAddress = null;
    protected LoginSearchBean principal;
    protected String attributeName;
    protected String attributeValue;
    protected String attributeElementId;
    protected String userId;
    protected String locationCd;
    protected Integer showInSearch;
    protected Integer maxResultSize;

    protected String userTypeInd;
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
    protected String zipCode;

    protected String loggedIn = null;
    protected boolean delAdmin = false;

    protected Set<String> organizationIdList = new HashSet<String>();
    protected List<SearchAttribute> attributeList = new ArrayList<SearchAttribute>();

    private String requesterId;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public LoginSearchBean getPrincipal() {
        return principal;
    }

    public void setPrincipal(LoginSearchBean principal) {
        this.principal = principal;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

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

	public String getMaidenName() {
		return maidenName;
	}

	public void setMaidenName(String maidenName) {
		this.maidenName = maidenName;
	}
    
    
}
