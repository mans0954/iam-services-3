package org.openiam.idm.searchbeans;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.org.dto.OrganizationType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationTypeSearchBean", propOrder = {
	"keySet",
	"name",
	"parentIds",
	"childIds"
})
public class OrganizationTypeSearchBean extends AbstractSearchBean<OrganizationType, String> implements SearchBean<OrganizationType, String> {

	private Set<String> keySet;
	private Set<String> parentIds;
	private Set<String> childIds;
	private String name;
	
	

	public Set<String> getKeySet() {
		return keySet;
	}

	public void setKeySet(Set<String> keySet) {
		this.keySet = keySet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
