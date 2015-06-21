package org.openiam.idm.srvc.policy.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.openiam.idm.srvc.policy.dto.ITPolicyApproveType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "IT_POLICY")
@DozerDTOCorrespondence(ITPolicy.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides(value= {
        @AttributeOverride(name = "id", column = @Column(name = "IT_POLICY_ID")),
})
public class ITPolicyEntity extends KeyEntity {

    @Enumerated(EnumType.STRING)
    @Column(name="APPROVE_TYPE", length=64)
    private ITPolicyApproveType approveType;
    @Column(name = "ACTIVE")
    @Type(type = "yes_no")
    private boolean active;
    @Column(name="CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(name="CREATED_BY",length=32)
    private String createdBy;
    @Column(name="UPDATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;
    @Column(name="UPDATED_BY",length=32)
    private String updatedBy;
    @Lob
    @Column(name = "POLICY_CONTENT",nullable=true)
    private String policyContent;
    @Column(name="CONFIRMATION",length=255,nullable=true)
    private String confirmation;

    public ITPolicyApproveType getApproveType() {
        return approveType;
    }

    public void setApproveType(ITPolicyApproveType approveType) {
        this.approveType = approveType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getPolicyContent() {
        return policyContent;
    }

    public void setPolicyContent(String policyContent) {
        this.policyContent = policyContent;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }
}
