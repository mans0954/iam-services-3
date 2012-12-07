package org.openiam.idm.srvc.org.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.dto.OrgClassificationEnum;
import org.openiam.idm.srvc.org.dto.Organization;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.DocumentId;
import org.openiam.idm.srvc.org.dto.OrgClassificationEnum;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;

@Entity
@Table(name = "COMPANY")
@DozerDTOCorrespondence(Organization.class)
public class OrganizationEntity {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="COMPANY_ID", length=32, nullable = false)
    @DocumentId
    private String orgId;

    @Column(name="ALIAS", length=100)
    private String alias;

    @OneToMany(orphanRemoval=true, cascade = CascadeType.ALL, mappedBy = "organization", fetch = FetchType.LAZY)
    @MapKey(name = "name")
    @Fetch(FetchMode.SUBSELECT)
    private Map<String, OrganizationAttributeEntity> attributes = new HashMap<String, OrganizationAttributeEntity>(0);

    @Column(name="CREATE_DATE", length=19)
    private Date createDate;

    @Column(name="CREATED_BY", length=20)
    private String createdBy;

    @Column(name="DESCRIPTION", length=100)
    private String description;

    @Column(name="DOMAIN_NAME", length=40)
    private String domainName;

    @Column(name="LDAP_STR")
    private String ldapStr;

    @Column(name="LST_UPDATE", length=19)
    private Date lstUpdate;

    @Column(name="LST_UPDATED_BY", length=20)
    private String lstUpdatedBy;

    @Column(name="TYPE_ID", length=20)
    private String metadataTypeId;

    @Column(name="COMPANY_NAME", length=200)
    private String organizationName;

    @Column(name="INTERNAL_COMPANY_ID")
    private String internalOrgId;

    @Column(name="PARENT_ID", length=32)
    private String parentId;

    @Column(name="STATUS", length=20)
    private String status;

    @Column(name="CLASSIFICATION", length=40)
    @Enumerated(EnumType.STRING)
    private OrgClassificationEnum classification;

    @Column(name="ABBREVIATION", length=20)
    private String abbreviation;

    @Column(name="SYMBOL", length=10)
    private String symbol;

    public OrganizationEntity() {
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Map<String, OrganizationAttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, OrganizationAttributeEntity> attributes) {
        this.attributes = attributes;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getLdapStr() {
        return ldapStr;
    }

    public void setLdapStr(String ldapStr) {
        this.ldapStr = ldapStr;
    }

    public Date getLstUpdate() {
        return lstUpdate;
    }

    public void setLstUpdate(Date lstUpdate) {
        this.lstUpdate = lstUpdate;
    }

    public String getLstUpdatedBy() {
        return lstUpdatedBy;
    }

    public void setLstUpdatedBy(String lstUpdatedBy) {
        this.lstUpdatedBy = lstUpdatedBy;
    }

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getInternalOrgId() {
        return internalOrgId;
    }

    public void setInternalOrgId(String internalOrgId) {
        this.internalOrgId = internalOrgId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrgClassificationEnum getClassification() {
        return classification;
    }

    public void setClassification(OrgClassificationEnum classification) {
        this.classification = classification;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((abbreviation == null) ? 0 : abbreviation.hashCode());
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result
				+ ((classification == null) ? 0 : classification.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
		result = prime * result
				+ ((internalOrgId == null) ? 0 : internalOrgId.hashCode());
		result = prime * result + ((ldapStr == null) ? 0 : ldapStr.hashCode());
		result = prime * result
				+ ((lstUpdate == null) ? 0 : lstUpdate.hashCode());
		result = prime * result
				+ ((lstUpdatedBy == null) ? 0 : lstUpdatedBy.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime * result + ((orgId == null) ? 0 : orgId.hashCode());
		result = prime
				* result
				+ ((organizationName == null) ? 0 : organizationName.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
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
		OrganizationEntity other = (OrganizationEntity) obj;
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
		if (classification != other.classification)
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
		if (orgId == null) {
			if (other.orgId != null)
				return false;
		} else if (!orgId.equals(other.orgId))
			return false;
		if (organizationName == null) {
			if (other.organizationName != null)
				return false;
		} else if (!organizationName.equals(other.organizationName))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
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
