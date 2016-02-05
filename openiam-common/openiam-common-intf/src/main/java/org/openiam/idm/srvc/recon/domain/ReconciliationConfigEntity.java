package org.openiam.idm.srvc.recon.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.dto.ReconExecStatusOptions;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

@Entity
@Table(name = "RECONCILIATION_CONFIG")
@DozerDTOCorrespondence(ReconciliationConfig.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "RECON_CONFIG_ID", length = 32)),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 150))
})
public class ReconciliationConfigEntity extends AbstractKeyNameEntity {

    private static final long serialVersionUID = 431603790346613674L;

    @Column(name = "RESOURCE_ID", length = 32)
    private String resourceId;
    @Column(name = "MANAGED_SYS_ID", length = 32)
    private String managedSysId;
    @Column(name = "FREQUENCY", length = 20)
    private String frequency;
    @Column(name = "STATUS", length = 20)
    private String status;
    @Column(name = "CSV_LINE_SEPARATOR", length = 10)
    private String separator;
    @Column(name = "CSV_END_OF_LINE", length = 10)
    private String endOfLine;
    @Column(name = "NOTIFICATION_EMAIL_ADDRESS", length = 120)
    private String notificationEmailAddress;
    @Column(name = "TARGET_SYS_MATCH_SCRIPT", length = 120)
    private String targetSystemMatchScript;
    @Column(name = "TARGET_SYS_SEARCH_FILTER", length = 200)
    private String targetSystemSearchFilter;
    @Column(name = "MATCH_SCRIPT", length = 120)
    private String matchScript;
    @Column(name = "SEARCH_FILTER", length = 200)
    private String searchFilter;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_SINCE", length = 19)
    private Date updatedSince;
    @Column(name = "CUSTOM_IDENTITY_MATCH_SCRIPT", length = 120)
    private String customIdentityMatchScript;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "RECON_CONFIG_ID", insertable = false, updatable = false)
    private Set<ReconciliationSituationEntity> situationSet = new HashSet<ReconciliationSituationEntity>(0);

    @Column(name = "MANUAL_RECONCILIATION_FLAG")
    @Type(type = "yes_no")
    private Boolean manualReconciliationFlag;

    @Column(name="MATCH_FIELD_NAME",length=40)
    private String matchFieldName;
    @Column(name="CUSTOM_MATCH_ATTR",length=40)
    private String customMatchAttr;
    @Column(name="CUSTOM_MATCH_SRC_ATTR",length=40)
    private String matchSrcFieldName;

    @Column(name="LAST_EXEC_TIME",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date lastExecTime;
    @Column(name = "EXEC_STATUS", length = 20)
    @Enumerated(EnumType.STRING)
    private ReconExecStatusOptions execStatus;

    @Column(name = "RECON_CUSTOM_PROCESSOR", length = 255)
    private String customProcessorScript;

    @Column(name = "RECON_TYPE", length = 32)
    private String reconType;

    @Column(name = "POST_PROCESSOR", length = 150)
    private String postProcessor;

    @Column(name = "PRE_PROCESSOR", length = 150)
    private String preProcessor;

    public String getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(String postProcessor) {
        this.postProcessor = postProcessor;
    }

    public String getPreProcessor() {
        return preProcessor;
    }

    public void setPreProcessor(String preProcessor) {
        this.preProcessor = preProcessor;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getEndOfLine() {
        return endOfLine;
    }

    public void setEndOfLine(String endOfLine) {
        this.endOfLine = endOfLine;
    }

    public ReconciliationConfigEntity() {
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public String getFrequency() {
        return this.frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the notificationEmailAddress
     */
    public String getNotificationEmailAddress() {
        return notificationEmailAddress;
    }

    /**
     * @param notificationEmailAddress
     *            the notificationEmailAddress to set
     */
    public void setNotificationEmailAddress(String notificationEmailAddress) {
        this.notificationEmailAddress = notificationEmailAddress;
    }

    public String getTargetSystemMatchScript() {
        return targetSystemMatchScript;
    }

    public void setTargetSystemMatchScript(String targetSystemMatchScript) {
        this.targetSystemMatchScript = targetSystemMatchScript;
    }

    public String getTargetSystemSearchFilter() {
        return targetSystemSearchFilter;
    }

    public void setTargetSystemSearchFilter(String targetSystemSearchFilter) {
        this.targetSystemSearchFilter = targetSystemSearchFilter;
    }

    public String getMatchScript() {
        return matchScript;
    }

    public void setMatchScript(String matchScript) {
        this.matchScript = matchScript;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public Date getUpdatedSince() {
        return updatedSince;
    }

    public void setUpdatedSince(Date updatedSince) {
        this.updatedSince = updatedSince;
    }

    public Set<ReconciliationSituationEntity> getSituationSet() {
        return situationSet;
    }

    public void setSituationSet(Set<ReconciliationSituationEntity> situationSet) {
        this.situationSet = situationSet;
    }

    public Boolean getManualReconciliationFlag() {
        return manualReconciliationFlag;
    }

    public void setManualReconciliationFlag(Boolean manualReconciliationFlag) {
        this.manualReconciliationFlag = manualReconciliationFlag;
    }

    public String getCustomIdentityMatchScript() {
        return customIdentityMatchScript;
    }

    public void setCustomIdentityMatchScript(String customIdentityMatchScript) {
        this.customIdentityMatchScript = customIdentityMatchScript;
    }

    public String getMatchFieldName() {
        return matchFieldName;
    }

    public void setMatchFieldName(String matchFieldName) {
        this.matchFieldName = matchFieldName;
    }

    public String getCustomMatchAttr() {
        return customMatchAttr;
    }

    public void setCustomMatchAttr(String customMatchAttr) {
        this.customMatchAttr = customMatchAttr;
    }

    public String getMatchSrcFieldName() {
        return matchSrcFieldName;
    }

    public void setMatchSrcFieldName(String matchSrcFieldName) {
        this.matchSrcFieldName = matchSrcFieldName;
    }

    public Date getLastExecTime() {
        return lastExecTime;
    }

    public void setLastExecTime(Date lastExecTime) {
        this.lastExecTime = lastExecTime;
    }

    public ReconExecStatusOptions getExecStatus() {
        return execStatus;
    }

    public void setExecStatus(ReconExecStatusOptions execStatus) {
        this.execStatus = execStatus;
    }

    public String getCustomProcessorScript() {
        return customProcessorScript;
    }

    public void setCustomProcessorScript(String customProcessorScript) {
        this.customProcessorScript = customProcessorScript;
    }

    public String getReconType() {
        return reconType;
    }

    public void setReconType(String reconType) {
        this.reconType = reconType;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((customIdentityMatchScript == null) ? 0
						: customIdentityMatchScript.hashCode());
		result = prime * result
				+ ((customMatchAttr == null) ? 0 : customMatchAttr.hashCode());
		result = prime
				* result
				+ ((customProcessorScript == null) ? 0 : customProcessorScript
						.hashCode());
		result = prime * result
				+ ((endOfLine == null) ? 0 : endOfLine.hashCode());
		result = prime * result
				+ ((execStatus == null) ? 0 : execStatus.hashCode());
		result = prime * result
				+ ((frequency == null) ? 0 : frequency.hashCode());
		result = prime * result
				+ ((lastExecTime == null) ? 0 : lastExecTime.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime
				* result
				+ ((manualReconciliationFlag == null) ? 0
						: manualReconciliationFlag.hashCode());
		result = prime * result
				+ ((matchFieldName == null) ? 0 : matchFieldName.hashCode());
		result = prime * result
				+ ((matchScript == null) ? 0 : matchScript.hashCode());
		result = prime
				* result
				+ ((matchSrcFieldName == null) ? 0 : matchSrcFieldName
						.hashCode());
		result = prime
				* result
				+ ((notificationEmailAddress == null) ? 0
						: notificationEmailAddress.hashCode());
		result = prime * result
				+ ((postProcessor == null) ? 0 : postProcessor.hashCode());
		result = prime * result
				+ ((preProcessor == null) ? 0 : preProcessor.hashCode());
		result = prime * result
				+ ((reconType == null) ? 0 : reconType.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result
				+ ((searchFilter == null) ? 0 : searchFilter.hashCode());
		result = prime * result
				+ ((separator == null) ? 0 : separator.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime
				* result
				+ ((targetSystemMatchScript == null) ? 0
						: targetSystemMatchScript.hashCode());
		result = prime
				* result
				+ ((targetSystemSearchFilter == null) ? 0
						: targetSystemSearchFilter.hashCode());
		result = prime * result
				+ ((updatedSince == null) ? 0 : updatedSince.hashCode());
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
		ReconciliationConfigEntity other = (ReconciliationConfigEntity) obj;
		if (customIdentityMatchScript == null) {
			if (other.customIdentityMatchScript != null)
				return false;
		} else if (!customIdentityMatchScript
				.equals(other.customIdentityMatchScript))
			return false;
		if (customMatchAttr == null) {
			if (other.customMatchAttr != null)
				return false;
		} else if (!customMatchAttr.equals(other.customMatchAttr))
			return false;
		if (customProcessorScript == null) {
			if (other.customProcessorScript != null)
				return false;
		} else if (!customProcessorScript.equals(other.customProcessorScript))
			return false;
		if (endOfLine == null) {
			if (other.endOfLine != null)
				return false;
		} else if (!endOfLine.equals(other.endOfLine))
			return false;
		if (execStatus != other.execStatus)
			return false;
		if (frequency == null) {
			if (other.frequency != null)
				return false;
		} else if (!frequency.equals(other.frequency))
			return false;
		if (lastExecTime == null) {
			if (other.lastExecTime != null)
				return false;
		} else if (!lastExecTime.equals(other.lastExecTime))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (manualReconciliationFlag == null) {
			if (other.manualReconciliationFlag != null)
				return false;
		} else if (!manualReconciliationFlag
				.equals(other.manualReconciliationFlag))
			return false;
		if (matchFieldName == null) {
			if (other.matchFieldName != null)
				return false;
		} else if (!matchFieldName.equals(other.matchFieldName))
			return false;
		if (matchScript == null) {
			if (other.matchScript != null)
				return false;
		} else if (!matchScript.equals(other.matchScript))
			return false;
		if (matchSrcFieldName == null) {
			if (other.matchSrcFieldName != null)
				return false;
		} else if (!matchSrcFieldName.equals(other.matchSrcFieldName))
			return false;
		if (notificationEmailAddress == null) {
			if (other.notificationEmailAddress != null)
				return false;
		} else if (!notificationEmailAddress
				.equals(other.notificationEmailAddress))
			return false;
		if (postProcessor == null) {
			if (other.postProcessor != null)
				return false;
		} else if (!postProcessor.equals(other.postProcessor))
			return false;
		if (preProcessor == null) {
			if (other.preProcessor != null)
				return false;
		} else if (!preProcessor.equals(other.preProcessor))
			return false;
		if (reconType == null) {
			if (other.reconType != null)
				return false;
		} else if (!reconType.equals(other.reconType))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (searchFilter == null) {
			if (other.searchFilter != null)
				return false;
		} else if (!searchFilter.equals(other.searchFilter))
			return false;
		if (separator == null) {
			if (other.separator != null)
				return false;
		} else if (!separator.equals(other.separator))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (targetSystemMatchScript == null) {
			if (other.targetSystemMatchScript != null)
				return false;
		} else if (!targetSystemMatchScript
				.equals(other.targetSystemMatchScript))
			return false;
		if (targetSystemSearchFilter == null) {
			if (other.targetSystemSearchFilter != null)
				return false;
		} else if (!targetSystemSearchFilter
				.equals(other.targetSystemSearchFilter))
			return false;
		if (updatedSince == null) {
			if (other.updatedSince != null)
				return false;
		} else if (!updatedSince.equals(other.updatedSince))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReconciliationConfigEntity [resourceId=" + resourceId
				+ ", managedSysId=" + managedSysId + ", frequency=" + frequency
				+ ", status=" + status + ", separator=" + separator
				+ ", endOfLine=" + endOfLine + ", notificationEmailAddress="
				+ notificationEmailAddress + ", targetSystemMatchScript="
				+ targetSystemMatchScript + ", targetSystemSearchFilter="
				+ targetSystemSearchFilter + ", matchScript=" + matchScript
				+ ", searchFilter=" + searchFilter + ", updatedSince="
				+ updatedSince + ", customIdentityMatchScript="
				+ customIdentityMatchScript + ", manualReconciliationFlag="
				+ manualReconciliationFlag + ", matchFieldName="
				+ matchFieldName + ", customMatchAttr=" + customMatchAttr
				+ ", matchSrcFieldName=" + matchSrcFieldName
				+ ", lastExecTime=" + lastExecTime + ", execStatus="
				+ execStatus + ", customProcessorScript="
				+ customProcessorScript + ", reconType=" + reconType
				+ ", postProcessor=" + postProcessor + ", preProcessor="
				+ preProcessor + "]";
	}

    
}