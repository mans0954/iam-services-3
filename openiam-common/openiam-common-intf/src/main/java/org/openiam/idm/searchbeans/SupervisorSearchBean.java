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
public class SupervisorSearchBean extends AbstractSearchBean<Supervisor, String> {


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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((employee == null) ? 0 : employee.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPrimarySuper ? 1231 : 1237);
		result = prime * result
				+ ((supervisor == null) ? 0 : supervisor.hashCode());
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
		SupervisorSearchBean other = (SupervisorSearchBean) obj;
		if (employee == null) {
			if (other.employee != null)
				return false;
		} else if (!employee.equals(other.employee))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isPrimarySuper != other.isPrimarySuper)
			return false;
		if (supervisor == null) {
			if (other.supervisor != null)
				return false;
		} else if (!supervisor.equals(other.supervisor))
			return false;
		return true;
	}

    
}
