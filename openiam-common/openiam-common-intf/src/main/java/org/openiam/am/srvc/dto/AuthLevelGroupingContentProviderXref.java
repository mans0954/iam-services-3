package org.openiam.am.srvc.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.AuthLevelGroupingContentProviderXrefEntity;
import org.openiam.am.srvc.domain.pk.AuthLevelGroupingContentProviderXrefIdEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthLevelGroupingContentProviderXrefEntity", propOrder = {
        "id"
})
@DozerDTOCorrespondence(AuthLevelGroupingContentProviderXrefEntity.class)
public class AuthLevelGroupingContentProviderXref extends AbstractAuthLevelGroupingXref {

	private AuthLevelGroupingContentProviderXrefId id;
	
	public AuthLevelGroupingContentProviderXref() {
		
	}
	
	public AuthLevelGroupingContentProviderXrefId getId() {
		return id;
	}
	public void setId(AuthLevelGroupingContentProviderXrefId id) {
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
		AuthLevelGroupingContentProviderXref other = (AuthLevelGroupingContentProviderXref) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AuthLevelGroupingContentProviderXref [id=%s]", id);
	}

	
}
