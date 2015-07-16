package org.openiam.xacml.srvc.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.xacml.srvc.domain.XACMLPolicySetEntity;

import java.util.Set;

/**
 * Created by zaporozhec on 7/10/15.
 */
@JsonRootName(value = "PolicySet")
@DozerDTOCorrespondence(XACMLPolicySetEntity.class)
public class XACMLPolicySetDTO extends KeyDTO {

    private String identifier;
    private String version = "1.0";
    private String combinationAlgorithm;
    private Integer maxDelegationDepth;
    private String description;
    private String issuer;
    private String policySetIdReferences;
    private String policySetDefaults;
    private XACMLTargetDTO target;
    //    private XACMLObligationsEntity obligations;
//    private XACMLCombainerParamsEntity combainerParams;
    private Set<XACMLPolicyDTO> policies;


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

    public XACMLTargetDTO getTarget() {
        return target;
    }

    public void setTarget(XACMLTargetDTO target) {
        this.target = target;
    }

    public Set<XACMLPolicyDTO> getPolicies() {
        return policies;
    }

    public void setPolicies(Set<XACMLPolicyDTO> policies) {
        this.policies = policies;
    }
}
