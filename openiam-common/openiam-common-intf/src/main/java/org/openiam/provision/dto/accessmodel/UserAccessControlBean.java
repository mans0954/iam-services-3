package org.openiam.provision.dto.accessmodel;

import org.openiam.idm.srvc.user.dto.UserStatusEnum;

import javax.xml.bind.annotation.*;
import java.util.Set;

/**
 * Created by zaporozhec on 7/28/16.
 */
@XmlType(propOrder = {"firstName", "lastName", "employeeId", "login", "entitlements", "status", "isLocked", "lastDate", "lastLoginTime", "secondaryStatus", "startDate"})
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccessControlBean {
    private String firstName;
    private String lastName;
    private String employeeId;
    private UserStatusEnum status;
    private UserStatusEnum secondaryStatus;
    private String lastDate;
    private String startDate;
    private boolean isLocked;
    private String lastLoginTime;
    private String login;

    @XmlElementWrapper(name = "entitlements")
    @XmlElements({@XmlElement(name = "bean")})
    private Set<UserAccessControlMemberBean> entitlements;

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

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Set<UserAccessControlMemberBean> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(Set<UserAccessControlMemberBean> entitlements) {
        this.entitlements = entitlements;
    }
}
