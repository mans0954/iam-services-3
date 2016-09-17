package org.openiam.idm.srvc.entitlements;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.AbstractMetadataTypeDTO;
import org.openiam.base.AdminResourceDTO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractEntitlementsDTO", propOrder = {"accessRightIds"})
public abstract class AbstractEntitlementsDTO extends AdminResourceDTO {

    private Set<String> accessRightIds;

    public Set<String> getAccessRightIds() {
        return accessRightIds;
    }

    public void setAccessRightIds(Collection<String> accessRightIds) {
        if (accessRightIds != null) {
            this.accessRightIds = new HashSet<String>(accessRightIds);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((accessRightIds == null) ? 0 : accessRightIds.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        AbstractEntitlementsDTO other = (AbstractEntitlementsDTO) obj;
        if (accessRightIds == null) {
            if (other.accessRightIds != null) return false;
        } else if (!accessRightIds.equals(other.accessRightIds)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "AbstractEntitlementsDTO [ accessRightIds=" + accessRightIds + ", id=" + id + ", objectState=" + objectState + ", requestorSessionID=" + requestorSessionID + ", requestorUserId=" + requestorUserId + ", requestorLogin=" + requestorLogin + ", requestClientIP=" + requestClientIP + "]";
    }


}
