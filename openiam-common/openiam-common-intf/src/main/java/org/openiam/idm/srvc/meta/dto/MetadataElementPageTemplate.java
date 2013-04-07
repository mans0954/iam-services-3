package org.openiam.idm.srvc.meta.dto;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetaElementPageTemplate", propOrder = {
        "id",
        "name",
        "resourceId",
        "metadataElements",
        "uriPatterns",
        "isPublic"
})
@DozerDTOCorrespondence(MetadataElementPageTemplateEntity.class)
public class MetadataElementPageTemplate implements Serializable {

	private String id;
	private String name;
	private String resourceId;
	private Set<URIPattern> uriPatterns;
	private Set<MetadataElementPageTemplateXref> metadataElements;
	private boolean isPublic = true;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public Set<MetadataElementPageTemplateXref> getMetadataElements() {
		return metadataElements;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
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
		MetadataElementPageTemplate other = (MetadataElementPageTemplate) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (isPublic != other.isPublic)
			return false;
		return true;
	}
	
	
	
}
