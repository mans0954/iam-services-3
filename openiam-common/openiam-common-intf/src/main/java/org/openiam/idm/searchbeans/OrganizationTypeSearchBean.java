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
public class OrganizationTypeSearchBean extends AbstractKeyNameSearchBean<OrganizationType, String> {

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
}
