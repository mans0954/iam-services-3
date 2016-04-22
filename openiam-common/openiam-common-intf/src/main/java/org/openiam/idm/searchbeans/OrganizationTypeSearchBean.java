package org.openiam.idm.searchbeans;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.org.dto.OrganizationType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationTypeSearchBean", propOrder = {
	"keySet",
	"parentIds",
	"childIds",
    "excludeIds"
})
public class OrganizationTypeSearchBean extends AbstractKeyNameSearchBean<OrganizationType, String> implements SearchBean<OrganizationType, String> {

	private Set<String> keySet;
	private Set<String> parentIds;
	private Set<String> childIds;
    private Set<String> excludeIds;

	public Set<String> getKeySet() {
		return keySet;
	}

	public void setKeySet(Set<String> keySet) {
		this.keySet = keySet;
	}

	public Set<String> getParentIds() {
		return parentIds;
	}

	public void setParentIds(Set<String> parentIds) {
		this.parentIds = parentIds;
	}

	public Set<String> getChildIds() {
		return childIds;
	}

	public void setChildIds(Set<String> childIds) {
		this.childIds = childIds;
	}

    public Set<String> getExcludeIds() {
        return excludeIds;
    }

    public void setExcludeIds(Set<String> excludeIds) {
        this.excludeIds = excludeIds;
    }
    public void addExcludeId(final String excludeId) {
        if(StringUtils.isNotBlank(excludeId)) {
            if(this.excludeIds == null) {
                this.excludeIds = new HashSet<String>();
            }
            this.excludeIds.add(excludeId);
        }
    }


    public void addChildId(final String childId) {
		if(StringUtils.isNotBlank(childId)) {
			if(this.childIds == null) {
				this.childIds = new HashSet<String>();
			}
			this.childIds.add(childId);
		}
	}
	
	public void addParentId(final String parentId) {
		if(StringUtils.isNotBlank(parentId)) {
			if(this.parentIds == null) {
				this.parentIds = new HashSet<String>();
			}
			this.parentIds.add(parentId);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((childIds == null) ? 0 : childIds.hashCode());
		result = prime * result
				+ ((excludeIds == null) ? 0 : excludeIds.hashCode());
		result = prime * result + ((keySet == null) ? 0 : keySet.hashCode());
		result = prime * result
				+ ((parentIds == null) ? 0 : parentIds.hashCode());
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
		OrganizationTypeSearchBean other = (OrganizationTypeSearchBean) obj;
		if (childIds == null) {
			if (other.childIds != null)
				return false;
		} else if (!childIds.equals(other.childIds))
			return false;
		if (excludeIds == null) {
			if (other.excludeIds != null)
				return false;
		} else if (!excludeIds.equals(other.excludeIds))
			return false;
		if (keySet == null) {
			if (other.keySet != null)
				return false;
		} else if (!keySet.equals(other.keySet))
			return false;
		if (parentIds == null) {
			if (other.parentIds != null)
				return false;
		} else if (!parentIds.equals(other.parentIds))
			return false;
		return true;
	}

    
}
