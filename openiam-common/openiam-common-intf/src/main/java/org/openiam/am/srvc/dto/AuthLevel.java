package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthLevelEntity;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthLevel", propOrder = {
	"requiresAuthentication"
})
@DozerDTOCorrespondence(AuthLevelEntity.class)
public class AuthLevel extends KeyNameDTO {

	private boolean requiresAuthentication = true;

	public AuthLevel() {}
	
	public boolean isRequiresAuthentication() {
		return requiresAuthentication;
	}

	public void setRequiresAuthentication(boolean requiresAuthentication) {
		this.requiresAuthentication = requiresAuthentication;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (requiresAuthentication ? 1231 : 1237);
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
		AuthLevel other = (AuthLevel) obj;
		if (requiresAuthentication != other.requiresAuthentication)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthLevel [requiresAuthentication=" + requiresAuthentication
				+ ", toString()=" + super.toString() + "]";
	}

	
}
