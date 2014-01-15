package org.openiam.idm.searchbeans;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.role.dto.Role;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoleSearchBean", propOrder = {
        "keySet",
        "name",
        "isRootsOnly",
        "managedSysId"
})
public class RoleSearchBean extends AbstractSearchBean<Role, String> implements SearchBean<Role, String>, Serializable {

	private static final long serialVersionUID = 1L;
    private Set<String> keySet;
	private String name;
    private String managedSysId;
	private Boolean isRootsOnly;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsRootsOnly() {
		return isRootsOnly;
	}

	public void setIsRootsOnly(boolean isRootsOnly) {
		this.isRootsOnly = isRootsOnly;
	}

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((isRootsOnly == null) ? 0 : isRootsOnly.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((managedSysId == null) ? 0 : managedSysId.hashCode());
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
		RoleSearchBean other = (RoleSearchBean) obj;
		if (isRootsOnly == null) {
			if (other.isRootsOnly != null)
				return false;
		} else if (!isRootsOnly.equals(other.isRootsOnly))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
        if (managedSysId == null) {
            if (other.managedSysId != null)
                return false;
        } else if (!managedSysId.equals(other.managedSysId))
            return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"RoleSearchBean [keySet=%s, name=%s, isRootsOnly=%s]", keySet, name,
				isRootsOnly);
	}

    @Override
    public String getKey() {
        return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next() : null;
    }

    @Override
    public void setKey(final String key) {
        if(keySet == null) {
            keySet = new HashSet<String>();
        }
        keySet.add(key);
    }

    public Set<String> getKeys() {
        return keySet;
    }

    public void addKey(final String key) {
        if(this.keySet == null) {
            this.keySet = new HashSet<String>();
        }
        this.keySet.add(key);
    }

    public boolean hasMultipleKeys() {
        return (keySet != null && keySet.size() > 1);
    }

    public void setKeys(final Set<String> keySet) {
        this.keySet = keySet;
    }
}
