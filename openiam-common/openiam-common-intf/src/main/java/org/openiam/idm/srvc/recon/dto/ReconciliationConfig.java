package org.openiam.idm.srvc.recon.dto;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.xml.bind.annotation.*;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.springframework.format.annotation.DateTimeFormat;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReconciliationConfig", propOrder = { "reconConfigId",
        "resourceId", "managedSysId", "frequency", "status", "situationSet", "reportPath",
        "separator", "endOfLine", "notificationEmailAddress","manualReconciliationFlag",
        "targetSystemMatchScript","targetSystemSearchFilter","matchScript","searchFilter","updatedSince",
        "customIdentityMatchScript","scriptHandler","matchFieldName",
        "customMatchAttr","matchSrcFieldName","lastExecTime","execStatus"})

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

    @Transient
    private String reportPath;

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

    public ReconciliationConfig(String reconConfigId, String resourceId, String managedSysId,
            String mode, String frequency, String status,
            Integer attributeLevelCheck, Integer updateChangedAttribute) {
        this.reconConfigId = reconConfigId;
        this.resourceId = resourceId;
        this.managedSysId = managedSysId;
        this.frequency = frequency;
        this.status = status;

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
}