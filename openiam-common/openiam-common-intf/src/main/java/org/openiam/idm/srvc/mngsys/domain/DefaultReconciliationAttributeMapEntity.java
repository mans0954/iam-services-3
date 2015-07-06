package org.openiam.idm.srvc.mngsys.domain;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.DefaultReconciliationAttributeMap;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zaporozhec
 */
@Entity
@Table(name = "DEF_RECON_ATTR_MAP")
@DozerDTOCorrespondence(DefaultReconciliationAttributeMap.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DefaultReconciliationAttributeMapEntity implements
        java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DEF_ATTR_MAP_ID", length = 32, nullable = false)
    private String defaultAttributeMapId;

    @Column(name = "DEF_ATTR_MAP_NAME", length = 100)
    private String defaultAttributeMapName;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "defaultAttributePolicy")
    private Set<ReconciliationResourceAttributeMapEntity> attributeMaps = new HashSet<ReconciliationResourceAttributeMapEntity>(0);

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

    public Set<ReconciliationResourceAttributeMapEntity> getAttributeMaps() {
        return attributeMaps;
    }

    public void setAttributeMaps(Set<ReconciliationResourceAttributeMapEntity> attributeMaps) {
        this.attributeMaps = attributeMaps;
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
        DefaultReconciliationAttributeMapEntity other = (DefaultReconciliationAttributeMapEntity) obj;
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

    @Override
    public String toString() {
        return "DefaultReconciliationAttributeMapEntity [defaultAttributeMapId="
                + defaultAttributeMapId
                + ", defaultAttributeMapName="
                + defaultAttributeMapName + "]";
    }
}
