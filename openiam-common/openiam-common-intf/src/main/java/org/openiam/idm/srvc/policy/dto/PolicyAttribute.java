package org.openiam.idm.srvc.policy.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;

// Generated Mar 7, 2009 11:47:12 AM by Hibernate Tools 3.2.2.GA

/**
 * PolicyAttribute is used to add additional attributes to a policy object.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyAttribute", propOrder = {"policyAttrId", "policyId",
        "defParamId", "name", "operation", "value1", "value2", "rule", "description",
        "required", "grouping"})
@DozerDTOCorrespondence(PolicyAttributeEntity.class)
public class PolicyAttribute implements java.io.Serializable,
        Comparable<PolicyAttribute> {

    private static final long serialVersionUID = -291717117636794761L;
    protected String policyAttrId;
    protected String policyId;
    protected String defParamId;
    protected String name;
    protected String operation;
    protected String value1;
    protected String value2;
    protected String rule;
    protected String description;
    protected String grouping;
    protected boolean required = true;

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public PolicyAttribute() {
    }

    public PolicyAttribute(String policyAttrId) {
        this.policyAttrId = policyAttrId;
    }

    public String getPolicyAttrId() {
        return this.policyAttrId;
    }

    public void setPolicyAttrId(String policyAttrId) {
        this.policyAttrId = policyAttrId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getDefParamId() {
        return defParamId;
    }

    public void setDefParamId(String defParamId) {
        this.defParamId = defParamId;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    @Override
    public String toString() {
        return "PolicyAttribute [policyAttrId=" + policyAttrId + ", policyId="
                + policyId + ", defParamId=" + defParamId + ", name=" + name
                + ", operation=" + operation + ", value1=" + value1
                + ", value2=" + value2 + ", rule=" + rule + ", required="
                + required + "]";
    }

    public int compareTo(PolicyAttribute o) {
        if (getName() == null || o == null) {
            // Not recommended, but compareTo() is only used for display
            // purposes in this case
            return Integer.MIN_VALUE;
        }
        return getName().compareTo(o.getName());
    }
}
