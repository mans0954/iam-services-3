package org.openiam.idm.srvc.recon.dto;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReconciliationConfig", propOrder = { "reconConfigId",
        "resourceId", "managedSysId", "frequency", "status", "situationSet", "reportPath",
        "separator", "endOfLine", "notificationEmailAddress",
        "manualReconciliationFlag","targetSystemMatchScript",
        "customIdentityMatchScript",
        "scriptHandler",
        "matchFieldName",
        "customMatchAttr",
        "matchSrcFieldName"})

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
    private String targetSystemMatchScript;
    private String customIdentityMatchScript;
    private Set<ReconciliationSituation> situationSet;
    private boolean manualReconciliationFlag;
    private String matchFieldName;
    private String customMatchAttr;
    private String matchSrcFieldName;
    @XmlElement
    private String scriptHandler;

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
}