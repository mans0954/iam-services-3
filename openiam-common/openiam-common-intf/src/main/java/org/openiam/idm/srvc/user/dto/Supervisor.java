package org.openiam.idm.srvc.user.dto;


import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.SupervisorIDEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

/**
 * Supervisor generated by hbm2java
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "supervisor", propOrder = {
        "id",
        "employee",
        "isPrimarySuper",
        "supervisor"
})
@DozerDTOCorrespondence(SupervisorEntity.class)
public class Supervisor implements java.io.Serializable {

    private SupervisorID id;
    private boolean isPrimarySuper = false;
    private User employee;
    private User supervisor;


    public Supervisor() {
    }


//    public Supervisor(User supervisor, User employee) {
//        this.supervisor = supervisor;
//        this.employee = employee;
//    }

//    public Supervisor(String orgStructureId, User supervisor, User employee) {
//        this.orgStructureId = orgStructureId;
//        this.supervisor = supervisor;
//        this.employee = employee;
//    }
//
//    public Supervisor(String orgStructureId,
//                      User supervisor,
//                      User employee,
//                      String supervisorType,
//                      Integer isPrimarySuper,
//                      Date startDate,
//                      Date endDate,
//                      String status,
//                      String comments) {
//        this.orgStructureId = orgStructureId;
//        this.supervisor = supervisor;
//        this.employee = employee;
//        this.supervisorType = supervisorType;
//        this.isPrimarySuper = isPrimarySuper;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.status = status;
//        this.comments = comments;
//    }

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


    public boolean getIsPrimarySuper() {
        return this.isPrimarySuper;
    }

    public void setIsPrimarySuper(boolean isPrimarySuper) {
        this.isPrimarySuper = isPrimarySuper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Supervisor that = (Supervisor) o;

        if (isPrimarySuper != that.isPrimarySuper) return false;
        if (employee != null ? !employee.equals(that.employee) : that.employee != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return !(supervisor != null ? !supervisor.equals(that.supervisor) : that.supervisor != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (isPrimarySuper ? 1 : 0);
        result = 31 * result + (employee != null ? employee.hashCode() : 0);
        result = 31 * result + (supervisor != null ? supervisor.hashCode() : 0);
        return result;
    }
}


