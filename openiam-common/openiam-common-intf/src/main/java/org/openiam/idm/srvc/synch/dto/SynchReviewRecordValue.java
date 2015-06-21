package org.openiam.idm.srvc.synch.dto;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordValueEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SynchReviewRecordValue",
        propOrder = {
                "synchReviewRecordId",
                "value"
        })
@DozerDTOCorrespondence(SynchReviewRecordValueEntity.class)
public class SynchReviewRecordValue extends KeyDTO {

    private static final long serialVersionUID = -7099022108176721011L;

    private String synchReviewRecordId;
    private String value;

    public String getSynchReviewRecordId() {
        return synchReviewRecordId;
    }

    public void setSynchReviewRecordId(String synchReviewRecordId) {
        this.synchReviewRecordId = synchReviewRecordId;
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
        if (!(o instanceof SynchReviewRecordValue)) return false;

        SynchReviewRecordValue that = (SynchReviewRecordValue) o;

        if (synchReviewRecordId != null ? !synchReviewRecordId.equals(that.synchReviewRecordId) : that.synchReviewRecordId != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (synchReviewRecordId != null ? synchReviewRecordId.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
