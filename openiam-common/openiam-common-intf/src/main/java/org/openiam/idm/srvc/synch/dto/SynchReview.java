package org.openiam.idm.srvc.synch.dto;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SynchReview",
        propOrder = {
                "synchConfigId",
                "createTime",
                "execTime",
                "modifyTime",
                "sourceRejected",
                "skipSourceValid",
                "skipRecordValid",
                "reviewRecords"
        })
@DozerDTOCorrespondence(SynchReviewEntity.class)
public class SynchReview extends KeyDTO {

    private static final long serialVersionUID = 4894570727605523853L;

    private String synchConfigId;
    @XmlSchemaType(name = "dateTime")
    private Date createTime;
    @XmlSchemaType(name = "dateTime")
    private Date execTime;
    @XmlSchemaType(name = "dateTime")
    private Date modifyTime;
    private boolean sourceRejected = false;
    private boolean skipSourceValid = false;
    private boolean skipRecordValid = false;
    private List<SynchReviewRecord> reviewRecords;

    public SynchReview() {}

    public SynchReview(String synchConfigId, Date createTime) {
        this.synchConfigId = synchConfigId;
        this.createTime = createTime;
    }

    public SynchReview(boolean sourceRejected) {
        this.sourceRejected = sourceRejected;
    }

    public String getSynchConfigId() {
        return synchConfigId;
    }

    public void setSynchConfigId(String synchConfigId) {
        this.synchConfigId = synchConfigId;
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

    public List<SynchReviewRecord> getReviewRecords() {
        return reviewRecords;
    }

    public void setReviewRecords(List<SynchReviewRecord> reviewRecords) {
        this.reviewRecords = reviewRecords;
    }

    public void addRecord(SynchReviewRecord record) {
        if (reviewRecords == null) {
            reviewRecords = new ArrayList<SynchReviewRecord>();
        }
        reviewRecords.add(record);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynchReview)) return false;

        SynchReview that = (SynchReview) o;

        if (skipRecordValid != that.skipRecordValid) return false;
        if (skipSourceValid != that.skipSourceValid) return false;
        if (sourceRejected != that.sourceRejected) return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) return false;
        if (execTime != null ? !execTime.equals(that.execTime) : that.execTime != null) return false;
        if (modifyTime != null ? !modifyTime.equals(that.modifyTime) : that.modifyTime != null) return false;
        if (synchConfigId != null ? !synchConfigId.equals(that.synchConfigId) : that.synchConfigId != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (synchConfigId != null ? synchConfigId.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (execTime != null ? execTime.hashCode() : 0);
        result = 31 * result + (modifyTime != null ? modifyTime.hashCode() : 0);
        result = 31 * result + (sourceRejected ? 1 : 0);
        result = 31 * result + (skipSourceValid ? 1 : 0);
        result = 31 * result + (skipRecordValid ? 1 : 0);
        return result;
    }
}
