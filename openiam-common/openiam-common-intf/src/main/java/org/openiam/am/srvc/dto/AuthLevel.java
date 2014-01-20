package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthLevelEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthLevel", propOrder = {
        "id",
        "name",
        "requiresAuthentication"
})
@DozerDTOCorrespondence(AuthLevelEntity.class)
public class AuthLevel {

	private String id;
	private String name;
	private boolean requiresAuthentication = true;
//    @XmlTransient
//    private Set<ContentProvider> contentProviderSet;
	
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

	public boolean isRequiresAuthentication() {
		return requiresAuthentication;
	}

	public void setRequiresAuthentication(boolean requiresAuthentication) {
		this.requiresAuthentication = requiresAuthentication;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (requiresAuthentication ? 1231 : 1237);
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
		AuthLevel other = (AuthLevel) obj;
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
		if (requiresAuthentication != other.requiresAuthentication)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"AuthLevel [id=%s, name=%s, requiresAuthentication=%s]", id,
				name, requiresAuthentication);
	}
	
	
}
