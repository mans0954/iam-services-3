package org.openiam.idm.srvc.res.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;

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
@DozerDTOCorrespondence(ResourcePropEntity.class)
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

    public String getResourcePropId() {
        return this.resourcePropId;
    }

    public void setResourcePropId(String resourcePropId) {
        this.resourcePropId = resourcePropId;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getMetadataId() {
        return this.metadataId;
    }

    public void setMetadataId(String metadataId) {
        this.metadataId = metadataId;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

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
