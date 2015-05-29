package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentitySearchBean", propOrder = {
        "identity",
        "managedSysId",
        "referredObjectId",
        "status",
        "type",
        "createFromDate",
        "createToDate",
        "createdBy"
})
public class IdentitySearchBean extends AbstractSearchBean<IdentityDto, String> implements SearchBean<IdentityDto, String>, Serializable {
    private static final long serialVersionUID = 1L;

    private String identity;

    private String managedSysId;

    private String referredObjectId;

    @XmlSchemaType(name = "dateTime")
    private Date createFromDate;

    @XmlSchemaType(name = "dateTime")
    private Date createToDate;

    private String createdBy;

    private LoginStatusEnum status;

    private IdentityTypeEnum type;

    public IdentitySearchBean() {
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

    public Date getCreateFromDate() {
        return createFromDate;
    }

    public void setCreateFromDate(Date createFromDate) {
        this.createFromDate = createFromDate;
    }

    public Date getCreateToDate() {
        return createToDate;
    }

    public void setCreateToDate(Date createToDate) {
        this.createToDate = createToDate;
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

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(identity != null ? identity : "")
                .append(managedSysId != null ? managedSysId : "")
                .append(referredObjectId != null ? referredObjectId : "")
                .append(createFromDate != null ? createFromDate.hashCode() : "")
                .append(createToDate != null ? createToDate.hashCode() : "")
                .append(createdBy != null ? createdBy.hashCode() : "")
                .append(status != null ? status.getValue().hashCode() : "")
                .append(type != null ? type.getValue().hashCode() : "")
                .append(getKey() != null ? getKey() : "")
                .toString();
    }
}
