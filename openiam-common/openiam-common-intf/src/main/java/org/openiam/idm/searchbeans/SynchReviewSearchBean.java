package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.synch.dto.SynchReview;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SynchReviewSearchBean", propOrder = {
        "synchConfigId"
})
public class SynchReviewSearchBean extends AbstractSearchBean<SynchReview, String> implements SearchBean<SynchReview, String>,
        Serializable {

    private static final long serialVersionUID = 2028343104270477314L;

    private String synchConfigId;

    public String getSynchConfigId() {
        return synchConfigId;
    }

    public void setSynchConfigId(String synchConfigId) {
        this.synchConfigId = synchConfigId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((synchConfigId == null) ? 0 : synchConfigId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SynchReviewSearchBean other = (SynchReviewSearchBean) obj;
		if (synchConfigId == null) {
			if (other.synchConfigId != null)
				return false;
		} else if (!synchConfigId.equals(other.synchConfigId))
			return false;
		return true;
	}

    
}
