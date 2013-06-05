package org.openiam.idm.searchbeans;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.meta.dto.MetadataType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataTypeSearchBean", propOrder = {
	"active",
	"syncManagedSys",
	"grouping",
	"keySet"
})
public class MetadataTypeSearchBean extends AbstractSearchBean<MetadataType, String> implements SearchBean<MetadataType, String> {

	private Set<String> keySet;
	private boolean active;
    private boolean syncManagedSys;
    private String grouping;
    
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isSyncManagedSys() {
		return syncManagedSys;
	}
	
	public void setSyncManagedSys(boolean syncManagedSys) {
		this.syncManagedSys = syncManagedSys;
	}

	public String getGrouping() {
		return grouping;
	}

	public void setGrouping(String grouping) {
		this.grouping = grouping;
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
	
	@Override
	public String getKey() {
		return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next() : null;
	}
}
