package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;

import javax.persistence.*;

/**
 * Created by zaporozhec on 7/8/15.
 */
@Entity
@Table(name = "XACML_OBLIGATION")
//@DozerDTOCorrespondence(IdentityQuestGroup.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "OBLIGATION_ID", length = 32)),
        @AttributeOverride(name = "name", column = @Column(name = "OBLIGATION_IDENTIFIER", length = 255))
})
public class XACMLObligationEntity extends AbstractKeyNameEntity {

    @Column(name = "FULFILL_ON", length = 10)
    private String fullFillOn;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "OBLIGATIONS_ID", referencedColumnName = "OBLIGATIONS_ID")
    private XACMLObligationsEntity obligationsEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ATTRIB_DESIGNATOR_ID", referencedColumnName = "ATTRIB_DESIGNATOR_ID", nullable = false)
    private XACMLAttributeDesignatorEntity attributeDesignatorEntity;

    public String getFullFillOn() {
        return fullFillOn;
    }

    public void setFullFillOn(String fullFillOn) {
        this.fullFillOn = fullFillOn;
    }

    public XACMLObligationsEntity getObligationsEntity() {
        return obligationsEntity;
    }

    public void setObligationsEntity(XACMLObligationsEntity obligationsEntity) {
        this.obligationsEntity = obligationsEntity;
    }

    public XACMLAttributeDesignatorEntity getAttributeDesignatorEntity() {
        return attributeDesignatorEntity;
    }

    public void setAttributeDesignatorEntity(XACMLAttributeDesignatorEntity attributeDesignatorEntity) {
        this.attributeDesignatorEntity = attributeDesignatorEntity;
    }
}
