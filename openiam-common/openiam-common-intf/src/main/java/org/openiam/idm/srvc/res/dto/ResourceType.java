package org.openiam.idm.srvc.res.dto;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

/**
 * ResourceType allows you to classify the resource.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceType", 
	propOrder = {
		"description", 
		"provisionResource", 
		"processName",
		"supportsHierarchy", 
		"searchable", 
		"url", 
		"imageType",
		"displayNameMap",
		"displayName"
})
@DozerDTOCorrespondence(ResourceTypeEntity.class)
@Internationalized
public class ResourceType extends KeyDTO {

    private String description;
    private boolean provisionResource = true;
    private String processName;
    private boolean supportsHierarchy;
    private boolean searchable = true;
    private String url;
    private String imageType;
    
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMapping> displayNameMap;
	    
    private String displayName;

    public ResourceType() {
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isProvisionResource() {
		return provisionResource;
	}

	public void setProvisionResource(boolean provisionResource) {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, LanguageMapping> getDisplayNameMap() {
		return displayNameMap;
	}

	public void setDisplayNameMap(Map<String, LanguageMapping> displayNameMap) {
		this.displayNameMap = displayNameMap;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((imageType == null) ? 0 : imageType.hashCode());
		result = prime * result
				+ ((processName == null) ? 0 : processName.hashCode());
		result = prime * result + (provisionResource ? 1231 : 1237);
		result = prime * result + (searchable ? 1231 : 1237);
		result = prime * result + (supportsHierarchy ? 1231 : 1237);
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		ResourceType other = (ResourceType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (imageType == null) {
			if (other.imageType != null)
				return false;
		} else if (!imageType.equals(other.imageType))
			return false;
		if (processName == null) {
			if (other.processName != null)
				return false;
		} else if (!processName.equals(other.processName))
			return false;
		if (provisionResource != other.provisionResource)
			return false;
		if (searchable != other.searchable)
			return false;
		if (supportsHierarchy != other.supportsHierarchy)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	
}
