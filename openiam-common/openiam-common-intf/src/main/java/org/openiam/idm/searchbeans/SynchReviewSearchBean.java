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

}
