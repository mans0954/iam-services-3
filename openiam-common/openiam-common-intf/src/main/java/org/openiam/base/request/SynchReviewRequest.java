package org.openiam.base.request;

import org.openiam.idm.srvc.synch.dto.SynchReviewRecord;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SynchReviewRequest", propOrder = {
        "synchReviewId",
        "sourceRejected",
        "skipSourceValid",
        "skipRecordValid",
        "reviewRecords"
})
public class SynchReviewRequest {
    private String synchReviewId;
    private boolean sourceRejected;
    private boolean skipSourceValid;
    private boolean skipRecordValid;
    private List<SynchReviewRecord> reviewRecords;

    public String getSynchReviewId() {
        return synchReviewId;
    }

    public void setSynchReviewId(String synchReviewId) {
        this.synchReviewId = synchReviewId;
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
}
