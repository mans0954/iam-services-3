package org.openiam.idm.srvc.mngsys.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;

/**
 * @author zaporozhec
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DefaultReconciliationAttributeMap", propOrder = {
        "defaultAttributeMapId", "defaultAttributeMapName" })
@DozerDTOCorrespondence(DefaultReconciliationAttributeMapEntity.class)
public class DefaultReconciliationAttributeMap implements java.io.Serializable {

    private static final long serialVersionUID = -4584242607384442243L;
    private String defaultAttributeMapId;
    private String defaultAttributeMapName;

    @Override
    public String toString() {
        return "DefaultReconciliationAttributeMap [defaultAttributeMapId="
                + defaultAttributeMapId + ", defaultAttributeMapName="
                + defaultAttributeMapName + "]";
    }

    public String getDefaultAttributeMapId() {
        return defaultAttributeMapId;
    }

    public void setDefaultAttributeMapId(String defaultAttributeMapId) {
        this.defaultAttributeMapId = defaultAttributeMapId;
    }

    public String getDefaultAttributeMapName() {
        return defaultAttributeMapName;
    }

    public void setDefaultAttributeMapName(String defaultAttributeMapName) {
        this.defaultAttributeMapName = defaultAttributeMapName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((defaultAttributeMapId == null) ? 0 : defaultAttributeMapId
                        .hashCode());
        result = prime
                * result
                + ((defaultAttributeMapName == null) ? 0
                        : defaultAttributeMapName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultReconciliationAttributeMap other = (DefaultReconciliationAttributeMap) obj;
        if (defaultAttributeMapId == null) {
            if (other.defaultAttributeMapId != null)
                return false;
        } else if (!defaultAttributeMapId.equals(other.defaultAttributeMapId))
            return false;
        if (defaultAttributeMapName == null) {
            if (other.defaultAttributeMapName != null)
                return false;
        } else if (!defaultAttributeMapName
                .equals(other.defaultAttributeMapName))
            return false;
        return true;
    }
}
