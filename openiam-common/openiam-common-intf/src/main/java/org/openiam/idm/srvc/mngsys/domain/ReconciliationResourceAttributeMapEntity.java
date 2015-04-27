package org.openiam.idm.srvc.mngsys.domain;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.ReconciliationResourceAttributeMap;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;

/**
 * @author zaporozhec
 */
@Entity
@Table(name = "RECON_RES_ATTR_MAP")
@DozerDTOCorrespondence(ReconciliationResourceAttributeMap.class)
public class ReconciliationResourceAttributeMapEntity implements
        java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "RECON_RES_ATTR_MAP_ID", length = 32, nullable = false)
    private String reconciliationResourceAttributeMapId;

    @OneToOne(mappedBy = "reconResAttribute", orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    private AttributeMapEntity attributeMap;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ATTR_POLICY_ID", nullable = false, updatable = true)
    private PolicyEntity attributePolicy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "DEF_RECON_ATTR_MAP_ID", nullable = true, updatable = true)
    private DefaultReconciliationAttributeMapEntity defaultAttributePolicy;

    public String getReconciliationResourceAttributeMapId() {
        return reconciliationResourceAttributeMapId;
    }

    public void setReconciliationResourceAttributeMapId(
            String reconciliationResourceAttributeMapId) {
        this.reconciliationResourceAttributeMapId = reconciliationResourceAttributeMapId;
    }

    public PolicyEntity getAttributePolicy() {
        return attributePolicy;
    }

    public void setAttributePolicy(PolicyEntity attributePolicy) {
        this.attributePolicy = attributePolicy;
    }

    public DefaultReconciliationAttributeMapEntity getDefaultAttributePolicy() {
        return defaultAttributePolicy;
    }

    public void setDefaultAttributePolicy(
            DefaultReconciliationAttributeMapEntity defaultAttributePolicy) {
        this.defaultAttributePolicy = defaultAttributePolicy;
    }

    public AttributeMapEntity getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(AttributeMapEntity attributeMap) {
        this.attributeMap = attributeMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReconciliationResourceAttributeMapEntity that = (ReconciliationResourceAttributeMapEntity) o;

        if (attributeMap != null ? !attributeMap.equals(that.attributeMap) : that.attributeMap != null) return false;
        if (attributePolicy != null ? !attributePolicy.equals(that.attributePolicy) : that.attributePolicy != null)
            return false;
        if (defaultAttributePolicy != null ? !defaultAttributePolicy.equals(that.defaultAttributePolicy) : that.defaultAttributePolicy != null)
            return false;
        if (reconciliationResourceAttributeMapId != null ? !reconciliationResourceAttributeMapId.equals(that.reconciliationResourceAttributeMapId) : that.reconciliationResourceAttributeMapId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = reconciliationResourceAttributeMapId != null ? reconciliationResourceAttributeMapId.hashCode() : 0;
        result = 31 * result + (attributeMap != null ? attributeMap.hashCode() : 0);
        result = 31 * result + (attributePolicy != null ? attributePolicy.hashCode() : 0);
        result = 31 * result + (defaultAttributePolicy != null ? defaultAttributePolicy.hashCode() : 0);
        return result;
    }
}
