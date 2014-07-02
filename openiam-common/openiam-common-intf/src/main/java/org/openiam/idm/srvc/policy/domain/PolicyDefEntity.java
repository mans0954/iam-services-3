package org.openiam.idm.srvc.policy.domain;

// Generated Mar 7, 2009 11:47:12 AM by Hibernate Tools 3.2.2.GA

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.dto.PolicyDef;

/**
 * PolicyDef represent a policy definition
 */
@Entity
@Table(name = "POLICY_DEF")
@DozerDTOCorrespondence(PolicyDef.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PolicyDefEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "POLICY_DEF_ID", length = 32)
	private String policyDefId;
	@Column(name = "NAME", length = 60)
	private String name;
	@Column(name = "DESCRIPTION", length = 255)
    private String description;
	@Column(name = "POLICY_TYPE", length = 20)
    private String policyType;
	@Column(name = "LOCATION_TYPE", length = 20)
    private String locationType;
	@Column(name = "POLICY_RULE", length = 500)
    private String policyRule;
	@Column(name = "POLICY_HANDLER", length = 255)
    private String policyHandler;
	@Column(name = "POLICY_ADVISE_HANDLER", length = 255)
    private String policyAdviseHandler;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "POLICY_DEF_ID", insertable = false, updatable = false)
    private Set<PolicyDefParamEntity> policyDefParams = new HashSet<PolicyDefParamEntity>(0);


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_DEF_ID", insertable = false, updatable = false)
    private Set<PolicyEntity> policies = new HashSet<PolicyEntity>(0);

    public PolicyDefEntity() {
    }

    public PolicyDefEntity(String policyDefId) {
        this.policyDefId = policyDefId;
    }

    public String getPolicyDefId() {
        return this.policyDefId;
    }

    public void setPolicyDefId(String policyDefId) {
        this.policyDefId = policyDefId;
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

    public String getPolicyType() {
        return this.policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getLocationType() {
        return this.locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getPolicyRule() {
        return this.policyRule;
    }

    public void setPolicyRule(String policyRule) {
        this.policyRule = policyRule;
    }

    public String getPolicyHandler() {
        return this.policyHandler;
    }

    public void setPolicyHandler(String policyHandler) {
        this.policyHandler = policyHandler;
    }

    public String getPolicyAdviseHandler() {
        return this.policyAdviseHandler;
    }

    public void setPolicyAdviseHandler(String policyAdviseHandler) {
        this.policyAdviseHandler = policyAdviseHandler;
    }

    public Set<PolicyDefParamEntity> getPolicyDefParams() {
        return this.policyDefParams;
    }

    public void setPolicyDefParams(Set<PolicyDefParamEntity> policyDefParams) {
        this.policyDefParams = policyDefParams;
    }

    public Set<PolicyEntity> getPolicies() {
        return this.policies;
    }

    public void setPolicies(Set<PolicyEntity> policies) {
        this.policies = policies;
    }

    @Override
    public String toString() {
        return "PolicyDef{" +
                "policyDefId='" + policyDefId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", policyType='" + policyType + '\'' +
                ", locationType='" + locationType + '\'' +
                ", policyRule='" + policyRule + '\'' +
                ", policyHandler='" + policyHandler + '\'' +
                ", policyAdviseHandler='" + policyAdviseHandler + '\'' +
                ", policyDefParams=" + policyDefParams +
                ", policies=" + policies +
                '}';
    }
}
