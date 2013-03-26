package org.openiam.idm.srvc.meta.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataElementPageTemplateXref", propOrder = {
        "metadataElementPageTemplateId",
        "metadataElementId",
        "displayOrder" })
@DozerDTOCorrespondence(MetadataElementPageTemplateXrefEntity.class)
public class MetadataElementPageTemplateXref implements Serializable {
    private String metadataElementPageTemplateId;
    private String metadataElementId;
    private Integer displayOrder;

    public String getMetadataElementPageTemplateId() {
        return metadataElementPageTemplateId;
    }

    public void setMetadataElementPageTemplateId(String metadataElementPageTemplateId) {
        this.metadataElementPageTemplateId = metadataElementPageTemplateId;
    }

    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String metadataElementId) {
        this.metadataElementId = metadataElementId;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MetadataElementPageTemplateXref that = (MetadataElementPageTemplateXref) o;

        if (displayOrder != null ? !displayOrder.equals(that.displayOrder) : that.displayOrder != null) {
            return false;
        }
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
        result = 31 * result + (displayOrder != null ? displayOrder.hashCode() : 0);
        return result;
    }
}
