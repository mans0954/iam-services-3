package org.openiam.idm.searchbeans;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.grp.dto.Group;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupSearchBean", propOrder = {
        "keySet",
        "name",
        "isRootsOnly"
})
public class GroupSearchBean extends AbstractSearchBean<Group, String> implements SearchBean<Group, String>, Serializable {

	private static final long serialVersionUID = 1L;
    private Set<String> keySet;
	private String name;
	private boolean isRootsOnly;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isRootsOnly ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		GroupSearchBean other = (GroupSearchBean) obj;
		if (isRootsOnly != other.isRootsOnly)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"GroupSearchBean [name=%s, isRootsOnly=%s, keySet=%s, toString()=%s]",
				name, isRootsOnly, keySet, super.toString());
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
