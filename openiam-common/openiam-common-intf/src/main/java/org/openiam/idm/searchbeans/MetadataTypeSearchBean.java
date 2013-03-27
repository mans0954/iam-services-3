package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.meta.dto.MetadataType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataTypeSearchBean", propOrder = {
	"active",
	"syncManagedSys"
})
public class MetadataTypeSearchBean extends AbstractSearchBean<MetadataType, String> implements SearchBean<MetadataType, String> {

	private boolean active;
    private boolean syncManagedSys;
    
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
}
