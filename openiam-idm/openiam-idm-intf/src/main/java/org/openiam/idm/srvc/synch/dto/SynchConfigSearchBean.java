package org.openiam.idm.srvc.synch.dto;

import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SynchConfigSearchBean", propOrder = {
        "name",
        "synchType"
})
public class SynchConfigSearchBean extends AbstractSearchBean<SynchConfig, String> {
    private String name;
    private String synchType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSynchType() {
        return synchType;
    }

    public void setSynchType(String synchType) {
        this.synchType = synchType;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(name != null ? name : "")
                .append(synchType != null ? synchType : "")
                .append(getKey() != null ? getKey() : "")
                .toString();
    }
}
