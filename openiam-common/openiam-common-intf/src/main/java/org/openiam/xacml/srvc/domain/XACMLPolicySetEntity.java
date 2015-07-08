package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.internationalization.Internationalized;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zaporozhec on 7/8/15.
 */

@Entity
@Table(name = "XACML_XPOLICY_SET")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "POLICY_SET_ID"))
public class XACMLPolicySetEntity extends KeyEntity {

    @Column(name = "POLICY_SET_IDENTIFIER", length = 255)
    private String identifier;
    @Column(name = "POLICY_SET_VERSION", length = 20)
    private String version;

    @Column(name = "POLICY_COMB_ALG", length = 255)
    private String combinationAlgorithm;

    @Column(name = "MAX_DELEGATION_DEPTH")
    private Integer maxDelegationDepth;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "ISSUER", length = 255)
    private String issuer;

    @Column(name = "POLICY_SET_ID_REF", length = 255)
    private String policySetIdReferences;

    @Column(name = "POLICY_SET_DEFAULTS", length = 255)
    private String policySetDefaults;

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
    private Set<XACMLPolicyEntity> policies = new HashSet<XACMLPolicyEntity>();

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

    public String getPolicySetIdReferences() {
        return policySetIdReferences;
    }

    public void setPolicySetIdReferences(String policySetIdReferences) {
        this.policySetIdReferences = policySetIdReferences;
    }

    public String getPolicySetDefaults() {
        return policySetDefaults;
    }

    public void setPolicySetDefaults(String policySetDefaults) {
        this.policySetDefaults = policySetDefaults;
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

    public Set<XACMLPolicyEntity> getPolicies() {
        return policies;
    }

    public void setPolicies(Set<XACMLPolicyEntity> policies) {
        this.policies = policies;
    }
}
