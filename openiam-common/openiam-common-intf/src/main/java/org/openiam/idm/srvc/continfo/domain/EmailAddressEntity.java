package org.openiam.idm.srvc.continfo.domain;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.openiam.core.dao.lucene.LuceneId;
import org.openiam.core.dao.lucene.LuceneLastUpdate;
import org.openiam.core.dao.lucene.bridge.OrganizationBridge;
import org.openiam.core.dao.lucene.bridge.UserBridge;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "EMAIL_ADDRESS")
@DozerDTOCorrespondence(EmailAddress.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed
public class EmailAddressEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "EMAIL_ID", length = 32, nullable = false)
    @LuceneId
    @DocumentId
    private String emailId;

    @Column(name = "ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;

    @Column(name = "DESCRIPTION", length = 100)
    @Size(max = 100, message = "validator.email.description.toolong")
    private String description;

    @Fields ({
        @Field(index = Index.TOKENIZED),
        @Field(name = "emailAddress", index = Index.TOKENIZED, store = Store.YES),
        @Field(name = "emailAddressUntokenized", index = Index.UN_TOKENIZED, store = Store.YES)
    })
    @Column(name = "EMAIL_ADDRESS", length = 320)
    @Size(max = 320, message = "validator.email.toolong")
    private String emailAddress;

    @Column(name = "IS_DEFAULT")
    @Type(type = "yes_no")
    private boolean isDefault = false;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    @Field(name="parent", bridge=@FieldBridge(impl=UserBridge.class), store=Store.YES)
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity parent;

    @Column(name = "NAME", length = 100)
    @Size(max = 100, message = "validator.email.label.toolong")
    private String name;
    
    @Column(name = "LAST_UPDATE", length = 19)
    @LuceneLastUpdate
    private Date lastUpdate;
    
    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID", insertable=true, updatable=true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private MetadataTypeEntity metadataType;

    public EmailAddressEntity() {
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public UserEntity getParent() {
        return parent;
    }

    public void setParent(UserEntity parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
    public MetadataTypeEntity getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(MetadataTypeEntity metadataType) {
        this.metadataType = metadataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailAddressEntity that = (EmailAddressEntity) o;

        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (emailAddress != null ? !emailAddress.equals(that.emailAddress) : that.emailAddress != null) return false;
        if (emailId != null ? !emailId.equals(that.emailId) : that.emailId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = emailId != null ? emailId.hashCode() : 0;
        result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("EmailAddressEntity");
        sb.append("{emailAddress='").append(emailAddress).append('\'');
        sb.append(", isDefault=").append(isDefault);
        sb.append(", name='").append(name).append('\'');
        sb.append(", lastUpdate=").append(lastUpdate);
        sb.append(", metadataType=").append(metadataType);
        sb.append('}');
        return sb.toString();
    }
}
