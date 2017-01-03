package org.openiam.idm.srvc.continfo.domain;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.bridge.UserBrigde;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.internationalization.Internationalized;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Table(name = "PHONE")
@DozerDTOCorrespondence(Phone.class)
//@Indexed
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Document(indexName = ESIndexName.PHONE, type= ESIndexType.PHONE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "PHONE_ID")),
    @AttributeOverride(name = "name", column = @Column(name="NAME", length=40))
})
@Internationalized
public class PhoneEntity extends AbstractMetdataTypeEntity {
   
    @Column(name="ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= false)
    @Column(name="AREA_CD", length=10)
    @Size(max = 10, message = "validator.phone.area.code.toolong")
    private String areaCd;

    @Column(name="COUNTRY_CD", length=3)
    @Size(max = 3, message = "validator.phone.country.code.toolong")
    private String countryCd;

    @Column(name="DESCRIPTION", length=100)
    @Size(max = 100, message = "validator.phone.description.toolong")
    private String description;

    @Column(name="IS_DEFAULT")
    @Type(type = "yes_no")
    private boolean isDefault = false;
    
    @Column(name="TOPT_SECRET", length=100)
    private String totpSecret;
    
    /*
    @Column(name="VALIDATED")
    @Type(type = "yes_no")
    private boolean validated;
	*/

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @ElasticsearchFieldBridge(impl = UserBrigde.class)
    private UserEntity parent;

    @Column(name="PHONE_EXT", length=20)
    @Size(max = 20, message = "validator.phone.extension.toolong")
    @Field(type = FieldType.String, index = FieldIndex.analyzed, store= false)
    private String phoneExt;

    @Field(type = FieldType.String, index = FieldIndex.analyzed, store= false)
    @Column(name="PHONE_NBR", length=50)
    @Size(max = 50, message = "validator.phone.number.toolong")
    private String phoneNbr;

    /*
    @Column(name="PHONE_TYPE", length=20)
    private String phoneType;
    */
    
    @Column(name = "LAST_UPDATE", length = 19)
    //@LuceneLastUpdate
    private Date lastUpdate;
    
    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    public PhoneEntity() {
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getAreaCd() {
        return areaCd;
    }

    public void setAreaCd(String areaCd) {
        this.areaCd = areaCd;
    }

    public String getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPhoneExt() {
        return phoneExt;
    }

    public void setPhoneExt(String phoneExt) {
        this.phoneExt = phoneExt;
    }

    public String getPhoneNbr() {
        return phoneNbr;
    }

    public void setPhoneNbr(String phoneNbr) {
        this.phoneNbr = phoneNbr;
    }

    @Override
    @Size(max = 50, message = "validator.phone.label.toolong")
    public String getName() {
        return name;
    }

    /*
    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }
    */

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

	public String getTotpSecret() {
		return totpSecret;
	}

	public void setTotpSecret(String totpSecret) {
		this.totpSecret = totpSecret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((areaCd == null) ? 0 : areaCd.hashCode());
		result = prime * result
				+ ((countryCd == null) ? 0 : countryCd.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + (isDefault ? 1231 : 1237);
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result
				+ ((phoneExt == null) ? 0 : phoneExt.hashCode());
		result = prime * result
				+ ((phoneNbr == null) ? 0 : phoneNbr.hashCode());
		result = prime * result
				+ ((totpSecret == null) ? 0 : totpSecret.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhoneEntity other = (PhoneEntity) obj;
		if (areaCd == null) {
			if (other.areaCd != null)
				return false;
		} else if (!areaCd.equals(other.areaCd))
			return false;
		if (countryCd == null) {
			if (other.countryCd != null)
				return false;
		} else if (!countryCd.equals(other.countryCd))
			return false;
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
		if (isActive != other.isActive)
			return false;
		/*
		if (validated != other.validated)
			return false;
		*/
		if (isDefault != other.isDefault)
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (phoneExt == null) {
			if (other.phoneExt != null)
				return false;
		} else if (!phoneExt.equals(other.phoneExt))
			return false;
		if (phoneNbr == null) {
			if (other.phoneNbr != null)
				return false;
		} else if (!phoneNbr.equals(other.phoneNbr))
			return false;
		
		if (totpSecret == null) {
			if (other.totpSecret != null)
				return false;
		} else if (!totpSecret.equals(other.totpSecret))
            return false;
		return true;
	}

	@Override
	public String toString() {
		return "PhoneEntity [isActive=" + isActive + ", areaCd=" + areaCd
				+ ", countryCd=" + countryCd + ", description=" + description
				+ ", isDefault=" + isDefault + ", parent=" + parent
				+ ", phoneExt=" + phoneExt + ", phoneNbr=" + phoneNbr
				+ ", name=" + name + ", lastUpdate=" + lastUpdate
				+ ", createDate=" + createDate + "]";
	}

	
}
