package org.openiam.provision.dto.accessmodel;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Set;

/**
 * Created by zaporozhec on 7/28/16.
 */
@XmlType(propOrder = {"firstName", "lastName", "employeeId", "logins", "directEntitles", "compiledEntitlements"})
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccessControlBean {
    private String firstName;
    private String lastName;
    private String employeeId;
    @XmlElementWrapper(name = "logins")
    @XmlElements({
            @XmlElement(name = "login")}
    )
    private Set<String> logins;

    @XmlElementWrapper(name = "directEntitles")
    @XmlElements({
            @XmlElement(name = "bean")}
    )
    private Set<UserAccessControlMemberBean> directEntitles;

    @XmlElementWrapper(name = "compiledEntitlements")
    @XmlElements({
            @XmlElement(name = "bean")}
    )
    private Set<UserAccessControlMemberBean> compiledEntitlements;

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

    public Set<String> getLogins() {
        return logins;
    }

    public void setLogins(Set<String> logins) {
        this.logins = logins;
    }

    public Set<UserAccessControlMemberBean> getDirectEntitles() {
        return directEntitles;
    }

    public void setDirectEntitles(Set<UserAccessControlMemberBean> directEntitles) {
        this.directEntitles = directEntitles;
    }

    public Set<UserAccessControlMemberBean> getCompiledEntitlements() {
        return compiledEntitlements;
    }

    public void setCompiledEntitlements(Set<UserAccessControlMemberBean> compiledEntitlements) {
        this.compiledEntitlements = compiledEntitlements;
    }
}
