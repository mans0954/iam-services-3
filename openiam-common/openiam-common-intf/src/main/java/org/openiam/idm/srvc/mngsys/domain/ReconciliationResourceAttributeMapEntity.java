package org.openiam.idm.srvc.mngsys.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    @ManyToOne(optional = true)
    @JoinColumn(name = "ATTR_POLICY_ID", nullable = true, updatable = true)
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
}
