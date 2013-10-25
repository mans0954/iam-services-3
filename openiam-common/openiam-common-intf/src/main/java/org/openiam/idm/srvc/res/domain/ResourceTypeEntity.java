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

    @Column(name = "METADATA_TYPE_ID", length = 20)
    private String metadataTypeId;

    @Column(name = "PROVISION_RESOURCE")
    private Integer provisionResource;

    @Column(name = "PROCESS_NAME", length = 80)
    private String processName;
    
    @Column(name="SUPPORTS_HIERARCHY")
    @Type(type = "yes_no")
    private boolean supportsHierarchy = true;

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

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
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

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceTypeEntity that = (ResourceTypeEntity) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (metadataTypeId != null ? !metadataTypeId.equals(that.metadataTypeId) : that.metadataTypeId != null)
            return false;
        if (processName != null ? !processName.equals(that.processName) : that.processName != null) return false;
        if (provisionResource != null ? !provisionResource.equals(that.provisionResource) : that.provisionResource != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (metadataTypeId != null ? metadataTypeId.hashCode() : 0);
        result = 31 * result + (provisionResource != null ? provisionResource.hashCode() : 0);
        result = 31 * result + (processName != null ? processName.hashCode() : 0);
        return result;
    }
}
