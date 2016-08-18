package org.openiam.idm.srvc.user.dto;

import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SortParam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.List;

/**
 * Created by zaporozhec on 8/16/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lightUserSearchRequest", propOrder = {
        "login",
        "employeeId",
        "emailAddress",
        "lastName",
        "from",
        "size",
        "sortParam",
        "requesterId",
        "status",
        "secondaryStatus"

})
@XmlRootElement(name = "light-search-request", namespace = "")
public class LightSearchRequest implements Serializable {
    private String employeeId;
    private String login;
    private String lastName;
    private String emailAddress;
    private UserStatusEnum status;
    private UserStatusEnum secondaryStatus;

    List<SortParam> sortParam;
    private int from = -1;
    private int size = -1;
    private String requesterId;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<SortParam> getSortParam() {
        return sortParam;
    }

    public void setSortParam(List<SortParam> sortParam) {
        this.sortParam = sortParam;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
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
}
