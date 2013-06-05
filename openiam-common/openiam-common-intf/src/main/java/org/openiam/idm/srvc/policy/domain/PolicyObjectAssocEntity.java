package org.openiam.idm.srvc.policy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc;

/**
 * 
 * @author zaporozhec
 *
 */
@Entity
@Table(name = "POLICY_OBJECT_ASSOC")
@DozerDTOCorrespondence(PolicyObjectAssoc.class)
public class PolicyObjectAssocEntity implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "POLICY_OBJECT_ID", length = 32)
    private String policyObjectId;
    @Column(name = "POLICY_ID", length = 32)
    private String policyId;
    @Column(name = "ASSOCIATION_LEVEL", length = 20)
    private String associationLevel;
    @Column(name = "ASSOCIATION_VALUE", length = 255)
    private String associationValue;
    @Column(name = "OBJECT_TYPE", length = 100)
    private String objectType;
    @Column(name = "OBJECT_ID", length = 32)
    private String objectId;
    @Column(name = "PARENT_ASSOC_ID", length = 32)
    private String parentAssocId;


    public PolicyObjectAssocEntity() {
    }

    public String getPolicyObjectId() {
        return policyObjectId;
    }

    public void setPolicyObjectId(String policyObjectId) {
        this.policyObjectId = policyObjectId;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getAssociationLevel() {
        return associationLevel;
    }

    public void setAssociationLevel(String associationLevel) {
        this.associationLevel = associationLevel;
    }

    public String getAssociationValue() {
        return associationValue;
    }

    public void setAssociationValue(String associationValue) {
        this.associationValue = associationValue;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getParentAssocId() {
        return parentAssocId;
    }

    public void setParentAssocId(String parentAssocId) {
        this.parentAssocId = parentAssocId;
    }


}
