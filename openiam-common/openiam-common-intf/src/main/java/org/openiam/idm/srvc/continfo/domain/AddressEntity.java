package org.openiam.idm.srvc.continfo.domain;

import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.core.dao.lucene.LuceneId;
import org.openiam.core.dao.lucene.LuceneLastUpdate;
import org.openiam.core.dao.lucene.bridge.UserBridge;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

@Entity
@Table(name = "ADDRESS")
@DozerDTOCorrespondence(Address.class)
@Indexed
public class AddressEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ADDRESS_ID", length = 32, nullable = false)
    @LuceneId
    @DocumentId
    private String addressId;

    @Column(name = "ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;
    @Column(name="IS_DEFAULT")
    @Type(type = "yes_no")
    private boolean isDefault = false;

    @Column(name = "BLDG_NUM", length = 100)
    private String bldgNumber;

    @Column(name = "STREET_DIRECTION", length = 20)
    private String streetDirection;

    @Column(name = "SUITE", length = 20)
    private String suite;

    @Column(name = "ADDRESS1", length = 45)
    private String address1;

    @Column(name = "ADDRESS2", length = 45)
    private String address2;

    @Column(name = "ADDRESS3", length = 45)
    private String address3;

    @Column(name = "ADDRESS4", length = 45)
    private String address4;

    @Column(name = "ADDRESS5", length = 45)
    private String address5;

    @Column(name = "ADDRESS6", length = 45)
    private String address6;

    @Column(name = "ADDRESS7", length = 45)
    private String address7;

    @Column(name = "CITY", length = 100)
    private String city;

    @Column(name = "COUNTRY", length = 100)
    private String country;

    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    @Field(name="parent", bridge=@FieldBridge(impl=UserBridge.class), store=Store.YES)
    private UserEntity parent;

    @Column(name = "POSTAL_CD", length = 100)
    private String postalCd;

    @Column(name = "STATE", length = 100)
    private String state;

    @Column(name = "NAME", length = 100)
    private String name;
    
    @Column(name = "LAST_UPDATE", length = 19)
    @LuceneLastUpdate
    private Date lastUpdate;
    
    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    private MetadataTypeEntity metadataType;

    public AddressEntity() {
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getBldgNumber() {
        return bldgNumber;
    }

    public void setBldgNumber(String bldgNumber) {
        this.bldgNumber = bldgNumber;
    }

    public String getStreetDirection() {
        return streetDirection;
    }

    public void setStreetDirection(String streetDirection) {
        this.streetDirection = streetDirection;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    public String getAddress5() {
        return address5;
    }

    public void setAddress5(String address5) {
        this.address5 = address5;
    }

    public String getAddress6() {
        return address6;
    }

    public void setAddress6(String address6) {
        this.address6 = address6;
    }

    public String getAddress7() {
        return address7;
    }

    public void setAddress7(String address7) {
        this.address7 = address7;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getParent() {
        return parent;
    }

    public void setParent(UserEntity parent) {
        this.parent = parent;
    }

    public String getPostalCd() {
        return postalCd;
    }

    public void setPostalCd(String postalCd) {
        this.postalCd = postalCd;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
				+ ((address1 == null) ? 0 : address1.hashCode());
		result = prime * result
				+ ((address2 == null) ? 0 : address2.hashCode());
		result = prime * result
				+ ((address3 == null) ? 0 : address3.hashCode());
		result = prime * result
				+ ((address4 == null) ? 0 : address4.hashCode());
		result = prime * result
				+ ((address5 == null) ? 0 : address5.hashCode());
		result = prime * result
				+ ((address6 == null) ? 0 : address6.hashCode());
		result = prime * result
				+ ((address7 == null) ? 0 : address7.hashCode());
		result = prime * result
				+ ((addressId == null) ? 0 : addressId.hashCode());
		result = prime * result
				+ ((bldgNumber == null) ? 0 : bldgNumber.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
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
				+ ((postalCd == null) ? 0 : postalCd.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result
				+ ((streetDirection == null) ? 0 : streetDirection.hashCode());
		result = prime * result + ((suite == null) ? 0 : suite.hashCode());
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
		AddressEntity other = (AddressEntity) obj;
		if (address1 == null) {
			if (other.address1 != null)
				return false;
		} else if (!address1.equals(other.address1))
			return false;
		if (address2 == null) {
			if (other.address2 != null)
				return false;
		} else if (!address2.equals(other.address2))
			return false;
		if (address3 == null) {
			if (other.address3 != null)
				return false;
		} else if (!address3.equals(other.address3))
			return false;
		if (address4 == null) {
			if (other.address4 != null)
				return false;
		} else if (!address4.equals(other.address4))
			return false;
		if (address5 == null) {
			if (other.address5 != null)
				return false;
		} else if (!address5.equals(other.address5))
			return false;
		if (address6 == null) {
			if (other.address6 != null)
				return false;
		} else if (!address6.equals(other.address6))
			return false;
		if (address7 == null) {
			if (other.address7 != null)
				return false;
		} else if (!address7.equals(other.address7))
			return false;
		if (addressId == null) {
			if (other.addressId != null)
				return false;
		} else if (!addressId.equals(other.addressId))
			return false;
		if (bldgNumber == null) {
			if (other.bldgNumber != null)
				return false;
		} else if (!bldgNumber.equals(other.bldgNumber))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
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
		if (postalCd == null) {
			if (other.postalCd != null)
				return false;
		} else if (!postalCd.equals(other.postalCd))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (streetDirection == null) {
			if (other.streetDirection != null)
				return false;
		} else if (!streetDirection.equals(other.streetDirection))
			return false;
		if (suite == null) {
			if (other.suite != null)
				return false;
		} else if (!suite.equals(other.suite))
			return false;
        if (metadataType == null) {
            if (other.metadataType != null)
                return false;
        } else if (!metadataType.equals(other.metadataType))
            return false;
		return true;
	}

   
}
