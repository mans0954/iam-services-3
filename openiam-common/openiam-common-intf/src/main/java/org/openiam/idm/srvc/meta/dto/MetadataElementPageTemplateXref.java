package org.openiam.idm.srvc.meta.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.meta.dto.pk.MetadataElementPageTemplateXrefId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataElementPageTemplateXref", propOrder = {
        "id",
        "displayOrder" })
@DozerDTOCorrespondence(MetadataElementPageTemplateXrefEntity.class)
public class MetadataElementPageTemplateXref implements Serializable {
	private MetadataElementPageTemplateXrefId id;
    private Integer displayOrder;
    
    public MetadataElementPageTemplateXref() {
    	
    }
    
    public MetadataElementPageTemplateXref(final String templateId, final String metadataElementId, final Integer displayOrder) {
    	this.id = new MetadataElementPageTemplateXrefId(templateId, metadataElementId);
    	this.displayOrder = displayOrder;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

	public MetadataElementPageTemplateXrefId getId() {
		return id;
	}

	public void setId(MetadataElementPageTemplateXrefId id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		MetadataElementPageTemplateXref other = (MetadataElementPageTemplateXref) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
