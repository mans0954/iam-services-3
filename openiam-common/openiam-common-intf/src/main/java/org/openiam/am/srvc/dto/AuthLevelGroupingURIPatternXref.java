package org.openiam.am.srvc.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.AuthLevelGroupingURIPatternXrefEntity;
import org.openiam.am.srvc.domain.pk.AuthLevelGroupingURIPatternXrefIdEntity;

import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthLevelGroupingURIPatternXrefEntity", propOrder = {
        "id"
})
@DozerDTOCorrespondence(AuthLevelGroupingURIPatternXrefEntity.class)
public class AuthLevelGroupingURIPatternXref extends AbstractAuthLevelGroupingXref {

	private AuthLevelGroupingURIPatternXrefId id;
	
	public AuthLevelGroupingURIPatternXref() {
		
	}
	
	public AuthLevelGroupingURIPatternXrefId getId() {
		return id;
	}
	public void setId(AuthLevelGroupingURIPatternXrefId id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AuthLevelGroupingURIPatternXref other = (AuthLevelGroupingURIPatternXref) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AuthLevelGroupingURIPatternXref [id=%s]", id);
	}
	
	
}
