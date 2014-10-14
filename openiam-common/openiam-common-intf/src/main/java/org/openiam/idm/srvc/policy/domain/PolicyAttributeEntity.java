package org.openiam.idm.srvc.policy.domain;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;

/**
 * @author zaporozhec
 */

/**
 * PolicyAttribute is used to add additional attributes to a policy object.
 */
@Entity
@Table(name = "POLICY_ATTRIBUTE")
@DozerDTOCorrespondence(PolicyAttribute.class)
public class PolicyAttributeEntity implements java.io.Serializable, Comparable<PolicyAttributeEntity> {

    private static final long serialVersionUID = -291717117636794761L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "POLICY_ATTR_ID", length = 32, nullable = false, updatable = false)
    private String policyAttrId;
    @Column(name = "POLICY_ID", nullable = false, updatable = false)
    private String policyId;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "DEF_PARAM_ID", insertable = true, updatable = true, nullable = true)
    private PolicyDefParamEntity defaultParametr;

    /**
     * Deprecated for Password Policy, got from PolicyDefParamEntity
     */
//    @Column(name = "NAME", length = 100)
//    private String name;

    /**
     * Deprecated for Password Policy, got from PolicyDefParamEntity
     */
//    @Column(name = "OPERATION", length = 20)
//    private String operation;

    @Column(name = "VALUE1", length = 2048)
    private String value1;
    @Column(name = "VALUE2", length = 2048)
    private String value2;

    @Column(name = "REQUIRED")
    @Type(type = "yes_no")
    private boolean required = true;

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Column(name = "RULE_TEXT")
    private String rule;

    public PolicyAttributeEntity() {
    }

    public PolicyAttributeEntity(String policyAttrId) {
        this.policyAttrId = policyAttrId;
    }


    public String getPolicyAttrId() {
        return this.policyAttrId;
    }

    public void setPolicyAttrId(String policyAttrId) {
        this.policyAttrId = policyAttrId;
    }
//
//    public String getName() {
//        return this.name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getOperation() {
//        return this.operation;
//    }
//
//    public void setOperation(String operation) {
//        this.operation = operation;
//    }

    public String getValue1() {
        return this.value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return this.value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public PolicyDefParamEntity getDefaultParametr() {
        return defaultParametr;
    }

    public void setDefaultParametr(PolicyDefParamEntity defaultParametr) {
        this.defaultParametr = defaultParametr;
    }


    public int compareTo(PolicyAttributeEntity o) {
        if (this.getDefaultParametr() == null && o.getDefaultParametr() == null)
            return 0;

        if (o == null || o.getDefaultParametr() == null || o.getDefaultParametr().getName() == null) {
            return Integer.MIN_VALUE;
        }
        return defaultParametr.getName().compareTo(o.getDefaultParametr().getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PolicyAttributeEntity that = (PolicyAttributeEntity) o;

        if (required != that.required) return false;
        if (defaultParametr != null ? !defaultParametr.equals(that.defaultParametr) : that.defaultParametr != null)
            return false;
        if (policyAttrId != null ? !policyAttrId.equals(that.policyAttrId) : that.policyAttrId != null) return false;
        if (policyId != null ? !policyId.equals(that.policyId) : that.policyId != null) return false;
        if (rule != null ? !rule.equals(that.rule) : that.rule != null) return false;
        if (value1 != null ? !value1.equals(that.value1) : that.value1 != null) return false;
        if (value2 != null ? !value2.equals(that.value2) : that.value2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = policyAttrId != null ? policyAttrId.hashCode() : 0;
        result = 31 * result + (policyId != null ? policyId.hashCode() : 0);
        result = 31 * result + (defaultParametr != null ? defaultParametr.hashCode() : 0);
        result = 31 * result + (value1 != null ? value1.hashCode() : 0);
        result = 31 * result + (value2 != null ? value2.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (rule != null ? rule.hashCode() : 0);
        return result;
    }
}
