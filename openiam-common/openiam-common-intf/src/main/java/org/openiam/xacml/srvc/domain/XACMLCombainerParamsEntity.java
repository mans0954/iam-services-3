package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.base.domain.KeyEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zaporozhec on 7/8/15.
 */
@Entity
@Table(name = "XACML_COMBINER_PARAMS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "COMBINER_PARAMS_ID"))
public class XACMLCombainerParamsEntity extends KeyEntity {


    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "combainerParamsEntity", fetch = FetchType.LAZY)
    private Set<XACMLCombainerParamEntity> combainerParamEntities = new HashSet<XACMLCombainerParamEntity>(0);

    public Set<XACMLCombainerParamEntity> getCombainerParamEntities() {
        return combainerParamEntities;
    }

    public void setCombainerParamEntities(Set<XACMLCombainerParamEntity> combainerParamEntities) {
        this.combainerParamEntities = combainerParamEntities;
    }
}
