package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.AuthLevel;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "AUTH_LEVEL")
@DozerDTOCorrespondence(AuthLevel.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "AUTH_LEVEL_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "AUTH_LEVEL_NAME", length = 100, nullable = false))
})
public class AuthLevelEntity extends AbstractKeyNameEntity {
	
	public AuthLevelEntity() {}
	
    @Column(name = "REQUIRES_AUTHENTICATION")
    @Type(type = "yes_no")
    private boolean requiresAuthentication = true;

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
		AuthLevelEntity other = (AuthLevelEntity) obj;
		if (requiresAuthentication != other.requiresAuthentication)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthLevelEntity [requiresAuthentication="
				+ requiresAuthentication + ", toString()=" + super.toString()
				+ "]";
	}
	

}
