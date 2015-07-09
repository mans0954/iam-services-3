package org.openiam.authmanager.common.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractAuthorizationRight", propOrder = {
        "rights"
})
public abstract class AbstractAuthorizationRight<T extends AbstractAuthorizationEntity> {

	private Set<AuthorizationAccessRight> rights;
	
	public AbstractAuthorizationRight() {}
	
	public void addRight(final AuthorizationAccessRight right) {
		if(right != null) {
			if(this.rights == null) {
				this.rights = new HashSet<AuthorizationAccessRight>();
			}
			this.rights.add(right);
		}
	}

	public Set<AuthorizationAccessRight> getRights() {
		return rights;
	}

	public void setRights(Set<AuthorizationAccessRight> rights) {
		this.rights = rights;
	}
	
	public abstract T getEntity();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rights == null) ? 0 : rights.hashCode());
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
		AbstractAuthorizationRight other = (AbstractAuthorizationRight) obj;
		if (rights == null) {
			if (other.rights != null)
				return false;
		} else if (!rights.equals(other.rights))
			return false;
		return true;
	}

	
}
