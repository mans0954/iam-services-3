package org.openiam.idm.srvc.recon.dto;

import java.util.Date;
import java.util.Set;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang.StringUtils;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReconciliationConfig", propOrder = { "reconConfigId",
        "resourceId", "managedSysId", "frequency", "status", "situationSet", "reportPath",
        "separator", "endOfLine", "notificationEmailAddress","manualReconciliationFlag",
        "targetSystemMatchScript","targetSystemSearchFilter","matchScript","searchFilter","updatedSince",
        "customIdentityMatchScript","scriptHandler","matchFieldName",
        "customMatchAttr","matchSrcFieldName","lastExecTime","execStatus","requesterId",
        "customProcessorScript","reconType", "name","preProcessor","postProcessor"})

@DozerDTOCorrespondence(ReconciliationConfigEntity.class)
public class ReconciliationConfig implements MatchConfig, java.io.Serializable {

    private static final long serialVersionUID = 431603790346613674L;
    private String reconConfigId;
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
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ReconciliationConfig(String reconConfigId) {
        this.reconConfigId = reconConfigId;
    }

    public ReconciliationConfig(String name,
                                String reconConfigId, String resourceId, String managedSysId,
                                String mode, String frequency, String status,
                                Integer attributeLevelCheck, Integer updateChangedAttribute,
                                String reconType) {
        this.reconConfigId = reconConfigId;
        this.resourceId = resourceId;
        this.managedSysId = managedSysId;
        this.frequency = frequency;
        this.status = status;
        this.name = name;
    }

    public String getReconConfigId() {
        return this.reconConfigId;
    }

    public void setReconConfigId(String reconConfigId) {
        this.reconConfigId = reconConfigId;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReconciliationConfig that = (ReconciliationConfig) o;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (customIdentityMatchScript != null ? !customIdentityMatchScript.equals(that.customIdentityMatchScript) : that.customIdentityMatchScript != null)
            return false;
        if (customMatchAttr != null ? !customMatchAttr.equals(that.customMatchAttr) : that.customMatchAttr != null)
            return false;
        if (customProcessorScript != null ? !customProcessorScript.equals(that.customProcessorScript) : that.customProcessorScript != null)
            return false;
        if (managedSysId != null ? !managedSysId.equals(that.managedSysId) : that.managedSysId != null) return false;
        if (matchFieldName != null ? !matchFieldName.equals(that.matchFieldName) : that.matchFieldName != null)
            return false;
        if (matchScript != null ? !matchScript.equals(that.matchScript) : that.matchScript != null) return false;
        if (matchSrcFieldName != null ? !matchSrcFieldName.equals(that.matchSrcFieldName) : that.matchSrcFieldName != null)
            return false;
        if (reconConfigId != null ? !reconConfigId.equals(that.reconConfigId) : that.reconConfigId != null)
            return false;
        if (reconType != null ? !reconType.equals(that.reconType) : that.reconType != null) return false;
        if (resourceId != null ? !resourceId.equals(that.resourceId) : that.resourceId != null) return false;
        if (searchFilter != null ? !searchFilter.equals(that.searchFilter) : that.searchFilter != null) return false;
        if (targetSystemMatchScript != null ? !targetSystemMatchScript.equals(that.targetSystemMatchScript) : that.targetSystemMatchScript != null)
            return false;
        return !(targetSystemSearchFilter != null ? !targetSystemSearchFilter.equals(that.targetSystemSearchFilter) : that.targetSystemSearchFilter != null);

    }

    @Override
    public int hashCode() {
        int result = reconConfigId != null ? reconConfigId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (targetSystemMatchScript != null ? targetSystemMatchScript.hashCode() : 0);
        result = 31 * result + (targetSystemSearchFilter != null ? targetSystemSearchFilter.hashCode() : 0);
        result = 31 * result + (matchScript != null ? matchScript.hashCode() : 0);
        result = 31 * result + (searchFilter != null ? searchFilter.hashCode() : 0);
        result = 31 * result + (customIdentityMatchScript != null ? customIdentityMatchScript.hashCode() : 0);
        result = 31 * result + (matchFieldName != null ? matchFieldName.hashCode() : 0);
        result = 31 * result + (customMatchAttr != null ? customMatchAttr.hashCode() : 0);
        result = 31 * result + (matchSrcFieldName != null ? matchSrcFieldName.hashCode() : 0);
        result = 31 * result + (customProcessorScript != null ? customProcessorScript.hashCode() : 0);
        result = 31 * result + (reconType != null ? reconType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ReconciliationConfig");
        sb.append("{reconType='").append(reconType).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", customProcessorScript='").append(customProcessorScript).append('\'');
        sb.append(", reconConfigId='").append(reconConfigId).append('\'');
        sb.append(", resourceId='").append(resourceId).append('\'');
        sb.append(", managedSysId='").append(managedSysId).append('\'');
        sb.append(", targetSystemMatchScript='").append(targetSystemMatchScript).append('\'');
        sb.append(", targetSystemSearchFilter='").append(targetSystemSearchFilter).append('\'');
        sb.append(", matchScript='").append(matchScript).append('\'');
        sb.append(", searchFilter='").append(searchFilter).append('\'');
        sb.append(", updatedSince=").append(updatedSince);
        sb.append(", customIdentityMatchScript='").append(customIdentityMatchScript).append('\'');
        sb.append(", matchFieldName='").append(matchFieldName).append('\'');
        sb.append(", customMatchAttr='").append(customMatchAttr).append('\'');
        sb.append(", matchSrcFieldName='").append(matchSrcFieldName).append('\'');
        sb.append(", lastExecTime=").append(lastExecTime);
        sb.append(", execStatus=").append(execStatus);
        sb.append('}');
        return sb.toString();
    }
}