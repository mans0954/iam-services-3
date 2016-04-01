package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.SupervisorID;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupervisorSearchBean", propOrder = {
        "id",
        "isPrimarySuper",
        "employee",
        "supervisor"
})
public class SupervisorSearchBean extends AbstractSearchBean<Supervisor, String> implements SearchBean<Supervisor, String> {


    private SupervisorID id;
    private boolean isPrimarySuper = false;
    private User employee;
    private User supervisor;

    public SupervisorID getId() {
        return id;
    }

    public void setId(SupervisorID id) {
        this.id = id;
    }

    public boolean isPrimarySuper() {
        return isPrimarySuper;
    }

    public void setPrimarySuper(boolean primarySuper) {
        isPrimarySuper = primarySuper;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(isPrimarySuper)
                .append(id != null ? id.hashCode() : "")
                .append(employee != null ? employee.hashCode() : "")
                .append(supervisor != null ? supervisor.hashCode() : "")
                .toString();
    }
}
