package org.openiam.idm.searchbeans;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.Tuple;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceRisk;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceSearchBean", propOrder = {
        "resourceTypeId",
        "rootsOnly",
        "attributes",
        "excludeResourceTypes",
        "risk",
        "URL",
        "metadataType",
        "coorelatedName",
		"referenceId",
		"ownerId"
})
public class ResourceSearchBean extends EntitlementsSearchBean<Resource, String> implements SearchBean<Resource, String>, Serializable {

	private static final long serialVersionUID = 1L;
    public static final String TYPE_MANAGED_SYS = "MANAGED_SYS";

	private String resourceTypeId;
	private Boolean rootsOnly;
	private List<Tuple<String, String>> attributes;
	private Set<String> excludeResourceTypes;
    private ResourceRisk risk;
    private String metadataType;
    private String URL;
	private String coorelatedName;
	private String referenceId;
	private String ownerId;

    public String getCoorelatedName() {
        return coorelatedName;
    }

    public void setCoorelatedName(String coorelatedName) {
        this.coorelatedName = coorelatedName;
    }

	public String getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	public Boolean getRootsOnly() {
		return rootsOnly;
	}

	public void setRootsOnly(Boolean rootsOnly) {
		this.rootsOnly = rootsOnly;
	}
	
	public void addAttribute(final String key, final String value) {
		if(StringUtils.isNotBlank(key) || StringUtils.isNotBlank(value)) {
			if(this.attributes == null) {
				this.attributes = new LinkedList<Tuple<String,String>>();
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

	public Set<String> getExcludeResourceTypes() {
		return excludeResourceTypes;
	}

	public void setExcludeResourceTypes(Set<String> excludeResourceTypes) {
		this.excludeResourceTypes = excludeResourceTypes;
	}
	
	public void addExcludeResourceType(final String excludeResourceType) {
		if(StringUtils.isNotBlank(excludeResourceType)) {
			if(this.excludeResourceTypes == null) {
				this.excludeResourceTypes = new HashSet<String>();
			}
			this.excludeResourceTypes.add(excludeResourceType);
		}
	}

    public ResourceRisk getRisk() {
        return risk;
    }

    public void setRisk(ResourceRisk risk) {
        this.risk = risk;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(String metadataType) {
        this.metadataType = metadataType;
    }

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((URL == null) ? 0 : URL.hashCode());
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime
				* result
				+ ((excludeResourceTypes == null) ? 0 : excludeResourceTypes
						.hashCode());
		result = prime * result
				+ ((resourceTypeId == null) ? 0 : resourceTypeId.hashCode());
		result = prime * result + ((risk == null) ? 0 : risk.hashCode());
		result = prime * result
				+ ((rootsOnly == null) ? 0 : rootsOnly.hashCode());
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
		ResourceSearchBean other = (ResourceSearchBean) obj;
		if (URL == null) {
			if (other.URL != null)
				return false;
		} else if (!URL.equals(other.URL))
			return false;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (excludeResourceTypes == null) {
			if (other.excludeResourceTypes != null)
				return false;
		} else if (!excludeResourceTypes.equals(other.excludeResourceTypes))
			return false;
		if (resourceTypeId == null) {
			if (other.resourceTypeId != null)
				return false;
		} else if (!resourceTypeId.equals(other.resourceTypeId))
			return false;
		if (risk != other.risk)
			return false;
		if (rootsOnly == null) {
			if (other.rootsOnly != null)
				return false;
		} else if (!rootsOnly.equals(other.rootsOnly))
			return false;
		return true;
	}
}
