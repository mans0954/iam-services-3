package org.openiam.idm.srvc.meta.domain.pk;

import javax.persistence.Column;
import java.io.Serializable;

public class MetadataElementPageTemplateXrefIdEntity implements Serializable {
    @Column(name="TEMPLATE_ID", length = 32, nullable = false)
    private String metadataElementPageTemplateId;
    @Column(name="METADATA_ELEMENT_ID", length = 32, nullable = false)
    private String metadataElementId;

    public MetadataElementPageTemplateXrefIdEntity(String metadataElementPageTemplateId, String metadataElementId) {
        this.metadataElementPageTemplateId = metadataElementPageTemplateId;
        this.metadataElementId = metadataElementId;
    }
    public MetadataElementPageTemplateXrefIdEntity() {
    }

    public String getMetadataElementPageTemplateId() {
        return metadataElementPageTemplateId;
    }

    public void setMetadataElementPageTemplateId(String metadataElementPageTemplateId) {
        metadataElementPageTemplateId = metadataElementPageTemplateId;
    }

    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String metadataElementId) {
        metadataElementId = metadataElementId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MetadataElementPageTemplateXrefIdEntity that = (MetadataElementPageTemplateXrefIdEntity) o;

        if (metadataElementId != null ? !metadataElementId.equals(that.metadataElementId) :
            that.metadataElementId != null) {
            return false;
        }
        if (metadataElementPageTemplateId != null ?
            !metadataElementPageTemplateId.equals(that.metadataElementPageTemplateId) :
            that.metadataElementPageTemplateId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = metadataElementPageTemplateId != null ? metadataElementPageTemplateId.hashCode() : 0;
        result = 31 * result + (metadataElementId != null ? metadataElementId.hashCode() : 0);
        return result;
    }
}
