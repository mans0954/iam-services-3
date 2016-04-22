package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.res.dto.ResourceType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
		name = "ResourceTypeSearchBean", 
		propOrder = { 
				"searchable", 
				"description", 
				"provisionResource", 
				"processName",
				"supportsHierarchy",
				"selectAll"
		}
)
public class ResourceTypeSearchBean extends AbstractSearchBean<ResourceType, String> implements
        SearchBean<ResourceType, String>, Serializable {

    private Boolean searchable;
    private String description;
    private Integer provisionResource;
    private String processName;
    private Boolean supportsHierarchy;
    private Boolean selectAll;

    public ResourceTypeSearchBean() {
    }

    public Boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getProvisionResource() {
        return provisionResource;
    }

    public void setProvisionResource(Integer provisionResource) {
        this.provisionResource = provisionResource;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

	public Boolean getSupportsHierarchy() {
		return supportsHierarchy;
	}

	public void setSupportsHierarchy(Boolean supportsHierarchy) {
		this.supportsHierarchy = supportsHierarchy;
	}

	public Boolean getSelectAll() {
		return selectAll;
	}

	public void setSelectAll(Boolean selectAll) {
		this.selectAll = selectAll;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((processName == null) ? 0 : processName.hashCode());
		result = prime
				* result
				+ ((provisionResource == null) ? 0 : provisionResource
						.hashCode());
		result = prime * result
				+ ((searchable == null) ? 0 : searchable.hashCode());
		result = prime * result
				+ ((selectAll == null) ? 0 : selectAll.hashCode());
		result = prime
				* result
				+ ((supportsHierarchy == null) ? 0 : supportsHierarchy
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
		ResourceTypeSearchBean other = (ResourceTypeSearchBean) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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
		if (searchable == null) {
			if (other.searchable != null)
				return false;
		} else if (!searchable.equals(other.searchable))
			return false;
		if (selectAll == null) {
			if (other.selectAll != null)
				return false;
		} else if (!selectAll.equals(other.selectAll))
			return false;
		if (supportsHierarchy == null) {
			if (other.supportsHierarchy != null)
				return false;
		} else if (!supportsHierarchy.equals(other.supportsHierarchy))
			return false;
		return true;
	}

	
}
