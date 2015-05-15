package org.openiam.idm.srvc.policy.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;

/**
 * <code>Policy</code> represents a policy object that is used by the policy service.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Policy", propOrder = { "policyId", "policyDefId", "name",
        "description", "status", "createDate", "createdBy", "lastUpdate",
        "lastUpdatedBy", "rule", "ruleSrcUrl", "policyAttributes", "enablement" })
@DozerDTOCorrespondence(PolicyEntity.class)
public class Policy implements java.io.Serializable {

    private static final long serialVersionUID = 5733143745301294956L;
    private String policyId;
    private String policyDefId;
    private String name;
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
    private Integer enablement;

    private Set<PolicyAttribute> policyAttributes = new HashSet<PolicyAttribute>(
            0);

    public Policy() {
    }

    public String getPolicyId() {
        return this.policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
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

    /**
     * @return the enablemement
     */
    public Integer getEnablement() {
        return enablement;
    }

    /**
     * @param enablemement
     *            the enablemement to set
     */
    public void setEnablement(Integer enablemement) {
        this.enablement = enablemement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Policy [policyId=" + policyId + ", policyDefId=" + policyDefId
                + ", name=" + name + ", description=" + description
                + ", status=" + status + ", createDate=" + createDate
                + ", createdBy=" + createdBy + ", lastUpdate=" + lastUpdate
                + ", lastUpdatedBy=" + lastUpdatedBy + ", rule=" + rule
                + ", ruleSrcUrl=" + ruleSrcUrl + ", enablement=" + enablement
                + ", policyAttributes=" + policyAttributes + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Policy policy = (Policy) o;

        if (policyId != null ? !policyId.equals(policy.policyId) : policy.policyId != null) return false;
        if (policyDefId != null ? !policyDefId.equals(policy.policyDefId) : policy.policyDefId != null) return false;
        if (name != null ? !name.equals(policy.name) : policy.name != null) return false;
        if (description != null ? !description.equals(policy.description) : policy.description != null) return false;
        if (status != null ? !status.equals(policy.status) : policy.status != null) return false;
        if (createDate != null ? !createDate.equals(policy.createDate) : policy.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(policy.createdBy) : policy.createdBy != null) return false;
        if (lastUpdate != null ? !lastUpdate.equals(policy.lastUpdate) : policy.lastUpdate != null) return false;
        if (lastUpdatedBy != null ? !lastUpdatedBy.equals(policy.lastUpdatedBy) : policy.lastUpdatedBy != null)
            return false;
        if (rule != null ? !rule.equals(policy.rule) : policy.rule != null) return false;
        if (ruleSrcUrl != null ? !ruleSrcUrl.equals(policy.ruleSrcUrl) : policy.ruleSrcUrl != null) return false;
        if (enablement != null ? !enablement.equals(policy.enablement) : policy.enablement != null) return false;
        return !(policyAttributes != null ? !policyAttributes.equals(policy.policyAttributes) : policy.policyAttributes != null);

    }

    @Override
    public int hashCode() {
        int result = policyId != null ? policyId.hashCode() : 0;
        result = 31 * result + (policyDefId != null ? policyDefId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (lastUpdatedBy != null ? lastUpdatedBy.hashCode() : 0);
        result = 31 * result + (rule != null ? rule.hashCode() : 0);
        result = 31 * result + (ruleSrcUrl != null ? ruleSrcUrl.hashCode() : 0);
        result = 31 * result + (enablement != null ? enablement.hashCode() : 0);
        result = 31 * result + (policyAttributes != null ? policyAttributes.hashCode() : 0);
        return result;
    }
}
