package org.openiam.idm.srvc.policy.domain;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;

/**
 * @author zaporozhec
 */
@Entity
@Table(name = "POLICY_DEF_PARAM")
@DozerDTOCorrespondence(PolicyDefParam.class)
public class PolicyDefParamEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "DEF_PARAM_ID", length = 32)
	private String defParamId;

    @Column(name = "POLICY_DEF_ID", length = 32)
    private String policyDefId;

	@Column(name = "NAME", length = 60)
	private String name;

	@Column(name = "DESCRIPTION", length = 255)
    private String description;

	@Column(name = "OPERATION", length = 20)
    private String operation;

	@Column(name = "VALUE1", length = 255)
    private String value1;

	@Column(name = "VALUE2", length = 255)
    private String value2;

	@Column(name = "REPEATS")
    private Integer repeats;

	@Column(name = "POLICY_PARAM_HANDLER", length = 255)
    private String policyParamHandler;

	@Column(name = "HANDLER_LANGUAGE", length = 20)
    private String handlerLanguage;

	@Column(name = "PARAM_GROUP", length = 20)
    private String paramGroup;


	public PolicyDefParamEntity() {
	}

    public PolicyDefParamEntity(String defParamId) {
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
                ", value1='" + value1 + '\'' +
                ", value2='" + value2 + '\'' +
                ", repeats=" + repeats +
                ", policyParamHandler='" + policyParamHandler + '\'' +
                ", handlerLanguage='" + handlerLanguage + '\'' +
                ", paramGroup='" + paramGroup + 
                '}';
    }
}
