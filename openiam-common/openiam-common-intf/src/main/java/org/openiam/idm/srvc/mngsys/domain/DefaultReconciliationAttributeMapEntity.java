package org.openiam.idm.srvc.mngsys.domain;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.base.domain.AbstractKeyNameEntity;
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
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "DEF_ATTR_MAP_ID", length = 32)),
	@AttributeOverride(name = "name", column = @Column(name = "DEF_ATTR_MAP_NAME", length = 100))
})
public class DefaultReconciliationAttributeMapEntity extends AbstractKeyNameEntity {

    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "defaultAttributePolicy")
    private Set<ReconciliationResourceAttributeMapEntity> attributeMaps = new HashSet<ReconciliationResourceAttributeMapEntity>(0);

    public Set<ReconciliationResourceAttributeMapEntity> getAttributeMaps() {
        return attributeMaps;
    }

    public void setAttributeMaps(Set<ReconciliationResourceAttributeMapEntity> attributeMaps) {
        this.attributeMaps = attributeMaps;
    }

   
}
