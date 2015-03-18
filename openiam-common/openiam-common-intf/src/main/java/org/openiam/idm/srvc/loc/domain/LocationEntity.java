package org.openiam.idm.srvc.loc.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;
import org.openiam.core.dao.lucene.LuceneId;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.annotation.ElasticsearchIndex;
import org.openiam.elasticsearch.annotation.ElasticsearchMapping;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;


@Entity
@Table(name = "LOCATION")
@DozerDTOCorrespondence(Location.class)
@AttributeOverride(name = "id", column = @Column(name = "LOCATION_ID"))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ElasticsearchIndex(indexName = ESIndexName.LOCATION)
@ElasticsearchMapping(typeName = ESIndexType.LOCATION)
public class LocationEntity extends KeyEntity {

	@Column(name = "NAME", length = 100)
    @Size(max = 100, message = "validator.location.name.toolong")
    private String name;

    @Column(name = "DESCRIPTION", length = 255)
    @Size(max = 255, message = "validator.location.description.toolong")
    private String description;

    @Column(name = "COUNTRY", length = 100)
    @Size(max = 100, message = "validator.location.country.toolong")
    private String country;

    @Column(name = "BLDG_NUM", length = 100)
    @Size(max = 100, message = "validator.location.building.number.toolong")
    private String bldgNum;

    @Column(name = "STREET_DIRECTION", length = 20)
    @Size(max = 20, message = "validator.location.street.direction.toolong")
    private String streetDirection;

    @Column(name = "ADDRESS1", length = 400)
    @Size(max = 400, message = "validator.location.address1.toolong")
    private String address1;

    @Column(name = "ADDRESS2", length = 400)
    @Size(max = 400, message = "validator.location.address2.toolong")
    private String address2;

    @Column(name = "ADDRESS3", length = 400)
    @Size(max = 400, message = "validator.location.address3.toolong")
    private String address3;

    @Column(name = "CITY", length = 100)
    @Size(max = 100, message = "validator.location.city.toolong")
    private String city;

    @Column(name = "STATE", length = 100)
    @Size(max = 100, message = "validator.location.state.toolong")
    private String state;

    @Column(name = "POSTAL_CD", length = 100)
    @Size(max = 100, message = "validator.location.postal.code.toolong")
    private String postalCd;

    @Column(name="INTERNAL_LOCATION_ID")
    private String internalLocationId;

    @Column(name = "ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;

    @Column(name = "SENSITIVE_LOCATION")
    private Integer sensitiveLocation;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", referencedColumnName="COMPANY_ID", insertable=true, updatable=false, nullable=true)
    private OrganizationEntity organization;



    public LocationEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getInternalLocationId() {
        return internalLocationId;
    }

    public void setInternalLocationId(String internalLocationId) {
        this.internalLocationId = internalLocationId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public Integer getSensitiveLocation() {
        return sensitiveLocation;
    }

    public void setSensitiveLocation(Integer sensitiveLocation) {
        this.sensitiveLocation = sensitiveLocation;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((organization == null) ? 0 : organization.hashCode());
		result = prime * result
				+ ((postalCd == null) ? 0 : postalCd.hashCode());
		result = prime
				* result
				+ ((sensitiveLocation == null) ? 0 : sensitiveLocation
						.hashCode());
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
		LocationEntity other = (LocationEntity) obj;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (postalCd == null) {
			if (other.postalCd != null)
				return false;
		} else if (!postalCd.equals(other.postalCd))
			return false;
		if (sensitiveLocation == null) {
			if (other.sensitiveLocation != null)
				return false;
		} else if (!sensitiveLocation.equals(other.sensitiveLocation))
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