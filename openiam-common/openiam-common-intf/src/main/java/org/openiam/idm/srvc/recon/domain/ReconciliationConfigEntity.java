package org.openiam.idm.srvc.recon.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Size;

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

    @Column(name = "RECON_CUSTOM_PROCESSOR", length = 255)
    private String customProcessorScript;

    @Column(name = "RECON_TYPE", length = 32)
    private String reconType;

    @Column(name = "NAME", length = 150)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

        ReconciliationConfigEntity that = (ReconciliationConfigEntity) o;
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
        if (targetSystemSearchFilter != null ? !targetSystemSearchFilter.equals(that.targetSystemSearchFilter) : that.targetSystemSearchFilter != null)
            return false;

        return true;
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
        sb.append("ReconciliationConfigEntity");
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