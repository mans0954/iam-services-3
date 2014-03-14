package org.openiam.idm.srvc.auth.dto;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * Login domain object
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentityDto", propOrder = {
        "identity",
        "id",
        "managedSysId",
        "referredObjectId",
        "status",
        "createDate",
        "createdBy",
        "operation",
        "origPrincipalName"
})
@DozerDTOCorrespondence(IdentityEntity.class)
public class IdentityDto implements java.io.Serializable {
    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;
    protected String id;
    protected String identity;
    protected String managedSysId;
    protected String referredObjectId;
    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    protected String createdBy;
    protected LoginStatusEnum status;
    protected IdentityTypeEnum type;

    protected String origPrincipalName;

    public IdentityDto() {
    }

    public IdentityDto(IdentityTypeEnum type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public String getReferredObjectId() {
        return referredObjectId;
    }

    public void setReferredObjectId(String referredObjectId) {
        this.referredObjectId = referredObjectId;
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

    public LoginStatusEnum getStatus() {
        return status;
    }

    public void setStatus(LoginStatusEnum status) {
        this.status = status;
    }

    public IdentityTypeEnum getType() {
        return type;
    }

    public void setType(IdentityTypeEnum type) {
        this.type = type;
    }

    public String getOrigPrincipalName() {
        return origPrincipalName;
    }

    public void setOrigPrincipalName(String origPrincipalName) {
        this.origPrincipalName = origPrincipalName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("IdentityDto");
        sb.append("{identity='").append(identity).append('\'');
        sb.append(", managedSysId='").append(managedSysId).append('\'');
        sb.append(", referredObjectId='").append(referredObjectId).append('\'');
        sb.append(", type=").append(type);
        sb.append(", origPrincipalName='").append(origPrincipalName).append('\'');
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentityDto that = (IdentityDto) o;

        if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;
        if (managedSysId != null ? !managedSysId.equals(that.managedSysId) : that.managedSysId != null) return false;
        if (referredObjectId != null ? !referredObjectId.equals(that.referredObjectId) : that.referredObjectId != null)
            return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = identity != null ? identity.hashCode() : 0;
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (referredObjectId != null ? referredObjectId.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
