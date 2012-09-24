package org.openiam.idm.srvc.res.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.GenericGenerator;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

/**
 * ResourceProp enables the extension of a resource by associated properties (name value pairs) to them.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceProp", propOrder = {
        "resourcePropId",
        "resourceId",
        "metadataId",
        "propValue",
        "name"
})
@Entity
@Table(name="RESOURCE_PROP")
public class ResourceProp implements java.io.Serializable, Comparable<ResourceProp> {

    private String resourcePropId;
    private String resourceId;
    private String metadataId;
    private String propValue;
    private String name;

    public ResourceProp() {
    }

    public ResourceProp(String resourcePropId) {
        this.resourcePropId = resourcePropId;
    }

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="RESOURCE_PROP_ID", length=32)
    public String getResourcePropId() {
        return this.resourcePropId;
    }

    public void setResourcePropId(String resourcePropId) {
        this.resourcePropId = resourcePropId;
    }

    @Column(name="RESOURCE_ID",length=32)
    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Column(name="METADATA_ID",length=20)
    public String getMetadataId() {
        return this.metadataId;
    }

    public void setMetadataId(String metadataId) {
        this.metadataId = metadataId;
    }

    @Column(name="PROP_VALUE",length=200)
    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    @Column(name="NAME",length=40)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ResourceProp{" +
                "resourcePropId='" + resourcePropId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", metadataId='" + metadataId + '\'' +
                ", propValue='" + propValue + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public int compareTo(ResourceProp o) {
        if (getName() == null || o == null) {
            // Not recommended, but compareTo() is only used for display purposes in this case
            return Integer.MIN_VALUE;
        }
        return getName().compareTo(o.getName());
    }
}
