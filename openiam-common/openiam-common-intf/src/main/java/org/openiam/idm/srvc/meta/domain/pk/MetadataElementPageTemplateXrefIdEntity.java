package org.openiam.idm.srvc.meta.domain.pk;

import javax.persistence.Column;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.pk.MetadataElementPageTemplateXrefId;

import java.io.Serializable;

@DozerDTOCorrespondence(MetadataElementPageTemplateXrefId.class)
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
	public void setMetadataElementPageTemplateId(
			String metadataElementPageTemplateId) {
		this.metadataElementPageTemplateId = metadataElementPageTemplateId;
	}
	public String getMetadataElementId() {
		return metadataElementId;
	}
	public void setMetadataElementId(String metadataElementId) {
		this.metadataElementId = metadataElementId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((metadataElementId == null) ? 0 : metadataElementId
						.hashCode());
		result = prime
				* result
				+ ((metadataElementPageTemplateId == null) ? 0
						: metadataElementPageTemplateId.hashCode());
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
		MetadataElementPageTemplateXrefIdEntity other = (MetadataElementPageTemplateXrefIdEntity) obj;
		if (metadataElementId == null) {
			if (other.metadataElementId != null)
				return false;
		} else if (!metadataElementId.equals(other.metadataElementId))
			return false;
		if (metadataElementPageTemplateId == null) {
			if (other.metadataElementPageTemplateId != null)
				return false;
		} else if (!metadataElementPageTemplateId
				.equals(other.metadataElementPageTemplateId))
			return false;
		return true;
	}

   
}
