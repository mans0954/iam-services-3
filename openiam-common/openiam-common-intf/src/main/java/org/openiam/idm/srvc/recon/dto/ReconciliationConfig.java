package org.openiam.idm.srvc.recon.dto;

import java.util.Date;
import java.util.Set;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReconciliationConfig", propOrder = {
        "resourceId", "managedSysId", "frequency", "status", "situationSet", "reportPath",
        "separator", "endOfLine", "notificationEmailAddress","manualReconciliationFlag",
        "targetSystemMatchScript","targetSystemSearchFilter","matchScript","searchFilter","updatedSince",
        "customIdentityMatchScript","scriptHandler","matchFieldName",
        "customMatchAttr","matchSrcFieldName","lastExecTime","execStatus","requesterId",
        "customProcessorScript","reconType","preProcessor","postProcessor"})

@DozerDTOCorrespondence(ReconciliationConfigEntity.class)
public class ReconciliationConfig extends KeyNameDTO implements MatchConfig {

    private static final long serialVersionUID = 431603790346613674L;
    private String resourceId;
    private String managedSysId;
    private String frequency;
    private String status;
    private String separator;
    private String endOfLine;
    private String notificationEmailAddress;
    //Target System Search Query Script
    private String targetSystemMatchScript;
    //Target System Search Query Filter
    private String targetSystemSearchFilter;
    //IDM Search Query Script
    private String matchScript;
    //IDM Search Query Filter
    private String searchFilter;
    //Updated Since
    @XmlSchemaType(name = "dateTime")
    private Date updatedSince;
    //Custom Rule for Matching
    private String customIdentityMatchScript;
    private Set<ReconciliationSituation> situationSet;
    private boolean manualReconciliationFlag;
    //IDM Repository Match Field
    private String matchFieldName;
    //Target System Match Attribute Name
    private String customMatchAttr;
    //IDM Custom Match Attribute Name (only if CUSTOM ATTRIBUTE type selected)
    private String matchSrcFieldName;
    @XmlElement
    private String scriptHandler;
    @XmlSchemaType(name = "dateTime")
    private Date lastExecTime;
    private ReconExecStatusOptions execStatus;

    private String requesterId;

    private String preProcessor;
    private String postProcessor;

    @XmlTransient
    private boolean useCustomScript;

    @Transient
    private String reportPath;

    private String customProcessorScript;

    private String reconType;

    public String getPreProcessor() {
        return preProcessor;
    }

    public void setPreProcessor(String preProcessor) {
        this.preProcessor = preProcessor;
    }

    public String getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(String postProcessor) {
        this.postProcessor = postProcessor;
    }

    public boolean getUseCustomScript() {
        return StringUtils.isNotEmpty(getCustomProcessorScript());
    }

    public void setUseCustomScript(boolean useCustomScript) {
        useCustomScript = useCustomScript;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
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

    public ReconciliationConfig() {
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

    public Set<ReconciliationSituation> getSituationSet() {
        return situationSet;
    }

    public void setSituationSet(Set<ReconciliationSituation> situationSet) {
        this.situationSet = situationSet;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
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

    public boolean getManualReconciliationFlag() {
        return manualReconciliationFlag;
    }

    public void setManualReconciliationFlag(boolean manualReconciliationFlag) {
        this.manualReconciliationFlag = manualReconciliationFlag;
    }

    public String getScriptHandler() {
        return scriptHandler;
    }

    public void setScriptHandler(String scriptHandler) {
        this.scriptHandler = scriptHandler;
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
		result = prime * result + (manualReconciliationFlag ? 1231 : 1237);
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
				+ ((reportPath == null) ? 0 : reportPath.hashCode());
		result = prime * result
				+ ((requesterId == null) ? 0 : requesterId.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result
				+ ((scriptHandler == null) ? 0 : scriptHandler.hashCode());
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
		result = prime * result + (useCustomScript ? 1231 : 1237);
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
		ReconciliationConfig other = (ReconciliationConfig) obj;
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
		if (manualReconciliationFlag != other.manualReconciliationFlag)
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
		if (reportPath == null) {
			if (other.reportPath != null)
				return false;
		} else if (!reportPath.equals(other.reportPath))
			return false;
		if (requesterId == null) {
			if (other.requesterId != null)
				return false;
		} else if (!requesterId.equals(other.requesterId))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (scriptHandler == null) {
			if (other.scriptHandler != null)
				return false;
		} else if (!scriptHandler.equals(other.scriptHandler))
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
		if (useCustomScript != other.useCustomScript)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReconciliationConfig [resourceId=" + resourceId
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
				+ ", scriptHandler=" + scriptHandler + ", lastExecTime="
				+ lastExecTime + ", execStatus=" + execStatus
				+ ", requesterId=" + requesterId + ", preProcessor="
				+ preProcessor + ", postProcessor=" + postProcessor
				+ ", useCustomScript=" + useCustomScript + ", reportPath="
				+ reportPath + ", customProcessorScript="
				+ customProcessorScript + ", reconType=" + reconType + "]";
	}

    
}