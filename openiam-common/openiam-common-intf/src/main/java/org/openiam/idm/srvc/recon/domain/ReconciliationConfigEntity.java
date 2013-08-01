package org.openiam.idm.srvc.recon.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

@Entity
@Table(name = "RECONCILIATION_CONFIG")
@DozerDTOCorrespondence(ReconciliationConfig.class)
public class ReconciliationConfigEntity implements java.io.Serializable {

    private static final long serialVersionUID = 431603790346613674L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "RECON_CONFIG_ID", length = 32)
    private String reconConfigId;
    @Column(name = "RESOURCE_ID", length = 32)
    private String resourceId;
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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "RECON_CONFIG_ID", insertable = false, updatable = false)
    private List<ReconciliationSituationEntity> situationSet = new ArrayList<ReconciliationSituationEntity>(
            0);

    @Column(name = "MANUAL_RECONCILIATION_FLAG")
    @Type(type = "yes_no")
    private boolean manualReconciliationFlag;

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

    public ReconciliationConfigEntity(String reconConfigId) {
        this.reconConfigId = reconConfigId;
    }

    public ReconciliationConfigEntity(String reconConfigId, String resourceId,
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

    // public Set<ReconciliationSituation> getSituationSet() {
    // return situationSet;
    // }
    //
    // public void setSituationSet(Set<ReconciliationSituation> situationSet) {
    // this.situationSet = situationSet;
    // }

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

    public List<ReconciliationSituationEntity> getSituationSet() {
        return situationSet;
    }

    public void setSituationSet(List<ReconciliationSituationEntity> situationSet) {
        this.situationSet = situationSet;
    }

    public boolean getManualReconciliationFlag() {
        return manualReconciliationFlag;
    }

    public void setManualReconciliationFlag(boolean manualReconciliationFlag) {
        this.manualReconciliationFlag = manualReconciliationFlag;
    }
}