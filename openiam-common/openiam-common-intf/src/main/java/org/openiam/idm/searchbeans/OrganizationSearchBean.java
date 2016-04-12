package org.openiam.idm.searchbeans;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.Tuple;
import org.openiam.idm.srvc.org.dto.Organization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 02.11.12
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationSearchBean", propOrder = {
        "keySet",
        "organizationTypeIdSet",
//		"userId",
//		"parentId",
//		"childId",
        "validParentTypeId",
        "internalOrgId",
        "attributes",
        "metadataType",
        "isSelectable",
        "abbreviation",
        "forCurrentUsersOnly",
        "domainName",
        "uncoverParents"
})
public class OrganizationSearchBean extends EntitlementsSearchBean<Organization, String> implements SearchBean<Organization, String> {
    private static final long serialVersionUID = 1L;
    private Set<String> keySet;
    private Set<String> organizationTypeIdSet;
    private String validParentTypeId;
    private String internalOrgId;
    private String metadataType;
    private List<Tuple<String, String>> attributes;
    private Boolean isSelectable = null;
    private String abbreviation;
    private boolean forCurrentUsersOnly = false;
    private String domainName;
    private Boolean uncoverParents = false;


    public String getOrganizationTypeId() {
        return (CollectionUtils.isNotEmpty(organizationTypeIdSet)) ? organizationTypeIdSet.iterator().next() : null;
    }

    public void setOrganizationTypeId(String organizationTypeId) {
        this.addOrganizationTypeId(organizationTypeId);
    }

    public void addOrganizationTypeId(final String organizationTypeId) {
        if (organizationTypeIdSet == null) {
            organizationTypeIdSet = new HashSet<String>();
        }
        organizationTypeIdSet.add(organizationTypeId);
    }

    public Set<String> getOrganizationTypeIdSet() {
        return organizationTypeIdSet;
    }

    public void setOrganizationTypeIdSet(final List<String> organizationTypeIdSet) {
        if (organizationTypeIdSet != null) {
            setKeys(new HashSet<String>(organizationTypeIdSet));
        }
    }

    public void setOrganizationTypeIdSet(final Set<String> organizationTypeIdSet) {
        this.organizationTypeIdSet = organizationTypeIdSet;
    }

    @Override
    public String getKey() {
        return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next() : null;
    }

    @Override
    public void setKey(final String key) {
        this.addKey(key);
    }

    public String getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(String metadataType) {
        this.metadataType = metadataType;
    }

    public Set<String> getKeys() {
        return keySet;
    }

    public void addKey(final String key) {
        if (this.keySet == null) {
            this.keySet = new HashSet<String>();
        }
        this.keySet.add(key);
    }

    public boolean hasMultipleKeys() {
        return (keySet != null && keySet.size() > 1);
    }

    public void setKeys(final Collection<String> keySet) {
        if (keySet != null) {
            setKeys(new HashSet<String>(keySet));
        }
    }
    public Boolean getUncoverParents() {
        return uncoverParents;
    }

        public void setUncoverParents(Boolean uncoverParents) {
        this.uncoverParents = uncoverParents;
    }

    public void setKeys(final Set<String> keySet) {
        this.keySet = keySet;
    }

    public Boolean getIsSelectable() {
        return isSelectable;
    }

    public void setIsSelectable(Boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    public String getValidParentTypeId() {
        return validParentTypeId;
    }

    public void setValidParentTypeId(String validParentTypeId) {
        this.validParentTypeId = validParentTypeId;
    }

    public String getInternalOrgId() {
        return internalOrgId;
    }

    public void setInternalOrgId(String internalOrgId) {
        this.internalOrgId = internalOrgId;
    }

    public void addAttribute(final String key, final String value) {
        if (StringUtils.isNotBlank(key) || StringUtils.isNotBlank(value)) {
            if (this.attributes == null) {
                this.attributes = new LinkedList<Tuple<String, String>>();
            }
            final Tuple<String, String> tuple = new Tuple<String, String>(key, value);
            this.attributes.add(tuple);
        }
    }

    public List<Tuple<String, String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Tuple<String, String>> attributes) {
        this.attributes = attributes;
    }

    public boolean isForCurrentUsersOnly() {
        return forCurrentUsersOnly;
    }

    public void setForCurrentUsersOnly(boolean forCurrentUserOnly) {
        this.forCurrentUsersOnly = forCurrentUserOnly;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((abbreviation == null) ? 0 : abbreviation.hashCode());
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
		result = prime * result + (forCurrentUsersOnly ? 1231 : 1237);
		result = prime * result
				+ ((internalOrgId == null) ? 0 : internalOrgId.hashCode());
		result = prime * result
				+ ((isSelectable == null) ? 0 : isSelectable.hashCode());
		result = prime * result + ((keySet == null) ? 0 : keySet.hashCode());
		result = prime * result
				+ ((metadataType == null) ? 0 : metadataType.hashCode());
		result = prime
				* result
				+ ((organizationTypeIdSet == null) ? 0 : organizationTypeIdSet
						.hashCode());
		result = prime * result
				+ ((uncoverParents == null) ? 0 : uncoverParents.hashCode());
		result = prime
				* result
				+ ((validParentTypeId == null) ? 0 : validParentTypeId
						.hashCode());
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
		OrganizationSearchBean other = (OrganizationSearchBean) obj;
		if (abbreviation == null) {
			if (other.abbreviation != null)
				return false;
		} else if (!abbreviation.equals(other.abbreviation))
			return false;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (domainName == null) {
			if (other.domainName != null)
				return false;
		} else if (!domainName.equals(other.domainName))
			return false;
		if (forCurrentUsersOnly != other.forCurrentUsersOnly)
			return false;
		if (internalOrgId == null) {
			if (other.internalOrgId != null)
				return false;
		} else if (!internalOrgId.equals(other.internalOrgId))
			return false;
		if (isSelectable == null) {
			if (other.isSelectable != null)
				return false;
		} else if (!isSelectable.equals(other.isSelectable))
			return false;
		if (keySet == null) {
			if (other.keySet != null)
				return false;
		} else if (!keySet.equals(other.keySet))
			return false;
		if (metadataType == null) {
			if (other.metadataType != null)
				return false;
		} else if (!metadataType.equals(other.metadataType))
			return false;
		if (organizationTypeIdSet == null) {
			if (other.organizationTypeIdSet != null)
				return false;
		} else if (!organizationTypeIdSet.equals(other.organizationTypeIdSet))
			return false;
		if (uncoverParents == null) {
			if (other.uncoverParents != null)
				return false;
		} else if (!uncoverParents.equals(other.uncoverParents))
			return false;
		if (validParentTypeId == null) {
			if (other.validParentTypeId != null)
				return false;
		} else if (!validParentTypeId.equals(other.validParentTypeId))
			return false;
		return true;
	}

    

}
