package org.openiam.idm.srvc.synch.domain;

import org.hibernate.annotations.*;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.synch.dto.SynchReviewRecord;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SYNCH_REVIEW_RECORD")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(SynchReviewRecord.class)
public class SynchReviewRecordEntity implements Serializable {

    private static final long serialVersionUID = -1800643505329798408L;

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid")
    @Column(name="SYNCH_REVIEW_RECORD_ID", length=32, nullable=false)
    private String synchReviewRecordId;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="SYNCH_REVIEW_ID", referencedColumnName="SYNCH_REVIEW_ID", insertable=true, updatable=false, nullable=false)
    private SynchReviewEntity synchReview;

    @Column(name="HEADER")
    @Type(type = "yes_no")
    private boolean header = false;

    @OneToMany(mappedBy="synchReviewRecord", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<SynchReviewRecordValueEntity> reviewValues;

    public String getSynchReviewRecordId() {
        return synchReviewRecordId;
    }

    public void setSynchReviewRecordId(String synchReviewValueId) {
        this.synchReviewRecordId = synchReviewValueId;
    }

    public SynchReviewEntity getSynchReview() {
        return synchReview;
    }

    public void setSynchReview(SynchReviewEntity synchReview) {
        this.synchReview = synchReview;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public List<SynchReviewRecordValueEntity> getReviewValues() {
        return reviewValues;
    }

    public void setReviewValues(List<SynchReviewRecordValueEntity> reviewValues) {
        this.reviewValues = reviewValues;
    }

    public void addValue(SynchReviewRecordValueEntity value) {
        if (value != null) {
            if (reviewValues == null) {
                reviewValues = new ArrayList<SynchReviewRecordValueEntity>();
            }
            value.setSynchReviewRecord(this);
            reviewValues.add(value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynchReviewRecordEntity)) return false;

        SynchReviewRecordEntity that = (SynchReviewRecordEntity) o;

        if (header != that.header) return false;
        if (synchReview != null ? !synchReview.equals(that.synchReview) : that.synchReview != null) return false;
        if (synchReviewRecordId != null ? !synchReviewRecordId.equals(that.synchReviewRecordId) : that.synchReviewRecordId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = synchReviewRecordId != null ? synchReviewRecordId.hashCode() : 0;
        result = 31 * result + (synchReview != null ? synchReview.hashCode() : 0);
        result = 31 * result + (header ? 1 : 0);
        return result;
    }
}
