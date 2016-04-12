package org.openiam.idm.searchbeans;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataTypeSearchBean", propOrder = { 
	"active",
	"syncManagedSys", 
	"grouping", 
	"keySet",
	"usedForSMSOTP"
})
public class MetadataTypeSearchBean extends AbstractLanguageSearchBean<MetadataType, String> implements SearchBean<MetadataType, String> {

    private Set<String> keySet;
    private Boolean active;
    private Boolean syncManagedSys;
    private MetadataTypeGrouping grouping;
    private Boolean usedForSMSOTP;

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean isSyncManagedSys() {
        return syncManagedSys;
    }

    public void setSyncManagedSys(Boolean syncManagedSys) {
        this.syncManagedSys = syncManagedSys;
    }

    public MetadataTypeGrouping getGrouping() {
		return grouping;
	}

	public void setGrouping(MetadataTypeGrouping grouping) {
		this.grouping = grouping;
	}

    @Override
    public void setKey(final String key) {
        if (keySet == null) {
            keySet = new HashSet<String>();
        }
        keySet.add(key);
    }


    public Set<String> getKeys() {
        return keySet;
    }

    public void addKey(final String key) {
        if (this.keySet == null) {
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
    
	public Boolean getUsedForSMSOTP() {
		return usedForSMSOTP;
	}

	public void setUsedForSMSOTP(Boolean usedForSMSOTP) {
		this.usedForSMSOTP = usedForSMSOTP;
	}

	@Override
    public String getKey() {
        return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next()
                : null;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result
				+ ((grouping == null) ? 0 : grouping.hashCode());
		result = prime * result + ((keySet == null) ? 0 : keySet.hashCode());
		result = prime * result
				+ ((syncManagedSys == null) ? 0 : syncManagedSys.hashCode());
		result = prime * result
				+ ((usedForSMSOTP == null) ? 0 : usedForSMSOTP.hashCode());
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
		MetadataTypeSearchBean other = (MetadataTypeSearchBean) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (grouping != other.grouping)
			return false;
		if (keySet == null) {
			if (other.keySet != null)
				return false;
		} else if (!keySet.equals(other.keySet))
			return false;
		if (syncManagedSys == null) {
			if (other.syncManagedSys != null)
				return false;
		} else if (!syncManagedSys.equals(other.syncManagedSys))
			return false;
		if (usedForSMSOTP == null) {
			if (other.usedForSMSOTP != null)
				return false;
		} else if (!usedForSMSOTP.equals(other.usedForSMSOTP))
			return false;
		return true;
	}

	
}
