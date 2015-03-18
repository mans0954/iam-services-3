/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.loc.dto;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.loc.domain.LocationEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;

/**
 * Domain object representing a location
 *
 * @author Suneet Shah
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "location", propOrder = {
        "description",
        "country",
        "bldgNum",
        "streetDirection",
        "address1",
        "address2",
        "address3",
        "city",
        "state",
        "postalCd",
        "organizationId",
        "internalLocationId",
        "isActive"
})
@DozerDTOCorrespondence(LocationEntity.class)
public class Location extends KeyNameDTO {

    /**
     *
     */
    protected static final long serialVersionUID = -1982532513415681152L;   
    protected String description;
    protected String country;
    protected String bldgNum;
    protected String streetDirection;
    protected String address1;
    protected String address2;
    protected String address3;
    protected String city;
    protected String state;
    protected String postalCd;
    protected String organizationId;
    protected boolean isActive;
    protected String internalLocationId;


    public Location() {

    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBldgNum() {
        return bldgNum;
    }

    public void setBldgNum(String bldgNum) {
        this.bldgNum = bldgNum;
    }

    public String getStreetDirection() {
        return streetDirection;
    }

    public void setStreetDirection(String streetDirection) {
        this.streetDirection = streetDirection;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCd() {
        return postalCd;
    }

    public void setPostalCd(String postalCd) {
        this.postalCd = postalCd;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInternalLocationId() {
        return internalLocationId;
    }

    public void setInternalLocationId(String internalLocationId) {
        this.internalLocationId = internalLocationId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((address1 == null) ? 0 : address1.hashCode());
		result = prime * result
				+ ((address2 == null) ? 0 : address2.hashCode());
		result = prime * result
				+ ((address3 == null) ? 0 : address3.hashCode());
		result = prime * result + ((bldgNum == null) ? 0 : bldgNum.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime
				* result
				+ ((internalLocationId == null) ? 0 : internalLocationId
						.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result
				+ ((organizationId == null) ? 0 : organizationId.hashCode());
		result = prime * result
				+ ((postalCd == null) ? 0 : postalCd.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result
				+ ((streetDirection == null) ? 0 : streetDirection.hashCode());
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
		Location other = (Location) obj;
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
		if (bldgNum == null) {
			if (other.bldgNum != null)
				return false;
		} else if (!bldgNum.equals(other.bldgNum))
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
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (internalLocationId == null) {
			if (other.internalLocationId != null)
				return false;
		} else if (!internalLocationId.equals(other.internalLocationId))
			return false;
		if (isActive != other.isActive)
			return false;
		if (organizationId == null) {
			if (other.organizationId != null)
				return false;
		} else if (!organizationId.equals(other.organizationId))
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
		return true;
	}

    
}
