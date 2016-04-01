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

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(active != null ? active.booleanValue() : "")
                .append(usedForSMSOTP != null ? usedForSMSOTP.booleanValue() : "")
                .append(syncManagedSys != null ? syncManagedSys.booleanValue() : "")
                .append(getKeys() != null ? getKeys().toString().hashCode() : "")
                .append(grouping != null ? grouping.toString().hashCode() : "")
                .append(getKey() != null ? getKey() : "")
                .append(getSortKeyForCache())
                .toString();    }

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

}
