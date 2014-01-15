package org.openiam.idm.srvc.recon.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.dto.ReconExecStatusOptions;
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
    private boolean manualReconciliationFlag;

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

   /* @Column(name="LAST_EXEC_TIME",length=19)
    @Temporal(TemporalType.DATE)
    private java.util.Date lastExecTime;
    @Column(name="PROGRESS_STATUS",length=40)
    public String progressStatus;*/

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

    public ReconciliationConfigEntity(String reconConfigId, String resourceId,  String managedSysId,
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

    public boolean getManualReconciliationFlag() {
        return manualReconciliationFlag;
    }

    public void setManualReconciliationFlag(boolean manualReconciliationFlag) {
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
}