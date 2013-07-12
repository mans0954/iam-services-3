package org.openiam.idm.srvc.policy.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.ITPolicyEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ITPolicy", propOrder = { "policyId", "approveType", "active", "createDate", "createdBy",
        "updateDate", "updatedBy", "policyContent", "confirmation" })
@DozerDTOCorrespondence(ITPolicyEntity.class)
public class ITPolicy implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String policyId;
    private ITPolicyApproveType approveType;
    private boolean active;
    @XmlSchemaType(name = "dateTime")
    private Date createDate;
    private String createdBy;
    @XmlSchemaType(name = "dateTime")
    private Date updateDate;
    private String updatedBy;
    private String policyContent;
    private String confirmation;

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

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
