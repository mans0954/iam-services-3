package org.openiam.idm.srvc.auth.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.*;
import org.openiam.core.dao.lucene.LuceneId;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="IDENTITY")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(IdentityDto.class)
@Indexed
public class IdentityEntity implements java.io.Serializable  {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "IDENTITY_ID", length = 32, nullable = false)
    @LuceneId
    @DocumentId
    private String id;

    @Field(name = "identity", index = Index.TOKENIZED, store = Store.YES)
    @Column(name="IDENTITY",length=320)
    private String identity;

    @Field(name = "managedSysId", index = Index.UN_TOKENIZED, store = Store.YES)
    @Column(name="MANAGED_SYS_ID",length=50)
    private String managedSysId;

    @Field(name = "referredObjectId", index = Index.UN_TOKENIZED, store = Store.YES)
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

    public IdentityEntity() {
    }

    public IdentityEntity(IdentityTypeEnum type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentityEntity that = (IdentityEntity) o;

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