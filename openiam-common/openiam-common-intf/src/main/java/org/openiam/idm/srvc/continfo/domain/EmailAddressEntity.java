package org.openiam.idm.srvc.continfo.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.core.dao.lucene.LuceneLastUpdate;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.annotation.ElasticsearchField;
import org.openiam.elasticsearch.annotation.ElasticsearchId;
import org.openiam.elasticsearch.annotation.ElasticsearchIndex;
import org.openiam.elasticsearch.annotation.ElasticsearchMapping;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.constants.ElasticsearchStore;
import org.openiam.elasticsearch.constants.Index;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

//import org.hibernate.search.annotations.Analyze;
//import org.hibernate.search.annotations.DocumentId;
//import org.hibernate.search.annotations.Field;
//import org.hibernate.search.annotations.FieldBridge;
//import org.hibernate.search.annotations.Fields;
//import org.hibernate.search.annotations.Index;
//import org.hibernate.search.annotations.Indexed;
//import org.hibernate.search.annotations.Store;

@Entity
@Table(name = "EMAIL_ADDRESS")
@DozerDTOCorrespondence(EmailAddress.class)
//@Indexed
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ElasticsearchIndex(indexName = ESIndexName.USERS)
@ElasticsearchMapping(typeName = ESIndexType.EMAIL, store = ElasticsearchStore.No)
public class EmailAddressEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "EMAIL_ID", length = 32, nullable = false)
    @ElasticsearchId
    private String emailId;

    @Column(name = "ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;

    @Column(name = "DESCRIPTION", length = 100)
    @Size(max = 100, message = "validator.email.description.toolong")
    private String description;

//    @Fields ({
//        @Field(analyze = Analyze.YES),
//        @Field(name = "emailAddress", analyze = Analyze.YES, store = Store.YES)
//    })
    @ElasticsearchField(name = "emailAddress", store = ElasticsearchStore.Yes, index = Index.Analyzed)
    @Column(name = "EMAIL_ADDRESS", length = 320)
    @Size(max = 320, message = "validator.email.toolong")
    private String emailAddress;

    @Column(name = "IS_DEFAULT")
    @Type(type = "yes_no")
    private boolean isDefault = false;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
//    @Field(name="parent", bridge=@FieldBridge(impl=UserBridge.class), store=Store.YES)
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
    private MetadataTypeEntity metadataType;

    public EmailAddressEntity() {
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Boolean getIsActive() {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime * result + ((emailId == null) ? 0 : emailId.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + (isDefault ? 1231 : 1237);
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((metadataType == null) ? 0 : metadataType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailAddressEntity other = (EmailAddressEntity) obj;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		if (emailId == null) {
			if (other.emailId != null)
				return false;
		} else if (!emailId.equals(other.emailId))
			return false;
		if (isActive != other.isActive)
			return false;
		if (isDefault != other.isDefault)
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
        if (metadataType == null) {
            if (other.metadataType != null)
                return false;
        } else if (!metadataType.equals(other.metadataType))
            return false;
		return true;
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
