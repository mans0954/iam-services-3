package org.openiam.provision.dto.srcadapter;

import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.common.UserSearchKey;
import org.openiam.provision.dto.common.UserSearchMemberhipKey;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(name = "SourceAdapterRequest", propOrder = {"action", "forceMode", "key", "requestor", "logins", "firstName", "lastName", "employeeId",
        "middleName", "prefix", "sex", "status", "secondaryStatus", "suffix",
        "title", "nickname", "maidenName", "userTypeId", "startDate", "lastDate",
        "userAttributes", "groups", "roles", "resources", "organizations", "emails", "addresses", "phones",
        /*"subordinates",*/ "supervisors","alternativeContact", "passwordRequest", "userSubTypeId", "prefixPartnerName", "prefixLastName", "partnerName"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "user",namespace = "http://www.openiam.org/service/provision/dto/srcadapter")
public class SourceAdapterRequest implements Serializable {
    private SourceAdapterOperationEnum action;
    private UserSearchKey key;
    @XmlElement(required = true)
    private UserSearchKey requestor;
    private UserSearchKey alternativeContact;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String middleName;
    private String title;
    @Enumerated(EnumType.STRING)
    private UserStatusEnum status;
    @Enumerated(EnumType.STRING)
    private UserStatusEnum secondaryStatus;
    private String userTypeId;
    protected String startDate;
    protected String lastDate;
    private String prefix;
    private String sex;
    private String suffix;
    private String nickname;
    private String maidenName;
    private String userSubTypeId;
    private String partnerName;
    private String prefixPartnerName;
    private String prefixLastName;

    @XmlElement(name = "skipWarnings")
    private boolean forceMode;

    @XmlElementWrapper(name = "principals-set")
    @XmlElements({
            @XmlElement(name = "principal")}
    )
    private Set<SourceAdapterLoginRequest> logins;

    @XmlElementWrapper(name = "groups-set")
    @XmlElements({
            @XmlElement(name = "group")}
    )
    private Set<SourceAdapterEntityManagedSystemRequest> groups;

    @XmlElementWrapper(name = "roles-set")
    @XmlElements({
            @XmlElement(name = "role")}
    )
    private Set<SourceAdapterEntityManagedSystemRequest> roles;

    @XmlElementWrapper(name = "resources-set")
    @XmlElements({
            @XmlElement(name = "resource")}
    )
    private Set<SourceAdapterEntityRequest> resources;

    @XmlElementWrapper(name = "affiliation-set")
    @XmlElements({
            @XmlElement(name = "affiliation")}
    )
    private Set<SourceAdapterOrganizationRequest> organizations;

    @XmlElementWrapper(name = "user-attributes-set")
    @XmlElements({
            @XmlElement(name = "user-attribute")}
    )
    private Set<SourceAdapterAttributeRequest> userAttributes;

    @XmlElementWrapper(name = "user-emails-set")
    @XmlElements({
            @XmlElement(name = "user-email")}
    )
    private Set<SourceAdapterEmailRequest> emails;

    @XmlElementWrapper(name = "user-addresses-set")
    @XmlElements({
            @XmlElement(name = "user-address")}
    )
    private Set<SourceAdapterAddressRequest> addresses;

    @XmlElementWrapper(name = "user-phones-set")
    @XmlElements({
            @XmlElement(name = "user-phone")}
    )
    private Set<SourceAdapterPhoneRequest> phones;

    @XmlElementWrapper(name = "user-supervisors-set")
    @XmlElements({
            @XmlElement(name = "user-supervisor")}
    )
    private Set<UserSearchMemberhipKey> supervisors;

    //    @XmlElementWrapper(name = "user-subordinates-set")
//    @XmlElements({
//            @XmlElement(name = "user-subordinate")}
//    )
//    private Set<SourceAdapterMemberhipKey> subordinates;
    private SourceAdapterPasswordRequest passwordRequest;

    //forceMode
    public Set<SourceAdapterAttributeRequest> getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(Set<SourceAdapterAttributeRequest> userAttributes) {
        this.userAttributes = userAttributes;
    }

    public SourceAdapterOperationEnum getAction() {
        return action;
    }

    public void setAction(SourceAdapterOperationEnum action) {
        this.action = action;
    }

    public UserSearchKey getKey() {
        return key;
    }

    public void setKey(UserSearchKey key) {
        this.key = key;
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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
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

    public String getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(String userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public Set<SourceAdapterEntityManagedSystemRequest> getGroups() {
        return groups;
    }

    public void setGroups(Set<SourceAdapterEntityManagedSystemRequest> groups) {
        this.groups = groups;
    }

    public Set<SourceAdapterEntityManagedSystemRequest> getRoles() {
        return roles;
    }

    public void setRoles(Set<SourceAdapterEntityManagedSystemRequest> roles) {
        this.roles = roles;
    }

    public Set<SourceAdapterOrganizationRequest> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Set<SourceAdapterOrganizationRequest> organizations) {
        this.organizations = organizations;
    }

    public Set<SourceAdapterEmailRequest> getEmails() {
        return emails;
    }

    public void setEmails(Set<SourceAdapterEmailRequest> emails) {
        this.emails = emails;
    }

    public Set<SourceAdapterAddressRequest> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<SourceAdapterAddressRequest> addresses) {
        this.addresses = addresses;
    }

    public Set<SourceAdapterPhoneRequest> getPhones() {
        return phones;
    }

    public void setPhones(Set<SourceAdapterPhoneRequest> phones) {
        this.phones = phones;
    }

    public Set<UserSearchMemberhipKey> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(Set<UserSearchMemberhipKey> supervisors) {
        this.supervisors = supervisors;
    }

    public Set<SourceAdapterEntityRequest> getResources() {
        return resources;
    }

    public void setResources(Set<SourceAdapterEntityRequest> resources) {
        this.resources = resources;
    }

    public Set<SourceAdapterLoginRequest> getLogins() {
        return logins;
    }

    public void setLogins(Set<SourceAdapterLoginRequest> logins) {
        this.logins = logins;
    }

    //    public Set<SourceAdapterMemberhipKey> getSubordinates() {
//        return subordinates;
//    }
//
//    public void setSubordinates(Set<SourceAdapterMemberhipKey> subordinates) {
//        this.subordinates = subordinates;
//    }

    public UserSearchKey getRequestor() {
        return requestor;
    }

    public void setRequestor(UserSearchKey requestor) {
        this.requestor = requestor;
    }

    public boolean isForceMode() {
        return forceMode;
    }

    public void setForceMode(boolean forceMode) {
        this.forceMode = forceMode;
    }

    public SourceAdapterPasswordRequest getPasswordRequest() {
        return passwordRequest;
    }

    public void setPasswordRequest(SourceAdapterPasswordRequest passwordRequest) {
        this.passwordRequest = passwordRequest;
    }

    public String getUserSubTypeId() {
        return userSubTypeId;
    }

    public void setUserSubTypeId(String userSubTypeId) {
        this.userSubTypeId = userSubTypeId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPrefixPartnerName() {
        return prefixPartnerName;
    }

    public void setPrefixPartnerName(String prefixPartnerName) {
        this.prefixPartnerName = prefixPartnerName;
    }

    public String getPrefixLastName() {
        return prefixLastName;
    }

    public void setPrefixLastName(String prefixLastName) {
        this.prefixLastName = prefixLastName;
    }

    public UserSearchKey getAlternativeContact() {
        return alternativeContact;
    }

    public void setAlternativeContact(UserSearchKey alternativeContact) {
        this.alternativeContact = alternativeContact;
    }
}
