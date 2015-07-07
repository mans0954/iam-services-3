package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openiam.base.domain.KeyEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zaporozhec on 7/8/15.
 */

@Entity
@Table(name = "XACML_XPOLICY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "POLICY_ID"))
public class XACMLPolicyEntity extends KeyEntity {

    @Column(name = "POLICY_IDENTIFIER", length = 255)
    private String identifier;
    @Column(name = "POLICY_VERSION", length = 20)
    private String version;

    @Column(name = "RULE_COMB_ALG", length = 255)
    private String combinationAlgorithm;

    @Column(name = "MAX_DELEGATION_DEPTH")
    private Integer maxDelegationDepth;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "ISSUER", length = 255)
    private String issuer;

    @Column(name = "POLICY_ID_REF", length = 255)
    private String policyIdReferences;

    @Column(name = "POLICY_DEFAULTS", length = 255)
    private String policyDefaults;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "TARGET_ID", referencedColumnName = "TARGET_ID")
    private XACMLTargetEntity targetEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "OBLIGATIONS_ID", referencedColumnName = "OBLIGATIONS_ID")
    private XACMLObligationsEntity obligationsEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "COMBINER_PARAMS_ID", referencedColumnName = "COMBINER_PARAMS_ID")
    private XACMLCombainerParamsEntity combainerParamsEntity;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "XACML_XPOLICY_SET_XPOLICY",
            joinColumns = {@JoinColumn(name = "POLICY_SET_ID")},
            inverseJoinColumns = {@JoinColumn(name = "POLICY_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<XACMLPolicySetEntity> policySets = new HashSet<XACMLPolicySetEntity>();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCombinationAlgorithm() {
        return combinationAlgorithm;
    }

    public void setCombinationAlgorithm(String combinationAlgorithm) {
        this.combinationAlgorithm = combinationAlgorithm;
    }

    public Integer getMaxDelegationDepth() {
        return maxDelegationDepth;
    }

    public void setMaxDelegationDepth(Integer maxDelegationDepth) {
        this.maxDelegationDepth = maxDelegationDepth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getPolicyIdReferences() {
        return policyIdReferences;
    }

    public void setPolicyIdReferences(String policyIdReferences) {
        this.policyIdReferences = policyIdReferences;
    }

    public String getPolicyDefaults() {
        return policyDefaults;
    }

    public void setPolicyDefaults(String policyDefaults) {
        this.policyDefaults = policyDefaults;
    }

    public XACMLTargetEntity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(XACMLTargetEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public XACMLObligationsEntity getObligationsEntity() {
        return obligationsEntity;
    }

    public void setObligationsEntity(XACMLObligationsEntity obligationsEntity) {
        this.obligationsEntity = obligationsEntity;
    }

    public XACMLCombainerParamsEntity getCombainerParamsEntity() {
        return combainerParamsEntity;
    }

    public void setCombainerParamsEntity(XACMLCombainerParamsEntity combainerParamsEntity) {
        this.combainerParamsEntity = combainerParamsEntity;
    }

    public Set<XACMLPolicySetEntity> getPolicySets() {
        return policySets;
    }

    public void setPolicySets(Set<XACMLPolicySetEntity> policySets) {
        this.policySets = policySets;
    }
}
