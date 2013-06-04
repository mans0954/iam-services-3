package org.openiam.idm.srvc.mngsys.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ReconciliationResourceAttributeMapEntity;
import org.openiam.idm.srvc.policy.dto.Policy;

/**
 * @author zaporozhec
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReconciliationResourceAttributeMap", propOrder = {
        "reconciliationResourceAttributeMapId", "attributePolicy",
        "defaultAttributePolicy", "attributeMapId" })
@DozerDTOCorrespondence(ReconciliationResourceAttributeMapEntity.class)
public class ReconciliationResourceAttributeMap implements java.io.Serializable {

    private static final long serialVersionUID = -4584242607384442243L;
    private String reconciliationResourceAttributeMapId;
    private Policy attributePolicy;
    private DefaultReconciliationAttributeMap defaultAttributePolicy;

    public String getReconciliationResourceAttributeMapId() {
        return reconciliationResourceAttributeMapId;
    }

    public void setReconciliationResourceAttributeMapId(
            String reconciliationResourceAttributeMapId) {
        this.reconciliationResourceAttributeMapId = reconciliationResourceAttributeMapId;
    }

    public Policy getAttributePolicy() {
        return attributePolicy;
    }

    public void setAttributePolicy(Policy attributePolicy) {
        this.attributePolicy = attributePolicy;
    }

    public DefaultReconciliationAttributeMap getDefaultAttributePolicy() {
        return defaultAttributePolicy;
    }

    public void setDefaultAttributePolicy(
            DefaultReconciliationAttributeMap defaultAttributePolicy) {
        this.defaultAttributePolicy = defaultAttributePolicy;
    }

}
