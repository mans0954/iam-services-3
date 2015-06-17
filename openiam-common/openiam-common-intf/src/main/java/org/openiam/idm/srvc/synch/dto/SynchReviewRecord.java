package org.openiam.idm.srvc.synch.dto;


import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SynchReviewRecord",
        propOrder = {
                "synchReviewRecordId",
                "synchReviewId",
                "header",
                "reviewValues"
        })
@DozerDTOCorrespondence(SynchReviewRecordEntity.class)
public class SynchReviewRecord implements Serializable {

    private static final long serialVersionUID = 445236639945638250L;

    private String synchReviewRecordId;
    private String synchReviewId;
    private boolean header = false;
    private List<SynchReviewRecordValue> reviewValues;

    public String getSynchReviewRecordId() {
        return synchReviewRecordId;
    }

    public void setSynchReviewRecordId(String synchReviewRecordId) {
        this.synchReviewRecordId = synchReviewRecordId;
    }

    public String getSynchReviewId() {
        return synchReviewId;
    }

    public void setSynchReviewId(String synchReviewId) {
        this.synchReviewId = synchReviewId;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public List<SynchReviewRecordValue> getReviewValues() {
        return reviewValues;
    }

    public void setReviewValues(List<SynchReviewRecordValue> reviewValues) {
        this.reviewValues = reviewValues;
    }

    public void addValue(SynchReviewRecordValue reviewValue) {
        if (reviewValues == null) {
            reviewValues = new ArrayList<SynchReviewRecordValue>();
        }
        reviewValues.add(reviewValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynchReviewRecord)) return false;

        SynchReviewRecord that = (SynchReviewRecord) o;

        if (header != that.header) return false;
        if (synchReviewId != null ? !synchReviewId.equals(that.synchReviewId) : that.synchReviewId != null)
            return false;
        return !(synchReviewRecordId != null ? !synchReviewRecordId.equals(that.synchReviewRecordId) : that.synchReviewRecordId != null);

    }

    @Override
    public int hashCode() {
        int result = synchReviewRecordId != null ? synchReviewRecordId.hashCode() : 0;
        result = 31 * result + (synchReviewId != null ? synchReviewId.hashCode() : 0);
        result = 31 * result + (header ? 1 : 0);
        return result;
    }
}
