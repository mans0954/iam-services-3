package org.openiam.provision.dto;

import org.openiam.idm.srvc.user.dto.UserStatusEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.*;
import java.util.Set;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(name = "SourceAdapterRequest", propOrder = {"action", "key", "firstName", "lastName", "employeeId",
        "middleName", "prefix", "sex", "status", "secondaryStatus", "suffix",
        "title", "nickname", "maidenName", "userTypeId", "startDate", "lastDate", "groups", "roles", "userAttributes"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterRequest {
    private SourceAdapterOperationEnum action;
    @XmlElement(required = true)
    private SourceAdapterKey key;
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

    @XmlElementWrapper(name = "groups-set")
    @XmlElements({
            @XmlElement(name = "group")}
    )
    private Set<SourceAdapterEntityRequest> groups;

    @XmlElementWrapper(name = "roles-set")
    @XmlElements({
            @XmlElement(name = "role")}
    )
    private Set<SourceAdapterEntityRequest> roles;

    @XmlElementWrapper(name = "user-attributes-set")
    @XmlElements({
            @XmlElement(name = "user-attribute")}
    )
    private Set<SourceAdapterAttributeRequest> userAttributes;

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

    public SourceAdapterKey getKey() {
        return key;
    }

    public void setKey(SourceAdapterKey key) {
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

    public Set<SourceAdapterEntityRequest> getGroups() {
        return groups;
    }

    public void setGroups(Set<SourceAdapterEntityRequest> groups) {
        this.groups = groups;
    }

    public Set<SourceAdapterEntityRequest> getRoles() {
        return roles;
    }

    public void setRoles(Set<SourceAdapterEntityRequest> roles) {
        this.roles = roles;
    }
}
