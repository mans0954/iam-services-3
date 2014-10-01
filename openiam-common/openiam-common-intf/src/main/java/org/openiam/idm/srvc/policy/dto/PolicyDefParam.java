package org.openiam.idm.srvc.policy.dto;


import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.PolicyDefParamEntity;

/**
 * PolicyDefParam represent the parameters of a policy definition.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyDefParam", propOrder = {
        "defParamId",
        "policyDefId",
        "name",
        "description",
        "operation",
        "repeats",
        "policyParamHandler",
        "handlerLanguage",
        "paramGroup"
})
@DozerDTOCorrespondence(PolicyDefParamEntity.class)
public class PolicyDefParam implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String defParamId;
    private String policyDefId;
    private String name;
    private String description;
    private String operation;
    private Integer repeats;
    private String policyParamHandler;
    private String handlerLanguage;
    private String paramGroup;


    public PolicyDefParam() {
    }

    public PolicyDefParam(String defParamId) {
        this.defParamId = defParamId;
    }

    public String getDefParamId() {
        return this.defParamId;
    }

    public void setDefParamId(String defParamId) {
        this.defParamId = defParamId;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Integer getRepeats() {
        return this.repeats;
    }

    public void setRepeats(Integer repeats) {
        this.repeats = repeats;
    }

    public String getPolicyParamHandler() {
        return this.policyParamHandler;
    }

    public void setPolicyParamHandler(String policyParamHandler) {
        this.policyParamHandler = policyParamHandler;
    }


    public String getParamGroup() {
        return paramGroup;
    }

    public void setParamGroup(String paramGroup) {
        this.paramGroup = paramGroup;
    }

    public String getPolicyDefId() {
        return policyDefId;
    }

    public void setPolicyDefId(String policyDefId) {
        this.policyDefId = policyDefId;
    }

    public String getHandlerLanguage() {
        return handlerLanguage;
    }

    public void setHandlerLanguage(String handlerLanguage) {
        this.handlerLanguage = handlerLanguage;
    }

    @Override
    public String toString() {
        return "PolicyDefParam{" +
                "defParamId='" + defParamId + '\'' +
                ", policyDefId='" + policyDefId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", operation='" + operation + '\'' +
                ", repeats=" + repeats +
                ", policyParamHandler='" + policyParamHandler + '\'' +
                ", handlerLanguage='" + handlerLanguage + '\'' +
                ", paramGroup='" + paramGroup + 
                '}';
    }
}
