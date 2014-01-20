package org.openiam.am.srvc.dto;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.AuthLevelGroupingContentProviderXrefEntity;
import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.am.srvc.domain.AuthLevelGroupingURIPatternXrefEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthLevelGrouping", propOrder = {
        "id",
        "name",
        "authLevel",
        "attributes",
        "patternXrefs",
        "contentProviderXrefs"
})
@DozerDTOCorrespondence(AuthLevelGroupingEntity.class)
public class AuthLevelGrouping implements Serializable {

	private String id;
	private String name;
	private AuthLevel authLevel;
	private Set<AuthLevelAttribute> attributes;
	private Set<AuthLevelGroupingURIPatternXref> patternXrefs;
	private Set<AuthLevelGroupingContentProviderXref> contentProviderXrefs;
	
	public AuthLevelGrouping() {
		
	}

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

	public AuthLevel getAuthLevel() {
		return authLevel;
	}

	public void setAuthLevel(AuthLevel authLevel) {
		this.authLevel = authLevel;
	}

	public Set<AuthLevelAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<AuthLevelAttribute> attributes) {
		this.attributes = attributes;
	}

	public Set<AuthLevelGroupingURIPatternXref> getPatternXrefs() {
		return patternXrefs;
	}

	public void setPatternXrefs(Set<AuthLevelGroupingURIPatternXref> patternXrefs) {
		this.patternXrefs = patternXrefs;
	}

	public Set<AuthLevelGroupingContentProviderXref> getContentProviderXrefs() {
		return contentProviderXrefs;
	}

	public void setContentProviderXrefs(
			Set<AuthLevelGroupingContentProviderXref> contentProviderXrefs) {
		this.contentProviderXrefs = contentProviderXrefs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authLevel == null) ? 0 : authLevel.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AuthLevelGrouping other = (AuthLevelGrouping) obj;
		if (authLevel == null) {
			if (other.authLevel != null)
				return false;
		} else if (!authLevel.equals(other.authLevel))
			return false;
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
		return true;
	}

	@Override
	public String toString() {
		return String.format("AuthLevelGrouping [id=%s, authLevel=%s, name=%s]", 
				id, authLevel, name);
	}
	
	
}
