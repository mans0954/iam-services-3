package org.openiam.provision.dto.srcadapter;

import org.openiam.base.ws.ResponseStatus;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = {"notes"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterInfoResponse {

    @XmlElementWrapper(name = "notes-set")
    @XmlElements({
            @XmlElement(name = "note")}
    )
    private List<String> notes;

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
}
