package org.openiam.idm.srvc.continfo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;
import org.openiam.core.dao.lucene.LuceneLastUpdate;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.annotation.ElasticsearchField;
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.annotation.ElasticsearchIndex;
import org.openiam.elasticsearch.annotation.ElasticsearchMapping;
import org.openiam.elasticsearch.bridge.UserBrigde;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.constants.ElasticsearchStore;
import org.openiam.elasticsearch.constants.Index;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

@Entity
@Table(name = "PHONE")
@DozerDTOCorrespondence(Phone.class)
//@Indexed
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ElasticsearchIndex(indexName = ESIndexName.USERS)
@ElasticsearchMapping(typeName = ESIndexType.PHONE, parent = ESIndexType.USER)
@AttributeOverride(name = "id", column = @Column(name = "PHONE_ID"))
public class PhoneEntity extends KeyEntity {
    @Column(name="ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;

//    @Fields ({
//        @Field(analyze = Analyze.NO),
//        @Field(name = "areaCd", analyze = Analyze.NO, store = Store.YES)
//    })
    @ElasticsearchField(name = "areaCd", store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
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

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    @ElasticsearchField(name = "userId", bridge=@ElasticsearchFieldBridge(impl = UserBrigde.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed, mapToParent=true)
//    @Field(name="parent", bridge=@FieldBridge(impl=UserBridge.class), store=Store.YES)
    private UserEntity parent;

    @Column(name="PHONE_EXT", length=20)
    @Size(max = 20, message = "validator.phone.extension.toolong")
    @ElasticsearchField(name = "phoneExt", store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    private String phoneExt;

//    @Fields ({
//        @Field(analyze = Analyze.NO),
//        @Field(name = "phoneNbr", analyze = Analyze.NO, store = Store.YES)
//    })
    @ElasticsearchField(name = "phoneNbr", store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    @Column(name="PHONE_NBR", length=50)
    @Size(max = 50, message = "validator.phone.number.toolong")
    private String phoneNbr;

    @Column(name="NAME", length=40)
    @Size(max = 50, message = "validator.phone.label.toolong")
    private String name;

    /*
    @Column(name="PHONE_TYPE", length=20)
    private String phoneType;
    */
    
    @Column(name = "LAST_UPDATE", length = 19)
    @LuceneLastUpdate
    private Date lastUpdate;
    
    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID", insertable=true, updatable=true)
    private MetadataTypeEntity metadataType;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public MetadataTypeEntity getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(MetadataTypeEntity metadataType) {
        this.metadataType = metadataType;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((phoneExt == null) ? 0 : phoneExt.hashCode());
		result = prime * result
				+ ((phoneNbr == null) ? 0 : phoneNbr.hashCode());
		/*
		result = prime * result
				+ ((phoneType == null) ? 0 : phoneType.hashCode());
		*/
        result = prime * result + ((metadataType == null) ? 0 : metadataType.hashCode());
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
		/*
		if (phoneType == null) {
			if (other.phoneType != null)
				return false;
		} else if (!phoneType.equals(other.phoneType))
			return false;
		*/
        if (metadataType == null) {
            if (other.metadataType != null)
                return false;
        } else if (!metadataType.equals(other.metadataType))
            return false;
		return true;
	}

    @Override
    public String toString() {
        return "PhoneEntity{" +
               "isActive=" + isActive +
               ", areaCd='" + areaCd + '\'' +
               ", countryCd='" + countryCd + '\'' +
               ", description='" + description + '\'' +
               ", isDefault=" + isDefault +
               ", parent=" + parent +
               ", phoneExt='" + phoneExt + '\'' +
               ", phoneNbr='" + phoneNbr + '\'' +
               ", name='" + name + '\'' +
               ", lastUpdate=" + lastUpdate +
               ", createDate=" + createDate +
               ", metadataType=" + metadataType +
               ", " + super.toString()+"}";
    }
}
