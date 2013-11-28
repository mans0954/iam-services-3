package org.openiam.idm.srvc.res.dto;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;

/**
 * ResourceType allows you to classify the resource.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceType", propOrder = {
        "id",
        "description",
        "metadataTypeId",
        "provisionResource",
        "processName",
        "supportsHierarchy",
        "searchable"
})
@DozerDTOCorrespondence(ResourceTypeEntity.class)
public class ResourceType implements java.io.Serializable {

    private String id;
    private String description;
    private String metadataTypeId;
    private Integer provisionResource;
    private String processName;
    private boolean supportsHierarchy;
    private boolean searchable = true;

    public ResourceType() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetadataTypeId() {
        return this.metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public Integer getProvisionResource() {
        return this.provisionResource;
    }

    public void setProvisionResource(Integer provisionResource) {
        this.provisionResource = provisionResource;
    }

    public String getProcessName() {
        return this.processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
    
    public boolean isSupportsHierarchy() {
		return supportsHierarchy;
	}

	public void setSupportsHierarchy(boolean supportsHierarchy) {
		this.supportsHierarchy = supportsHierarchy;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime * result
				+ ((processName == null) ? 0 : processName.hashCode());
		result = prime
				* result
				+ ((provisionResource == null) ? 0 : provisionResource
						.hashCode());
		result = prime * result + (searchable ? 1231 : 1237);
		result = prime * result + (supportsHierarchy ? 1231 : 1237);
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
		ResourceType other = (ResourceType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		if (processName == null) {
			if (other.processName != null)
				return false;
		} else if (!processName.equals(other.processName))
			return false;
		if (provisionResource == null) {
			if (other.provisionResource != null)
				return false;
		} else if (!provisionResource.equals(other.provisionResource))
			return false;
		if (searchable != other.searchable)
			return false;
		if (supportsHierarchy != other.supportsHierarchy)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("ResourceType [id=%s, description=%s, metadataTypeId=%s, provisionResource=%s, processName=%s, supportsHierarchy=%s, searchable=%s]",
						id, description, metadataTypeId, provisionResource,
						processName, supportsHierarchy, searchable);
	}

    
}
