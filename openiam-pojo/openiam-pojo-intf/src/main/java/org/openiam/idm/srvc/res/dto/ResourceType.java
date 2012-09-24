package org.openiam.idm.srvc.res.dto;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * ResourceType allows you to classify the resource.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceType", propOrder = {
        "resourceTypeId",
        "description",
        "metadataTypeId",
        "provisionResource",
        "processName"
})
@Entity
@Table(name="RESOURCE_TYPE")
public class ResourceType implements java.io.Serializable {

    private String resourceTypeId;
    private String description;
    private String metadataTypeId;
    private Integer provisionResource;
    private String processName;

    public ResourceType() {
    }

    public ResourceType(String resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    @Id
    @Column(name="RESOURCE_TYPE_ID", length=20)
    public String getResourceTypeId() {
        return this.resourceTypeId;
    }

    public void setResourceTypeId(String resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    @Column(name="DESCRIPTION",length=100)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="METADATA_TYPE_ID",length=20)
    public String getMetadataTypeId() {
        return this.metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    @Column(name="PROVISION_RESOURCE")
    public Integer getProvisionResource() {
        return this.provisionResource;
    }

    public void setProvisionResource(Integer provisionResource) {
        this.provisionResource = provisionResource;
    }

    @Column(name="PROCESS_NAME",length=80)
    public String getProcessName() {
        return this.processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    @Override
    public String toString() {
        return "ResourceType{" +
                "resourceTypeId='" + resourceTypeId + '\'' +
                ", description='" + description + '\'' +
                ", metadataTypeId='" + metadataTypeId + '\'' +
                ", provisionResource=" + provisionResource +
                ", processName='" + processName + '\'' +
                '}';
    }
}
