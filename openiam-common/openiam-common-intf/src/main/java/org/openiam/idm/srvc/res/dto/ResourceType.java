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
        "supportsHierarchy"
})
@DozerDTOCorrespondence(ResourceTypeEntity.class)
public class ResourceType implements java.io.Serializable {

    private String id;
    private String description;
    private String metadataTypeId;
    private Integer provisionResource;
    private String processName;
    private boolean supportsHierarchy;

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

    @Override
    public String toString() {
        return "ResourceType{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", metadataTypeId='" + metadataTypeId + '\'' +
                ", provisionResource=" + provisionResource +
                ", processName='" + processName + '\'' +
                '}';
    }
}
