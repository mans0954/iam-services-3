package org.openiam.idm.srvc.org.dto;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

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
        "metadataTypeId",
        "id",
        "organizationName",
        "organizationTypeId",
        "internalOrgId",
        "status",
        "abbreviation",
        "symbol",
        "selected",
        "operation",
        "parentOrganizations",
        "childOrganizations",
        "affiliations"
})
@DozerDTOCorrespondence(OrganizationEntity.class)
public class Organization implements java.io.Serializable, Comparable<Organization> {

    private static final long serialVersionUID = -6297113958697455428L;

    protected String id;

    protected String alias;

    @XmlJavaTypeAdapter(OrganizationAttributeMapAdapter.class)
    protected Map<String, OrganizationAttribute> attributes = new HashMap<String, OrganizationAttribute>(0);

    protected String createdBy;

    protected String description;

    protected String domainName;

    protected String ldapStr;

    @XmlSchemaType(name = "dateTime")
    protected Date lstUpdate;

    @XmlSchemaType(name = "dateTime")
    protected Date createDate;

    protected String lstUpdatedBy;

    protected String metadataTypeId;

    protected String organizationName;

    protected String internalOrgId;

    protected String status;
    
    protected String organizationTypeId;

    protected String abbreviation;

    protected String symbol;

    protected Boolean selected = Boolean.FALSE;

    protected AttributeOperationEnum operation;

    private Set<Organization> parentOrganizations;
    private Set<Organization> childOrganizations;
    private Set<UserAffiliation> affiliations;

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
    public Map<String, OrganizationAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     *
     * @param attributes allowed object is {@link org.openiam.idm.srvc.org.dto.OrganizationAttribute }
     */
    public void setAttributes(Map<String, OrganizationAttribute> attributes) {
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
     * Gets the value of the metadataType property.
     *
     * @return possible object is {@link String }
     */
    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    /**
     * Sets the value of the metadataType property.
     *
     * @param value allowed object is {@link String }
     */
    public void setMetadataTypeId(String value) {
        this.metadataTypeId = value;
    }
   
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
     * Gets the value of the organizationName property.
     *
     * @return possible object is {@link String }
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the value of the organizationName property.
     *
     * @param value allowed object is {@link String }
     */
    public void setOrganizationName(String value) {
        this.organizationName = value;
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
        if (getOrganizationName() == null || o == null) {
            return Integer.MIN_VALUE;
        }
        return getOrganizationName().compareTo(o.getOrganizationName());
    }

	public String getOrganizationTypeId() {
		return organizationTypeId;
	}

	public void setOrganizationTypeId(String organizationTypeId) {
		this.organizationTypeId = organizationTypeId;
	}

	public Set<UserAffiliation> getAffiliations() {
		return affiliations;
	}

	public void setAffiliations(Set<UserAffiliation> affiliations) {
		this.affiliations = affiliations;
	}
	
	public boolean isOrganization() {
		return StringUtils.equalsIgnoreCase("organization", organizationTypeId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((abbreviation == null) ? 0 : abbreviation.hashCode());
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result
				+ ((organizationTypeId == null) ? 0 : organizationTypeId.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((internalOrgId == null) ? 0 : internalOrgId.hashCode());
		result = prime * result + ((ldapStr == null) ? 0 : ldapStr.hashCode());
		result = prime * result
				+ ((lstUpdate == null) ? 0 : lstUpdate.hashCode());
		result = prime * result
				+ ((lstUpdatedBy == null) ? 0 : lstUpdatedBy.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime
				* result
				+ ((organizationName == null) ? 0 : organizationName.hashCode());
		result = prime * result
				+ ((selected == null) ? 0 : selected.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
		Organization other = (Organization) obj;
		if (abbreviation == null) {
			if (other.abbreviation != null)
				return false;
		} else if (!abbreviation.equals(other.abbreviation))
			return false;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		if (organizationTypeId == null) {
			if (other.organizationTypeId != null)
				return false;
		} else if (!organizationTypeId.equals(other.organizationTypeId))
			return false;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (domainName == null) {
			if (other.domainName != null)
				return false;
		} else if (!domainName.equals(other.domainName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (internalOrgId == null) {
			if (other.internalOrgId != null)
				return false;
		} else if (!internalOrgId.equals(other.internalOrgId))
			return false;
		if (ldapStr == null) {
			if (other.ldapStr != null)
				return false;
		} else if (!ldapStr.equals(other.ldapStr))
			return false;
		if (lstUpdate == null) {
			if (other.lstUpdate != null)
				return false;
		} else if (!lstUpdate.equals(other.lstUpdate))
			return false;
		if (lstUpdatedBy == null) {
			if (other.lstUpdatedBy != null)
				return false;
		} else if (!lstUpdatedBy.equals(other.lstUpdatedBy))
			return false;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		if (operation != other.operation)
			return false;
		if (organizationName == null) {
			if (other.organizationName != null)
				return false;
		} else if (!organizationName.equals(other.organizationName))
			return false;
		if (selected == null) {
			if (other.selected != null)
				return false;
		} else if (!selected.equals(other.selected))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

   
  
}
