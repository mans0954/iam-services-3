package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.base.domain.AbstractKeyNameValueEntity;

import javax.persistence.*;

/**
 * Created by zaporozhec on 7/8/15.
 */
@Entity
@Table(name = "XACML_COMBINER_PARAM")
//@DozerDTOCorrespondence(IdentityQuestGroup.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "COMBINER_PARAM_ID", length = 32)),
        @AttributeOverride(name = "name", column = @Column(name = "PARAM_NAME", length = 255, nullable = true)),
        @AttributeOverride(name = "value", column = @Column(name = "PARAM_VALUE", length = 255, nullable = true))
})
public class XACMLCombainerParamEntity extends AbstractKeyNameValueEntity {
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "COMBINER_PARAMS_ID", referencedColumnName = "COMBINER_PARAMS_ID")
   private XACMLCombainerParamsEntity combainerParamsEntity;


    public XACMLCombainerParamsEntity getCombainerParamsEntity() {
        return combainerParamsEntity;
    }

    public void setCombainerParamsEntity(XACMLCombainerParamsEntity combainerParamsEntity) {
        this.combainerParamsEntity = combainerParamsEntity;
    }
}
