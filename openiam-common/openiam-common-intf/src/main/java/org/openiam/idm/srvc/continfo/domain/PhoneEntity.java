package org.openiam.idm.srvc.continfo.domain;

import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.openiam.core.dao.lucene.LuceneId;
import org.openiam.core.dao.lucene.LuceneLastUpdate;
import org.openiam.core.dao.lucene.bridge.UserBridge;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

@Entity
@Table(name = "PHONE")
@DozerDTOCorrespondence(Phone.class)
@Indexed
public class PhoneEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "PHONE_ID", length = 32, nullable = false)
    @LuceneId
    @DocumentId
    private String phoneId;

    @Column(name="ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;

    @Fields ({
        @Field(index = Index.UN_TOKENIZED),
        @Field(name = "areaCd", index = Index.UN_TOKENIZED, store = Store.YES)
    })
    @Column(name="AREA_CD", length=10)
    private String areaCd;

    @Column(name="COUNTRY_CD", length=3)
    private String countryCd;

    @Column(name="DESCRIPTION", length=100)
    private String description;

    @Column(name="IS_DEFAULT")
    @Type(type = "yes_no")
    private boolean isDefault = false;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    @Field(name="parent", bridge=@FieldBridge(impl=UserBridge.class), store=Store.YES)
    private UserEntity parent;

    @Column(name="PHONE_EXT", length=20)
    private String phoneExt;

    @Fields ({
        @Field(index = Index.TOKENIZED),
        @Field(name = "phoneNbr", index = Index.TOKENIZED, store = Store.YES)
    })
    @Column(name="PHONE_NBR", length=50)
    private String phoneNbr;

    @Column(name="NAME", length=40)
    private String name;

    @Column(name="PHONE_TYPE", length=20)
    private String phoneType;
    
    @Column(name = "LAST_UPDATE", length = 19)
    @LuceneLastUpdate
    private Date lastUpdate;
    
    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    private MetadataTypeEntity metadataType;

    public PhoneEntity() {
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result
				+ ((phoneExt == null) ? 0 : phoneExt.hashCode());
		result = prime * result + ((phoneId == null) ? 0 : phoneId.hashCode());
		result = prime * result
				+ ((phoneNbr == null) ? 0 : phoneNbr.hashCode());
		result = prime * result
				+ ((phoneType == null) ? 0 : phoneType.hashCode());
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
		if (phoneId == null) {
			if (other.phoneId != null)
				return false;
		} else if (!phoneId.equals(other.phoneId))
			return false;
		if (phoneNbr == null) {
			if (other.phoneNbr != null)
				return false;
		} else if (!phoneNbr.equals(other.phoneNbr))
			return false;
		if (phoneType == null) {
			if (other.phoneType != null)
				return false;
		} else if (!phoneType.equals(other.phoneType))
			return false;
        if (metadataType == null) {
            if (other.metadataType != null)
                return false;
        } else if (!metadataType.equals(other.metadataType))
            return false;
		return true;
	}

	
}
