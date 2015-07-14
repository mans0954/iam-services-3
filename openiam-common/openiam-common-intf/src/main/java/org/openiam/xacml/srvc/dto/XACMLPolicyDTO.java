package org.openiam.xacml.srvc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by zaporozhec on 7/10/15.
 */
@JsonRootName(value = "Policy")
@DozerDTOCorrespondence(XACMLPolicyEntity.class)
public class XACMLPolicyDTO extends KeyDTO {


    private String identifier;
    private String version;
    private String combinationAlgorithm;
    private Integer maxDelegationDepth;
    private String description;
    private String issuer;
    private String policyIdReferences;
    private String policyDefaults;
    private XACMLTargetDTO target;

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

    public XACMLTargetDTO getTarget() {
        return target;
    }

    public void setTarget(XACMLTargetDTO target) {
        this.target = target;
    }
}
