package org.openiam.idm.srvc.policy.domain;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ReconciliationResourceAttributeMapEntity;
import org.openiam.idm.srvc.policy.dto.Policy;

@Entity
@Table(name = "POLICY")
@DozerDTOCorrespondence(Policy.class)
public class PolicyEntity implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "POLICY_ID", length = 32)
    private String policyId;

    @Column(name = "POLICY_DEF_ID", length = 32)
    private String policyDefId;

    @Column(name = "NAME", length = 60)
    private String name;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_ID", insertable = true, updatable = true)
    private Set<PolicyAttributeEntity> policyAttributes = new HashSet<PolicyAttributeEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "attributePolicy")
    private Set<ReconciliationResourceAttributeMapEntity> attributeMaps = new HashSet<ReconciliationResourceAttributeMapEntity>(0);

    public PolicyEntity() {
    }

    public PolicyEntity(String policyId) {
        this.policyId = policyId;
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

    public Set<PolicyAttributeEntity> getPolicyAttributes() {
        return this.policyAttributes;
    }

    public void setPolicyAttributes(Set<PolicyAttributeEntity> policyAttributes) {
        this.policyAttributes = policyAttributes;
    }

    public Set<ReconciliationResourceAttributeMapEntity> getAttributeMaps() {
        return attributeMaps;
    }

    public void setAttributeMaps(Set<ReconciliationResourceAttributeMapEntity> attributeMaps) {
        this.attributeMaps = attributeMaps;
    }

    public PolicyAttributeEntity getAttribute(String name) {
        for (PolicyAttributeEntity attr : policyAttributes) {
            if (attr != null && attr.getDefaultParametr() != null && StringUtils.equalsIgnoreCase(attr.getDefaultParametr().getName(), name)) {
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
     * @param enablemement the enablemement to set
     */
    public void setEnablemement(Integer enablemement) {
        this.enablement = enablemement;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PolicyEntity [policyId=" + policyId + ", policyDefId="
                + policyDefId + ", name=" + name + ", description="
                + description + ", status=" + status + ", createDate="
                + createDate + ", createdBy=" + createdBy + ", lastUpdate="
                + lastUpdate + ", lastUpdatedBy=" + lastUpdatedBy + ", rule="
                + rule + ", ruleSrcUrl=" + ruleSrcUrl + ", enablement="
                + enablement + ", policyAttributes=" + policyAttributes + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PolicyEntity that = (PolicyEntity) o;

        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (enablement != null ? !enablement.equals(that.enablement) : that.enablement != null) return false;
        if (lastUpdate != null ? !lastUpdate.equals(that.lastUpdate) : that.lastUpdate != null) return false;
        if (lastUpdatedBy != null ? !lastUpdatedBy.equals(that.lastUpdatedBy) : that.lastUpdatedBy != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (policyDefId != null ? !policyDefId.equals(that.policyDefId) : that.policyDefId != null) return false;
        if (policyId != null ? !policyId.equals(that.policyId) : that.policyId != null) return false;
        if (rule != null ? !rule.equals(that.rule) : that.rule != null) return false;
        if (ruleSrcUrl != null ? !ruleSrcUrl.equals(that.ruleSrcUrl) : that.ruleSrcUrl != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
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
        return result;
    }
}
