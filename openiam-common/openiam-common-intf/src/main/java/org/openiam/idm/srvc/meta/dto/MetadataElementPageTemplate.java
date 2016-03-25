package org.openiam.idm.srvc.meta.dto;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.base.KeyDTO;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetaElementPageTemplate", propOrder = {
        "metadataTemplateTypeId",
        "metadataTemplateTypeName",
        "resourceId",
        "metadataElements",
        "uriPatterns",
        "isPublic",
        "fieldXrefs",
		"dataModelUrl",
		"customJS"
})
@DozerDTOCorrespondence(MetadataElementPageTemplateEntity.class)
public class MetadataElementPageTemplate extends KeyNameDTO {
	private String metadataTemplateTypeId;
	private String metadataTemplateTypeName;
	private String resourceId;
	private Set<URIPattern> uriPatterns;
	private Set<MetadataElementPageTemplateXref> metadataElements;
	private boolean isPublic = true;
	private String dataModelUrl;
	private String customJS;

	private Set<MetadataFieldTemplateXref> fieldXrefs;
	
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public Set<MetadataElementPageTemplateXref> getMetadataElements() {
		return metadataElements;
	}

	public String getDataModelUrl() {
		return dataModelUrl;
	}

	public void setDataModelUrl(String dataModelUrl) {
		this.dataModelUrl = dataModelUrl;
	}

	public String getCustomJS() {
		return customJS;
	}

	public void setCustomJS(String customJS) {
		this.customJS = customJS;
	}

	public void setMetadataElements(Set<MetadataElementPageTemplateXref> metadataElements) {
		this.metadataElements = metadataElements;
	}
	public void addMetdataElement(final MetadataElementPageTemplateXref xref) {
		if(xref != null) {
			if(this.metadataElements == null) {
				this.metadataElements = new HashSet<MetadataElementPageTemplateXref>();
			}
			this.metadataElements.add(xref);
		}
	}
	
	public void removeMetdataElement(final MetadataElementPageTemplateXref xref) {
		if(xref != null) {
			if(this.metadataElements != null) {
				this.metadataElements.remove(xref);
			}
		}
	}
	
	
	
	public Set<URIPattern> getUriPatterns() {
		return uriPatterns;
	}
	public void setUriPatterns(Set<URIPattern> uriPatterns) {
		this.uriPatterns = uriPatterns;
	}
	
	public void addPattern(final URIPattern pattern) {
		if(pattern != null) {
			if(this.uriPatterns == null) {
				this.uriPatterns = new HashSet<URIPattern>();
			}
			this.uriPatterns.add(pattern);
		}
	}
	
	public boolean getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public String getMetadataTemplateTypeId() {
		return metadataTemplateTypeId;
	}
	public void setMetadataTemplateTypeId(String metadataTemplateTypeId) {
		this.metadataTemplateTypeId = metadataTemplateTypeId;
	}
	public String getMetadataTemplateTypeName() {
		return metadataTemplateTypeName;
	}
	public void setMetadataTemplateTypeName(String metadataTemplateTypeName) {
		this.metadataTemplateTypeName = metadataTemplateTypeName;
	}
	
	public Set<MetadataFieldTemplateXref> getFieldXrefs() {
		return fieldXrefs;
	}
	public void setFieldXrefs(Set<MetadataFieldTemplateXref> fieldXrefs) {
		this.fieldXrefs = fieldXrefs;
	}
	
	public void addFieldXref(final MetadataFieldTemplateXref xref) {
		if(xref !=  null) {
			if(this.fieldXrefs == null) {
				this.fieldXrefs = new LinkedHashSet<MetadataFieldTemplateXref>();
			}
			this.fieldXrefs.add(xref);
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime
				* result
				+ ((metadataTemplateTypeId == null) ? 0
						: metadataTemplateTypeId.hashCode());
		result = prime
				* result
				+ ((metadataTemplateTypeName == null) ? 0
						: metadataTemplateTypeName.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime
				* result
				+ ((dataModelUrl == null) ? 0
				: dataModelUrl.hashCode());
		result = prime
				* result
				+ ((customJS == null) ? 0
				: customJS.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetadataElementPageTemplate other = (MetadataElementPageTemplate) obj;
		if (isPublic != other.isPublic)
			return false;
		if (metadataTemplateTypeId == null) {
			if (other.metadataTemplateTypeId != null)
				return false;
		} else if (!metadataTemplateTypeId.equals(other.metadataTemplateTypeId))
			return false;
		if (metadataTemplateTypeName == null) {
			if (other.metadataTemplateTypeName != null)
				return false;
		} else if (!metadataTemplateTypeName
				.equals(other.metadataTemplateTypeName))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (dataModelUrl == null) {
			if (other.dataModelUrl != null)
				return false;
		} else if (!dataModelUrl.equals(other.dataModelUrl))
			return false;
		if (customJS == null) {
			if (other.customJS != null)
				return false;
		} else if (!customJS.equals(other.customJS))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String
				.format("MetadataElementPageTemplate [metadataTemplateTypeId=%s, metadataTemplateTypeName=%s, resourceId=%s, isPublic=%s, toString()=%s]",
						metadataTemplateTypeId, metadataTemplateTypeName,
						resourceId, isPublic, super.toString());
	}
	
	
	
}
