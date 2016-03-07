package org.openiam.idm.srvc.user.domain;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.user.dto.Supervisor;

import java.io.Serializable;

@Entity
@Table(name = "ORG_STRUCTURE")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(Supervisor.class)
public class SupervisorEntity implements Serializable {
    @EmbeddedId
    private SupervisorIDEntity id;

    @Column(name = "IS_PRIMARY_SUPER")
    @Type(type = "yes_no")
    private boolean isPrimarySuper = false;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="STAFF_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    private UserEntity employee;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="SUPERVISOR_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    private UserEntity supervisor;

    public SupervisorIDEntity getId(){
        return this.id;
    }
    public void setId(SupervisorIDEntity id){
        this.id=id;
    }

    public UserEntity getEmployee() {
        return employee;
    }

    public void setEmployee(UserEntity employee) {
        this.employee = employee;
    }

    public boolean getIsPrimarySuper() {
        return isPrimarySuper;
    }

    public void setIsPrimarySuper(boolean primarySuper) {
        isPrimarySuper = primarySuper;
    }

    public UserEntity getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(UserEntity supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupervisorEntity that = (SupervisorEntity) o;

        if (isPrimarySuper != that.isPrimarySuper) return false;
        if (employee != null ? !employee.equals(that.employee) : that.employee != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (supervisor != null ? !supervisor.equals(that.supervisor) : that.supervisor != null) return false;

        return true;
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
