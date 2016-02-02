package org.openiam.idm.srvc.loc.domain;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;
import org.openiam.core.dao.lucene.LuceneId;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Entity
@Table(name = "LOCATION")
@DozerDTOCorrespondence(Location.class)
@Indexed
public class LocationEntity {


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "LOCATION_ID", length = 32, nullable = false)
    @LuceneId
    @DocumentId
    private String locationId;

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

    @Column(name="ORGANIZATION_ID")
    private String organizationId;

    @Column(name="INTERNAL_LOCATION_ID")
    private String internalLocationId;

    @Column(name = "ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;

    @Column(name = "SENSITIVE_LOCATION")
    private Integer sensitiveLocation;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID", insertable=false, updatable=false)
    private OrganizationEntity organization;



    public LocationEntity() {
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
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

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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
    public String toString() {
        final StringBuilder sb = new StringBuilder("LocationEntity{");
        sb.append("locationId='").append(locationId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", country='").append(country).append('\'');
        sb.append(", bldgNum='").append(bldgNum).append('\'');
        sb.append(", streetDirection='").append(streetDirection).append('\'');
        sb.append(", address1='").append(address1).append('\'');
        sb.append(", address2='").append(address2).append('\'');
        sb.append(", address3='").append(address3).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", postalCd='").append(postalCd).append('\'');
        sb.append(", organizationId='").append(organizationId).append('\'');
        sb.append(", internalLocationId='").append(internalLocationId).append('\'');
        sb.append(", getActive=").append(isActive);
        sb.append(", sensitiveLocation=").append(sensitiveLocation);
        sb.append(", organization=").append(organization);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationEntity that = (LocationEntity) o;

        if (isActive != that.isActive) return false;
        if (address1 != null ? !address1.equals(that.address1) : that.address1 != null) return false;
        if (address2 != null ? !address2.equals(that.address2) : that.address2 != null) return false;
        if (address3 != null ? !address3.equals(that.address3) : that.address3 != null) return false;
        if (bldgNum != null ? !bldgNum.equals(that.bldgNum) : that.bldgNum != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (internalLocationId != null ? !internalLocationId.equals(that.internalLocationId) : that.internalLocationId != null)
            return false;
        if (!locationId.equals(that.locationId)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (postalCd != null ? !postalCd.equals(that.postalCd) : that.postalCd != null) return false;
        if (sensitiveLocation != null ? !sensitiveLocation.equals(that.sensitiveLocation) : that.sensitiveLocation != null)
            return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (streetDirection != null ? !streetDirection.equals(that.streetDirection) : that.streetDirection != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locationId.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (bldgNum != null ? bldgNum.hashCode() : 0);
        result = 31 * result + (streetDirection != null ? streetDirection.hashCode() : 0);
        result = 31 * result + (address1 != null ? address1.hashCode() : 0);
        result = 31 * result + (address2 != null ? address2.hashCode() : 0);
        result = 31 * result + (address3 != null ? address3.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (postalCd != null ? postalCd.hashCode() : 0);
        result = 31 * result + (internalLocationId != null ? internalLocationId.hashCode() : 0);
        result = 31 * result + (isActive ? 1 : 0);
        result = 31 * result + (sensitiveLocation != null ? sensitiveLocation.hashCode() : 0);
        return result;
    }
}