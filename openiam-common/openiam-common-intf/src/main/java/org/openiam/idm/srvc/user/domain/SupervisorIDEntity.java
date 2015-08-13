package org.openiam.idm.srvc.user.domain;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.user.dto.SupervisorID;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/17/13
 * Time: 12:46 AM
 * To change this template use File | Settings | File Templates.
 */
@DozerDTOCorrespondence(SupervisorID.class)
public class SupervisorIDEntity implements Serializable {
    @Column(name="STAFF_ID", length = 32, nullable = false)
    private String employeeId;

    @Column(name="SUPERVISOR_ID", length = 32, nullable = false)
    private String supervisorId;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupervisorIDEntity that = (SupervisorIDEntity) o;

        if (employeeId != null ? !employeeId.equals(that.employeeId) : that.employeeId != null) return false;
        return !(supervisorId != null ? !supervisorId.equals(that.supervisorId) : that.supervisorId != null);

    }

    @Override
    public int hashCode() {
        int result = employeeId != null ? employeeId.hashCode() : 0;
        result = 31 * result + (supervisorId != null ? supervisorId.hashCode() : 0);
        return result;
    }
}
