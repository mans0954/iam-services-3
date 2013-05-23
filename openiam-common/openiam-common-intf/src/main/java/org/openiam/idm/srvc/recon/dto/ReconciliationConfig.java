package org.openiam.idm.srvc.recon.dto;

import java.util.Set;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReconciliationConfig", propOrder = { "reconConfigId",
        "resourceId", "frequency", "status", "situationSet", "reportPath",
        "separator", "endOfLine", "notificationEmailAddress",
        "targetSystemMatchScript" })
@DozerDTOCorrespondence(ReconciliationConfigEntity.class)
public class ReconciliationConfig implements java.io.Serializable {

    private static final long serialVersionUID = 431603790346613674L;
    private String reconConfigId;
    private String resourceId;
    private String frequency;
    private String status;
    private String separator;
    private String endOfLine;
    private String notificationEmailAddress;
    private String targetSystemMatchScript;
    private Set<ReconciliationSituation> situationSet;
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

    public ReconciliationConfig(String reconConfigId, String resourceId,
            String mode, String frequency, String status,
            Integer attributeLevelCheck, Integer updateChangedAttribute) {
        this.reconConfigId = reconConfigId;
        this.resourceId = resourceId;
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
}