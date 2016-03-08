package org.openiam.idm.srvc.auth.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;

@Entity
@Table(name="IDENTIFICATION")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(IdentityDto.class)
@AttributeOverride(name = "id", column = @Column(name = "IDENTITY_ID"))
public class IdentityEntity extends KeyEntity  {

//    @Field(name = "identity", analyze = Analyze.YES, store = Store.YES)
    @Column(name="IDENTIFICATION",length=320)
    private String identity;

//    @Field(name = "managedSysId", analyze = Analyze.NO, store = Store.YES)
    @Column(name="MANAGED_SYS_ID",length=50)
    private String managedSysId;

//    @Field(name = "referredObjectId", analyze = Analyze.NO, store = Store.YES)
    @Column(name="REFERRED_OBJECT_ID",length=32)
    private String referredObjectId;

    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name="CREATED_BY",length = 32)
    private String createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name="STATUS",length = 20)
    private LoginStatusEnum status;

    @Enumerated(EnumType.STRING)
    @Column(name="IDENTITY_TYPE", length=20)
    private IdentityTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(name="PROV_STATUS",length = 20)
    private ProvLoginStatusEnum provStatus;

    @Column(name = "LAST_UPDATE", length = 19)
    //@LuceneLastUpdate
    private Date lastUpdate;

    public IdentityEntity() {
    }

    public IdentityEntity(IdentityTypeEnum type) {
        this.type = type;
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

	public ProvLoginStatusEnum getProvStatus() {
		return provStatus;
	}

	public void setProvStatus(ProvLoginStatusEnum provStatus) {
		this.provStatus = provStatus;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}


	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentityEntity that = (IdentityEntity) o;

        if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;
        if (managedSysId != null ? !managedSysId.equals(that.managedSysId) : that.managedSysId != null) return false;
        if (referredObjectId != null ? !referredObjectId.equals(that.referredObjectId) : that.referredObjectId != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (provStatus != null ? !provStatus.equals(that.provStatus) : that.provStatus != null) return false;
        return !(lastUpdate != null ? !lastUpdate.equals(that.lastUpdate) : that.lastUpdate != null);
    }

    @Override
    public int hashCode() {
        int result = identity != null ? identity.hashCode() : 0;
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (referredObjectId != null ? referredObjectId.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (provStatus != null ? provStatus.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        return result;
    }
}