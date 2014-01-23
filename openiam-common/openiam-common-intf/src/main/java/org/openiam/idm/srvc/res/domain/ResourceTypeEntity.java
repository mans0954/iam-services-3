package org.openiam.idm.srvc.res.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.dto.ResourceType;

@Entity
@Table(name = "RESOURCE_TYPE")
@DozerDTOCorrespondence(ResourceType.class)
public class ResourceTypeEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "RESOURCE_TYPE_ID", length = 32)
    private String id;

    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    @Column(name = "PROVISION_RESOURCE")
    private Integer provisionResource;

    @Column(name = "PROCESS_NAME", length = 80)
    private String processName;

    @Column(name = "SUPPORTS_HIERARCHY")
    @Type(type = "yes_no")
    private boolean supportsHierarchy = true;

    @Column(name = "URL", length = 512)
    private String url;

    @Column(name = "SEARCHABLE")
    @Type(type = "yes_no")
    private boolean searchable = true;

    public ResourceTypeEntity() {
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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
