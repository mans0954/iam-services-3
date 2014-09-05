package org.openiam.idm.srvc.synch.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.synch.dto.SynchReviewRecordValue;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SYNCH_REVIEW_RECORD_VALUE")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(SynchReviewRecordValue.class)
public class SynchReviewRecordValueEntity implements Serializable {

    private static final long serialVersionUID = 3370436114988104513L;

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid")
    @Column(name="SYNCH_REVIEW_RECORD_VALUE_ID", length=32, nullable=false)
    private String synchReviewRecordValueId;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="SYNCH_REVIEW_RECORD_ID", referencedColumnName="SYNCH_REVIEW_RECORD_ID", insertable=true, updatable=false, nullable=false)
    private SynchReviewRecordEntity synchReviewRecord;

    @Column(name="VALUE", length=4096, nullable=true)
    private String value;

    public String getSynchReviewRecordValueId() {
        return synchReviewRecordValueId;
    }

    public void setSynchReviewRecordValueId(String synchReviewRecordValueId) {
        this.synchReviewRecordValueId = synchReviewRecordValueId;
    }

    public SynchReviewRecordEntity getSynchReviewRecord() {
        return synchReviewRecord;
    }

    public void setSynchReviewRecord(SynchReviewRecordEntity synchReviewRecord) {
        this.synchReviewRecord = synchReviewRecord;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynchReviewRecordValueEntity)) return false;

        SynchReviewRecordValueEntity that = (SynchReviewRecordValueEntity) o;

        if (synchReviewRecord != null ? !synchReviewRecord.equals(that.synchReviewRecord) : that.synchReviewRecord != null)
            return false;
        if (synchReviewRecordValueId != null ? !synchReviewRecordValueId.equals(that.synchReviewRecordValueId) : that.synchReviewRecordValueId != null)
            return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = synchReviewRecordValueId != null ? synchReviewRecordValueId.hashCode() : 0;
        result = 31 * result + (synchReviewRecord != null ? synchReviewRecord.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
