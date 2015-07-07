package org.openiam.idm.srvc.entitlements;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.AbstractMetadataTypeDTO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractEntitlementsDTO", propOrder = {
        "accessRightIds"
})
public abstract class AbstractEntitlementsDTO extends AbstractMetadataTypeDTO {

	private Set<String> accessRightIds;
	
	public Set<String> getAccessRightIds() {
		return accessRightIds;
	}

	public void setAccessRightIds(Collection<String> accessRightIds) {
		if(accessRightIds != null) {
			this.accessRightIds = new HashSet<String>(accessRightIds);
		}
	}
}
