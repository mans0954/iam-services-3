package org.openiam.idm.srvc.policy.domain;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.dto.Policy;

@Entity
@Table(name = "POLICY")
@DozerDTOCorrespondence(Policy.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "POLICY_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 60))
})
public class PolicyEntity extends AbstractKeyNameEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

	@Column(name = "POLICY_DEF_ID", length = 32)
	private String policyDefId;

	@Column(name = "DESCRIPTION", length = 255)
	private String description;

    @Column(name = "STATUS")
	private Integer status;

	@Column(name = "CREATE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column(name = "CREATED_BY", length = 20)
	private String createdBy;

	@Column(name = "LAST_UPDATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

    @Column(name = "LAST_UPDATED_BY", length = 20)
	private String lastUpdatedBy;

    @Column(name = "RULE_TEXT")
	private String rule;

    @Column(name = "RULE_SRC_URL", length = 80)
	private String ruleSrcUrl;

	@Column(name = "ENABLEMENT")
    private Integer enablement;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="policy", orphanRemoval=true)
	@Fetch(FetchMode.SUBSELECT)
	//@JoinColumn(name = "POLICY_ID", insertable = true, updatable = true)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<PolicyAttributeEntity> policyAttributes = new HashSet<PolicyAttributeEntity>(
			0);

    public PolicyEntity() {
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer enablement) {
        this.status = enablement;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Set<PolicyAttributeEntity> getPolicyAttributes() {
        return this.policyAttributes;
    }

    public void setPolicyAttributes(Set<PolicyAttributeEntity> policyAttributes) {
        this.policyAttributes = policyAttributes;
    }

    public PolicyAttributeEntity getAttribute(String name) {
        for (PolicyAttributeEntity attr : policyAttributes) {
        	if(attr != null && StringUtils.equalsIgnoreCase(attr.getName(), name)) {
                return attr;
            }
        }
        return null;
    }


    public String getPolicyDefId() {
        return policyDefId;
    }

    public void setPolicyDefId(String policyDefId) {
        this.policyDefId = policyDefId;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getRuleSrcUrl() {
        return ruleSrcUrl;
    }

    public void setRuleSrcUrl(String ruleSrcUrl) {
        this.ruleSrcUrl = ruleSrcUrl;
    }

	/**
	 * @return the enablemement
	 */
	public Integer getEnablemement() {
        return enablement;
	}

	/**
	 * @param enablemement
	 *            the enablemement to set
	 */
	public void setEnablemement(Integer enablemement) {
        this.enablement = enablemement;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((enablement == null) ? 0 : enablement.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result
				+ ((lastUpdatedBy == null) ? 0 : lastUpdatedBy.hashCode());
		result = prime * result
				+ ((policyDefId == null) ? 0 : policyDefId.hashCode());
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
		result = prime * result
				+ ((ruleSrcUrl == null) ? 0 : ruleSrcUrl.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolicyEntity other = (PolicyEntity) obj;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (enablement == null) {
			if (other.enablement != null)
				return false;
		} else if (!enablement.equals(other.enablement))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (lastUpdatedBy == null) {
			if (other.lastUpdatedBy != null)
				return false;
		} else if (!lastUpdatedBy.equals(other.lastUpdatedBy))
			return false;
		if (policyDefId == null) {
			if (other.policyDefId != null)
				return false;
		} else if (!policyDefId.equals(other.policyDefId))
			return false;
		if (rule == null) {
			if (other.rule != null)
				return false;
		} else if (!rule.equals(other.rule))
			return false;
		if (ruleSrcUrl == null) {
			if (other.ruleSrcUrl != null)
				return false;
		} else if (!ruleSrcUrl.equals(other.ruleSrcUrl))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("PolicyEntity [policyDefId=%s, description=%s, status=%s, createDate=%s, createdBy=%s, lastUpdate=%s, lastUpdatedBy=%s, rule=%s, ruleSrcUrl=%s, enablement=%s, toString()=%s]",
						policyDefId, description, status, createDate,
						createdBy, lastUpdate, lastUpdatedBy, rule, ruleSrcUrl,
						enablement, super.toString());
	}

	
}
