package org.openiam.am.srvc.dto.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	private static final Log log = LogFactory.getLog(AbstractAuthorizationRight.class);

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
	public abstract void setEntity(T entity);

	public static AbstractAuthorizationRight getInstance(Class<? extends AbstractAuthorizationRight> clazz){
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			log.warn("Cannot create instance of: "+clazz.getName(), e);
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rights == null) ? 0 : rights.hashCode());
		result = prime * result + ((getEntity() == null) ? 0 : getEntity().hashCode());
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
		if (getEntity() == null) {
			if (other.getEntity() != null)
				return false;
		} else if (!getEntity().equals(other.getEntity()))
			return false;
		return true;
	}

	
}
