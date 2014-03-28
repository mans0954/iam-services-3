package org.openiam.idm.srvc.res.domain;

import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

@Entity
@Table(name = "RESOURCE_TYPE")
@DozerDTOCorrespondence(ResourceType.class)
@AttributeOverride(name = "id", column = @Column(name = "RESOURCE_TYPE_ID"))
@Internationalized
public class ResourceTypeEntity extends KeyEntity {

    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    @Column(name = "PROVISION_RESOURCE")
    private Integer provisionResource;

    @Column(name = "PROCESS_NAME", length = 80)
    private String processName;

    @Column(name = "SUPPORTS_HIERARCHY")
    @Type(type = "yes_no")
    private boolean supportsHierarchy = true;

    @Column(name = "URL", length = 12288)
    private String url;

    @Column(name = "IMAGE_TYPE", length = 16)
    private String imageType;

    @Column(name = "SEARCHABLE")
    @Type(type = "yes_no")
    private boolean searchable = true;

    @Transient
    private boolean selectAll;
    
    @Transient
    @InternationalizedCollection(referenceType="ResourceTypeEntity", targetField="displayName")
    private Map<String, LanguageMappingEntity> displayNameMap;
    
    @Transient
    private String displayName;

    public ResourceTypeEntity() {
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
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

    public Map<String, LanguageMappingEntity> getDisplayNameMap() {
		return displayNameMap;
	}

	public void setDisplayNameMap(Map<String, LanguageMappingEntity> displayNameMap) {
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
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((processName == null) ? 0 : processName.hashCode());
        result = prime * result + ((provisionResource == null) ? 0 : provisionResource.hashCode());
        result = prime * result + (searchable ? 1231 : 1237);
        result = prime * result + (supportsHierarchy ? 1231 : 1237);
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    public boolean getSelectAll() {
        return selectAll;
    }

    public void setSelectAll(boolean isSelectAll) {
        this.selectAll = isSelectAll;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourceTypeEntity other = (ResourceTypeEntity) obj;
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
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    public ResourceTypeEntity(String id, String description, String metadataTypeId, Integer provisionResource,
            String processName, boolean supportsHierarchy, boolean searchable) {
        super();
        this.id = id;
        this.description = description;
        this.provisionResource = provisionResource;
        this.processName = processName;
        this.supportsHierarchy = supportsHierarchy;
        this.searchable = searchable;
    }

}
