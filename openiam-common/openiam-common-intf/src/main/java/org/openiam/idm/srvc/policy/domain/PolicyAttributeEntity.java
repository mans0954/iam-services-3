package org.openiam.idm.srvc.policy.domain;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.base.domain.KeyEntity;
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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "POLICY_ATTR_ID")),
})
public class PolicyAttributeEntity extends KeyEntity {

    private static final long serialVersionUID = -291717117636794761L;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "POLICY_ID", referencedColumnName = "POLICY_ID", insertable = true, updatable = false)
    private PolicyEntity policy;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "DEF_PARAM_ID", insertable = true, updatable = true, nullable = true)
    private PolicyDefParamEntity defParam;


    @Column(name = "VALUE1", length = 2048)
    private String value1;
    @Column(name = "VALUE2", length = 2048)
    private String value2;

    @Column(name = "REQUIRED")
    @Type(type = "yes_no")
    private boolean required = true;

    @Column(name = "RULE_TEXT")
    private String rule;

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }


    public PolicyEntity getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyEntity policy) {
        this.policy = policy;
    }

    public PolicyAttributeEntity() {
    }

    public PolicyAttributeEntity(String policyAttrId) {
        this.id = policyAttrId;
    }

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

    public PolicyDefParamEntity getDefParam() {
        return defParam;
    }

    public void setDefParam(PolicyDefParamEntity defParam) {
        this.defParam = defParam;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public int compareTo(PolicyAttributeEntity o) {
        if (defParam == null || o == null || o.getDefParam() == null) {
            return Integer.MIN_VALUE;
        }
        return defParam.getName().compareTo(o.getDefParam().getName());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PolicyAttributeEntity that = (PolicyAttributeEntity) o;

        if (required != that.required) return false;
        if (defParam != null ? !defParam.equals(that.defParam) : that.defParam != null)
            return false;
        if (policy != null ? !policy.equals(that.policy) : that.policy != null) return false;
        if (rule != null ? !rule.equals(that.rule) : that.rule != null) return false;
        if (value1 != null ? !value1.equals(that.value1) : that.value1 != null) return false;
        if (value2 != null ? !value2.equals(that.value2) : that.value2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (policy != null ? policy.hashCode() : 0);
        result = 31 * result + (defParam != null ? defParam.hashCode() : 0);
        result = 31 * result + (value1 != null ? value1.hashCode() : 0);
        result = 31 * result + (value2 != null ? value2.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (rule != null ? rule.hashCode() : 0);
        return result;
    }
}
