package org.openiam.idm.srvc.recon.domain;

import java.util.HashSet;
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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;

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

    // @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // @JoinColumn(name = "RECON_CONFIG_ID", referencedColumnName =
    // "RECON_CONFIG_ID")
    // private Set<ReconciliationSituationEntity> situationSet;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "RECON_CONFIG_ID", insertable = false, updatable = false)
    private Set<ReconciliationSituationEntity> situationSet = new HashSet<ReconciliationSituationEntity>(
            0);

    
    
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

    public Set<ReconciliationSituationEntity> getSituationSet() {
        return situationSet;
    }

    public void setSituationSet(Set<ReconciliationSituationEntity> situationSet) {
        this.situationSet = situationSet;
    }
}