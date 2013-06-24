package org.openiam.idm.srvc.meta.dto.pk;

import java.io.Serializable;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.pk.MetadataElementPageTemplateXrefIdEntity;

@DozerDTOCorrespondence(MetadataElementPageTemplateXrefIdEntity.class)
public class MetadataElementPageTemplateXrefId implements Serializable {
	
	public MetadataElementPageTemplateXrefId() {
		
	}

	public MetadataElementPageTemplateXrefId(final String metadataElementPageTemplateId, final String metadataElementId) {
		this.metadataElementPageTemplateId = metadataElementPageTemplateId;
		this.metadataElementId = metadataElementId;
	}
	
	private String metadataElementPageTemplateId;
	private String metadataElementId;
	
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
		MetadataElementPageTemplateXrefId other = (MetadataElementPageTemplateXrefId) obj;
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
