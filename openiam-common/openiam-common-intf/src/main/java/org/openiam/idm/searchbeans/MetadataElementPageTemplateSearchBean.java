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
		"patternIds"
})
public class MetadataElementPageTemplateSearchBean extends AbstractKeyNameSearchBean<MetadataElementPageTemplate, String> {

	private Set<String> patternIds;
	private Set<String> keySet;

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
}
