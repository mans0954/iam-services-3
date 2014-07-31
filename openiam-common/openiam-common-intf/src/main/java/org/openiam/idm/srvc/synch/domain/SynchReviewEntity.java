package org.openiam.idm.srvc.synch.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.synch.dto.SynchReview;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "SYNCH_REVIEW")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(SynchReview.class)
public class SynchReviewEntity implements Serializable {

    private static final long serialVersionUID = -3470837715890623537L;

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid")
    @Column(name="SYNCH_REVIEW_ID", length=32, nullable=false)
    private String synchReviewId;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="SYNCH_CONFIG_ID", referencedColumnName="SYNCH_CONFIG_ID", insertable=true, updatable=false, nullable=false)
    private SynchConfigEntity synchConfig;

    @Column(name="CREATE_TIME", length=19, nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name="EXEC_TIME", length=19, nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date execTime;

    @Column(name="MODIFY_TIME", length=19, nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyTime;

    @Column(name="SRC_REJECTED")
    @Type(type = "yes_no")
    private boolean sourceRejected = false;

    @Column(name="SKIP_SRC_VALID")
    @Type(type = "yes_no")
    private boolean skipSourceValid = false;

    @Column(name="SKIP_REC_VALID")
    @Type(type = "yes_no")
    private boolean skipRecordValid = false;

    @OneToMany(mappedBy="synchReview", orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<SynchReviewRecordEntity> reviewRecords;

    public SynchReviewEntity() {}

    public SynchReviewEntity(SynchConfigEntity synchConfig, Date createTime) {
        this.synchConfig = synchConfig;
        this.createTime = createTime;
    }

    public String getSynchReviewId() {
        return synchReviewId;
    }

    public void setSynchReviewId(String synchReviewId) {
        this.synchReviewId = synchReviewId;
    }

    public SynchConfigEntity getSynchConfig() {
        return synchConfig;
    }

    public void setSynchConfig(SynchConfigEntity synchConfig) {
        this.synchConfig = synchConfig;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExecTime() {
        return execTime;
    }

    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public boolean isSourceRejected() {
        return sourceRejected;
    }

    public void setSourceRejected(boolean sourceRejected) {
        this.sourceRejected = sourceRejected;
    }

    public boolean isSkipSourceValid() {
        return skipSourceValid;
    }

    public void setSkipSourceValid(boolean skipSourceValid) {
        this.skipSourceValid = skipSourceValid;
    }

    public boolean isSkipRecordValid() {
        return skipRecordValid;
    }

    public void setSkipRecordValid(boolean skipRecordValid) {
        this.skipRecordValid = skipRecordValid;
    }

    public List<SynchReviewRecordEntity> getReviewRecords() {
        return reviewRecords;
    }

    public void setReviewRecords(List<SynchReviewRecordEntity> reviewRecords) {
        this.reviewRecords = reviewRecords;
    }

    public void addRecord(SynchReviewRecordEntity record) {
        if (record != null) {
            if (reviewRecords == null) {
                reviewRecords = new ArrayList<SynchReviewRecordEntity>();
            }
            record.setSynchReview(this);
            reviewRecords.add(record);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynchReviewEntity)) return false;

        SynchReviewEntity that = (SynchReviewEntity) o;

        if (skipRecordValid != that.skipRecordValid) return false;
        if (skipSourceValid != that.skipSourceValid) return false;
        if (sourceRejected != that.sourceRejected) return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) return false;
        if (execTime != null ? !execTime.equals(that.execTime) : that.execTime != null) return false;
        if (modifyTime != null ? !modifyTime.equals(that.modifyTime) : that.modifyTime != null) return false;
        if (synchConfig != null ? !synchConfig.equals(that.synchConfig) : that.synchConfig != null) return false;
        if (synchReviewId != null ? !synchReviewId.equals(that.synchReviewId) : that.synchReviewId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = synchReviewId != null ? synchReviewId.hashCode() : 0;
        result = 31 * result + (synchConfig != null ? synchConfig.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (execTime != null ? execTime.hashCode() : 0);
        result = 31 * result + (modifyTime != null ? modifyTime.hashCode() : 0);
        result = 31 * result + (sourceRejected ? 1 : 0);
        result = 31 * result + (skipSourceValid ? 1 : 0);
        result = 31 * result + (skipRecordValid ? 1 : 0);
        return result;
    }
}
