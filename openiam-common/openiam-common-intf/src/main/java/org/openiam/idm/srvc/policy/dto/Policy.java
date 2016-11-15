package org.openiam.idm.srvc.policy.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;

/**
 * <code>Policy</code> represents a policy object that is used by the policy service.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Policy", propOrder = {
	"policyDefId",
	"description", 
	"status", 
	"createDate", 
	"createdBy", 
	"lastUpdate",
	"lastUpdatedBy", 
	"rule", 
	"ruleSrcUrl", 
	"policyAttributes",
	"active",
	"priority"
})
@DozerDTOCorrespondence(PolicyEntity.class)
public class Policy extends KeyNameDTO {

    private static final long serialVersionUID = 5733143745301294956L;
    private String policyDefId;
    private String description;
    private Integer status;
    @XmlSchemaType(name = "dateTime")
    private Date createDate;
    private String createdBy;
    @XmlSchemaType(name = "dateTime")
    private Date lastUpdate;
    private String lastUpdatedBy;
    private String rule;
    private String ruleSrcUrl;
    private boolean active;
    private Integer priority;

    private Set<PolicyAttribute> policyAttributes = new HashSet<PolicyAttribute>(
            0);

    public Policy() {
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

    public Set<PolicyAttribute> getPolicyAttributes() {
        return this.policyAttributes;
    }
    
    public void addPolicyAttribute(final PolicyAttribute attribute) {
    	if(attribute != null) {
    		if(this.policyAttributes == null) {
    			this.policyAttributes = new HashSet<>();
    		}
    		this.policyAttributes.add(attribute);
    	}
    }

    public void setPolicyAttributes(Set<PolicyAttribute> policyAttributes) {
        this.policyAttributes = policyAttributes;
    }

    public PolicyAttribute getAttribute(String name) {
        for (PolicyAttribute attr : policyAttributes) {
        	if(StringUtils.equalsIgnoreCase(attr.getName(), name)) {
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result
				+ ((lastUpdatedBy == null) ? 0 : lastUpdatedBy.hashCode());
		result = prime * result
				+ ((policyDefId == null) ? 0 : policyDefId.hashCode());
		result = prime * result
				+ ((priority == null) ? 0 : priority.hashCode());
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
		Policy other = (Policy) obj;
		if (active != other.active)
			return false;
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
		if (priority == null) {
			if (other.priority != null)
				return false;
		} else if (!priority.equals(other.priority))
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

	
}
