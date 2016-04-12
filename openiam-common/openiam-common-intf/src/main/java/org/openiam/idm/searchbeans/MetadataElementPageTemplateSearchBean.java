package org.openiam.idm.searchbeans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataElementPageTemplateSearchBean", propOrder = {
		"keySet",
		"patternIds",
		"resourceId"
})
public class MetadataElementPageTemplateSearchBean extends AbstractKeyNameSearchBean<MetadataElementPageTemplate, String> implements SearchBean {

	private Set<String> patternIds;
	private Set<String> keySet;
	private String resourceId;

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
	
	public void addPatternId(final String patternId) {
		if(patternId != null) {
			if(this.patternIds == null) {
				this.patternIds = new HashSet<String>();
			}
			this.patternIds.add(patternId);
		}
	}

	public Set<String> getPatternIds() {
		return patternIds;
	}

	public void setPatternIds(Set<String> patternIds) {
		this.patternIds = patternIds;
	}

	@Override
	public String getKey() {
		return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next() : null;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((keySet == null) ? 0 : keySet.hashCode());
		result = prime * result
				+ ((patternIds == null) ? 0 : patternIds.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
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
		MetadataElementPageTemplateSearchBean other = (MetadataElementPageTemplateSearchBean) obj;
		if (keySet == null) {
			if (other.keySet != null)
				return false;
		} else if (!keySet.equals(other.keySet))
			return false;
		if (patternIds == null) {
			if (other.patternIds != null)
				return false;
		} else if (!patternIds.equals(other.patternIds))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		return true;
	}

	
}
