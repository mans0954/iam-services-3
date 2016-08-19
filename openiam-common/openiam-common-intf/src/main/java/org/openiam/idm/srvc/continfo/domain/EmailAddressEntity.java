package org.openiam.idm.srvc.continfo.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.bridge.UserBrigde;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.internationalization.Internationalized;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Table(name = "EMAIL_ADDRESS")
@DozerDTOCorrespondence(EmailAddress.class)
//@Indexed
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Document(indexName = ESIndexName.EMAIL, type= ESIndexType.EMAIL)
@AttributeOverride(name = "id", column = @Column(name = "EMAIL_ID"))
@Internationalized
public class EmailAddressEntity extends AbstractMetdataTypeEntity {

    @Column(name = "ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;

    @Column(name = "DESCRIPTION", length = 100)
    @Size(max = 100, message = "validator.email.description.toolong")
    private String description;

    @Field(type = FieldType.String, index = FieldIndex.analyzed, store= true)
    @Column(name = "EMAIL_ADDRESS", length = 320)
    @Size(max = 320, message = "validator.email.toolong")
    protected String emailAddress;
    
    @Transient
    @Field(type = FieldType.String, index = FieldIndex.analyzed, store= true)
    private String emailUsername;
    
    @Transient
    @Field(type = FieldType.String, index = FieldIndex.analyzed, store= true)
    private String emailDomain;

    @Column(name = "IS_DEFAULT")
    @Type(type = "yes_no")
    private boolean isDefault = false;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @ElasticsearchFieldBridge(impl = UserBrigde.class)
    private UserEntity parent;

    @Column(name = "NAME", length = 100)
    @Size(max = 100, message = "validator.email.label.toolong")
    private String name;
    
    @Column(name = "LAST_UPDATE", length = 19)
    //@LuceneLastUpdate
    private Date lastUpdate;
    
    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;
    
    public EmailAddressEntity() {
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
	
	public String getEmailUsername() {
		return emailUsername;
	}

	public void setEmailUsername(String emailUsername) {
		this.emailUsername = emailUsername;
	}

	public String getEmailDomain() {
		return emailDomain;
	}

	public void setEmailDomain(String emailDomain) {
		this.emailDomain = emailDomain;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + (isDefault ? 1231 : 1237);
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
        if (!super.equals(obj))
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
		return true;
	}

    @Override
    public String toString() {
        return "EmailAddressEntity{" +
               "isActive=" + isActive +
               ", description='" + description + '\'' +
               ", emailAddress='" + emailAddress + '\'' +
               ", isDefault=" + isDefault +
               ", parent=" + parent +
               ", name='" + name + '\'' +
               ", lastUpdate=" + lastUpdate +
               ", createDate=" + createDate +
               ", " + super.toString()+"}";
    }
}
