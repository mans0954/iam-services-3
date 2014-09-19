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
				"supportsHierarchy"
		}
)
public class ResourceTypeSearchBean extends AbstractSearchBean<ResourceType, String> implements
        SearchBean<ResourceType, String>, Serializable {

    private Boolean searchable;
    private String description;
    private Integer provisionResource;
    private String processName;
    private Boolean supportsHierarchy;

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
    
    
}
