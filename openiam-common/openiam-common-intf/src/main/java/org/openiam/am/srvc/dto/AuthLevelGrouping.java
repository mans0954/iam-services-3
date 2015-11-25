package org.openiam.am.srvc.dto;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.AuthLevelGroupingContentProviderXrefEntity;
import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.am.srvc.domain.AuthLevelGroupingURIPatternXrefEntity;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthLevelGrouping", propOrder = {
        "authLevel",
        "attributes",
        "patternXrefs",
        "contentProviderXrefs"
})
@DozerDTOCorrespondence(AuthLevelGroupingEntity.class)
public class AuthLevelGrouping extends KeyNameDTO {

	private AuthLevel authLevel;
	private Set<AuthLevelAttribute> attributes;
	private Set<AuthLevelGroupingURIPatternXref> patternXrefs;
	private Set<AuthLevelGroupingContentProviderXref> contentProviderXrefs;
	
	public AuthLevelGrouping() {
		
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
		int result = super.hashCode();
		result = prime * result
				+ ((authLevel == null) ? 0 : authLevel.hashCode());
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
		AuthLevelGrouping other = (AuthLevelGrouping) obj;
		if (authLevel == null) {
			if (other.authLevel != null)
				return false;
		} else if (!authLevel.equals(other.authLevel))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthLevelGrouping [authLevel=" + authLevel + ", toString()="
				+ super.toString() + "]";
	}

	
}
