package org.openiam.idm.srvc.continfo.dto;

import java.util.Date;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

// Generated Jun 12, 2007 10:46:13 PM by Hibernate Tools 3.2.0.beta8

/**
 * Address transfer object.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "address", propOrder = {
        "isActive",
        "bldgNumber",
        "streetDirection",
        "suite",
        "address1",
        "address2",
        "address3",
        "address4",
        "address5",
        "address6",
        "address7",
        "addressId",
        "city",
        "country",
        "description",
        "isDefault",
        "parentId",
        "postalCd",
        "state",
        "name",
        "operation",
        "createDate",
        "lastUpdate",
        "metadataTypeId",
        "typeDescription",
        "locationId"
})
@DozerDTOCorrespondence(AddressEntity.class)
public class Address implements java.io.Serializable {


    // Fields
    protected String addressId;

    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    protected boolean isActive = true;

    protected String bldgNumber;

    protected String streetDirection;

    protected String suite;

    protected String address1;

    protected String address2;

    protected String address3;

    protected String address4;

    protected String address5;

    protected String address6;

    protected String address7;

    protected String city;

    protected String country;

    protected String description;

    protected boolean isDefault = false;

    protected String postalCd;

    protected String state;

    protected String name;

    protected String parentId;
    
    private Date lastUpdate;
    
    @XmlSchemaType(name = "dateTime")
    private Date createDate;
    // Constructors

    private String metadataTypeId;
    private String typeDescription;

    private String locationId;


    /**
     * default constructor
     */
    public Address() {
    }

    /**
     * minimal constructor
     */
    public Address(String addressId) {
        this.addressId = addressId;
    }
    
    
    public void updateAddress(Address adr) {
        this.address1 = adr.getAddress1();
        this.address2 = adr.getAddress2();
        this.address3 = adr.getAddress3();
        this.address4 = adr.getAddress4();
        this.address5 = adr.getAddress5();
        this.address6 = adr.getAddress6();
        this.address7 = adr.getAddress7();
        this.bldgNumber = adr.getBldgNumber();
        this.city = adr.getCity();
        this.country = adr.getCountry();
        this.description = adr.getDescription();
        this.isActive = adr.getIsActive();
        this.isDefault = adr.getIsDefault();
        this.name = adr.getName();
        this.postalCd = adr.getPostalCd();
        this.state = adr.getState();
        this.streetDirection = adr.getStreetDirection();
        this.suite = adr.getSuite();
        this.metadataTypeId = adr.getMetadataTypeId();
        this.locationId = adr.getLocationId();
    }

    // Property accessors
    public String getAddressId() {
        return this.addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress1() {
        return this.address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return this.address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCd() {
        return this.postalCd;
    }

    public void setPostalCd(String postalCd) {
        this.postalCd = postalCd;
    }

    public boolean getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the Id of the parent that owns this address. The parent may be another entity like a
     * USER, ORGANIZATION, etc
     *
     * @return
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Indicates if the address is currently active if the value is
     * true and inactive if the value false.
     *
     * @return
     */
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    /**
     * Associates the address with a parent entity, such as USER or ORGANIZATION that owns this address.
     *
     * @return
     */


    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
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
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result
				+ ((postalCd == null) ? 0 : postalCd.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result
				+ ((streetDirection == null) ? 0 : streetDirection.hashCode());
		result = prime * result + ((suite == null) ? 0 : suite.hashCode());

        result = prime * result + ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
        result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
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
		Address other = (Address) obj;
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
		if (operation != other.operation)
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
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
        if (metadataTypeId == null) {
            if (other.metadataTypeId != null)
                return false;
        } else if (!metadataTypeId.equals(other.metadataTypeId))
            return false;
        if (locationId == null) {
            if (other.locationId != null)
                return false;
        } else if (!locationId.equals(other.locationId))
            return false;
		return true;
	}

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Address");
        sb.append("{bldgNumber='").append(bldgNumber).append('\'');
        sb.append(", streetDirection='").append(streetDirection).append('\'');
        sb.append(", suite='").append(suite).append('\'');
        sb.append(", address1='").append(address1).append('\'');
        sb.append(", address2='").append(address2).append('\'');
        sb.append(", address3='").append(address3).append('\'');
        sb.append(", address4='").append(address4).append('\'');
        sb.append(", address5='").append(address5).append('\'');
        sb.append(", address6='").append(address6).append('\'');
        sb.append(", address7='").append(address7).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", country='").append(country).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", isDefault=").append(isDefault);
        sb.append(", postalCd='").append(postalCd).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", typeDescription='").append(typeDescription).append('\'');
        sb.append(", locationId='").append(locationId).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
