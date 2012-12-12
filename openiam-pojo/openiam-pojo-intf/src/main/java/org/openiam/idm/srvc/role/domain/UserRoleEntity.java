package org.openiam.idm.srvc.role.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.role.dto.UserRole;

import javax.persistence.*;

import java.util.Date;

/**
 * Created by: Alexander Duckardt
 * Date: 17.11.12
 */
@Entity
@Table(name="USER_ROLE")
@DozerDTOCorrespondence(UserRole.class)
@Embeddable
public class UserRoleEntity implements java.io.Serializable {
    private static final long serialVersionUID = -3785768336629177182L;
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="USER_ROLE_ID", length=32)
    private String userRoleId;
    
    @Column(name="USER_ID",length=32,nullable=false)
    private String userId;
    
    @Column(name="ROLE_ID",length=32,nullable=false)
    @Field(name = "roleId", index = Index.UN_TOKENIZED, store = Store.YES)
    private String roleId;
    
    @Column(name="STATUS",length=20)
    private String status;
    
    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.DATE)
    private Date createDate;
    
    @Column(name="START_DATE",length=19)
    @Temporal(TemporalType.DATE)
    private Date startDate;
    
    @Column(name="END_DATE",length=19)
    @Temporal(TemporalType.DATE)
    private Date endDate;
    
    @Column(name="CREATED_BY",length=19)
    private String createdBy;

    public UserRoleEntity() {
    }

    public UserRoleEntity(final String userId, final String roleId) {
        this.userId = userId;
        this.roleId = roleId;
        this.status = "ACTIVE";
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRoleEntity that = (UserRoleEntity) o;

        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (roleId != null ? !roleId.equals(that.roleId) : that.roleId != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (userRoleId != null ? !userRoleId.equals(that.userRoleId) : that.userRoleId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userRoleId == null) ? 0 : userRoleId.hashCode());
        return result;
    }
}
