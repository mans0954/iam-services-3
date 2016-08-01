package org.openiam.base.request;

import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;

import java.io.Serializable;

/**
 * Created by alexander on 29/07/16.
 */
public class UpdateAttributeByMetadataRequest implements Serializable{
    private String metadataElementId;
    private String metadataTypeId;
    private MetadataTypeGrouping metadataTypeGrouping;
    private String name;
    private String defaultValue;
    private boolean isRequired;

    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String metadataElementId) {
        this.metadataElementId = metadataElementId;
    }

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public MetadataTypeGrouping getMetadataTypeGrouping() {
        return metadataTypeGrouping;
    }

    public void setMetadataTypeGrouping(MetadataTypeGrouping metadataTypeGrouping) {
        this.metadataTypeGrouping = metadataTypeGrouping;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UpdateAttributeByMetadataRequest{");
        sb.append("metadataElementId='").append(metadataElementId).append('\'');
        sb.append(", metadataTypeId='").append(metadataTypeId).append('\'');
        sb.append(", metadataTypeGrouping=").append(metadataTypeGrouping);
        sb.append(", name='").append(name).append('\'');
        sb.append(", defaultValue='").append(defaultValue).append('\'');
        sb.append(", isRequired=").append(isRequired);
        sb.append('}');
        return sb.toString();
    }
}
