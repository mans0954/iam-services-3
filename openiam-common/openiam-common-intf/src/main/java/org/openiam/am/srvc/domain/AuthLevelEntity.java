package org.openiam.am.srvc.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.AuthLevel;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "AUTH_LEVEL")
@DozerDTOCorrespondence(AuthLevel.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AuthLevelEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "AUTH_LEVEL_ID", length = 32, nullable = false)
	private String id;
	
	@Column(name="AUTH_LEVEL_NAME", length = 100, nullable = false)
	private String name;
	
    @Column(name = "REQUIRES_AUTHENTICATION")
    @Type(type = "yes_no")
    private boolean requiresAuthentication = true;
	
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
		AuthLevelEntity other = (AuthLevelEntity) obj;
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
				"AuthLevelEntity [id=%s, name=%s, requiresAuthentication=%s]",
				id, name, requiresAuthentication);
	}
	
	
}
