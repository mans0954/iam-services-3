package org.openiam.idm.srvc.org.dto;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.AdminResourceDTO;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.user.dto.User;

/**
 * <p/>
 * Java class for organization complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organization", propOrder = {
        "alias",
        "attributes",
        "createDate",
        "createdBy",
        "description",
        "domainName",
        "ldapStr",
        "lstUpdate",
        "lstUpdatedBy",
        "organizationTypeId",
        "organizationTypeName",
        "internalOrgId",
        "status",
        "abbreviation",
        "symbol",
        "selected",
        "operation",
        "parentOrganizations",
        "childOrganizations"
})
@DozerDTOCorrespondence(OrganizationEntity.class)
public class Organization extends AdminResourceDTO implements Serializable, Comparable<Organization> {

    private static final long serialVersionUID = -6297113958697455428L;


    protected String alias;

    protected Set<OrganizationAttribute> attributes = new HashSet<OrganizationAttribute>();

    protected String createdBy;

    protected String description;

    protected String domainName;

    protected String ldapStr;

    @XmlSchemaType(name = "dateTime")
    protected Date lstUpdate;

    @XmlSchemaType(name = "dateTime")
    protected Date createDate;

    protected String lstUpdatedBy;

    protected String internalOrgId;

    protected String status;
    
    protected String organizationTypeId;
    
    private String organizationTypeName;

    protected String abbreviation;

    protected String symbol;

    protected Boolean selected = Boolean.FALSE;

    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    private Set<Organization> parentOrganizations;
    private Set<Organization> childOrganizations;
    
    /**
     * default constructor
     */
    public Organization() {
    }

    /**
     * Gets the value of the alias property.
     *
     * @return possible object is {@link String }
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the value of the alias property.
     *
     * @param value allowed object is {@link String }
     */
    public void setAlias(String value) {
        this.alias = value;
    }

    /**
     * Gets the value of the attributes property.
     *
     * @return possible object is {@link org.openiam.idm.srvc.org.dto.OrganizationAttribute }
     */
    public Set<OrganizationAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     *
     * @param attributes allowed object is {@link org.openiam.idm.srvc.org.dto.OrganizationAttribute }
     */
    public void setAttributes(Set<OrganizationAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Gets the value of the createDate property.
     *
     * @return possible object is {@link String }
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets the value of the createDate property.
     *
     * @param value allowed object is {@link String }
     */
    public void setCreateDate(Date value) {
        this.createDate = value;
    }

    /**
     * Gets the value of the createdBy property.
     *
     * @return possible object is {@link String }
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     *
     * @param value allowed object is {@link String }
     */
    public void setCreatedBy(String value) {
        this.createdBy = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is {@link String }
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the domainName property.
     *
     * @return possible object is {@link String }
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * Sets the value of the domainName property.
     *
     * @param value allowed object is {@link String }
     */
    public void setDomainName(String value) {
        this.domainName = value;
    }

    /**
     * Gets the value of the ldapStr property.
     *
     * @return possible object is {@link String }
     */
    public String getLdapStr() {
        return ldapStr;
    }

    /**
     * Sets the value of the ldapStr property.
     *
     * @param value allowed object is {@link String }
     */
    public void setLdapStr(String value) {
        this.ldapStr = value;
    }

    /**
     * Gets the value of the lstUpdate property.
     *
     * @return possible object is {@link String }
     */
    public Date getLstUpdate() {
        return lstUpdate;
    }

    /**
     * Sets the value of the lstUpdate property.
     *
     * @param value allowed object is {@link String }
     */
    public void setLstUpdate(Date value) {
        this.lstUpdate = value;
    }

    /**
     * Gets the value of the lstUpdatedBy property.
     *
     * @return possible object is {@link String }
     */
    public String getLstUpdatedBy() {
        return lstUpdatedBy;
    }

    /**
     * Sets the value of the lstUpdatedBy property.
     *
     * @param value allowed object is {@link String }
     */
    public void setLstUpdatedBy(String value) {
        this.lstUpdatedBy = value;
    }

	/**
     * Gets the value of the status property.
     *
     * @return possible object is {@link String }
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value allowed object is {@link String }
     */
    public void setStatus(String value) {
        this.status = value;
    }

    public String getInternalOrgId() {
        return internalOrgId;
    }

    public void setInternalOrgId(String internalOrgId) {
        this.internalOrgId = internalOrgId;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

	public Set<Organization> getParentOrganizations() {
		return parentOrganizations;
	}

	public void setParentOrganizations(Set<Organization> parentOrganizations) {
		this.parentOrganizations = parentOrganizations;
	}

	public Set<Organization> getChildOrganizations() {
		return childOrganizations;
	}

	public void setChildOrganizations(Set<Organization> childOrganizations) {
		this.childOrganizations = childOrganizations;
	}

	public int compareTo(Organization o) {
        if (getName() == null || o == null) {
            return Integer.MIN_VALUE;
        }
        return getName().compareTo(o.getName());
    }

	public String getOrganizationTypeId() {
		return organizationTypeId;
	}

	public void setOrganizationTypeId(String organizationTypeId) {
		this.organizationTypeId = organizationTypeId;
	}

	public boolean isOrganization() {
		return StringUtils.equalsIgnoreCase("organization", organizationTypeId);
	}

	public String getOrganizationTypeName() {
		return organizationTypeName;
	}

	public void setOrganizationTypeName(String organizationTypeName) {
		this.organizationTypeName = organizationTypeName;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Organization that = (Organization) o;

        if (alias != null ? !alias.equals(that.alias) : that.alias != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (domainName != null ? !domainName.equals(that.domainName) : that.domainName != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (domainName != null ? domainName.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
