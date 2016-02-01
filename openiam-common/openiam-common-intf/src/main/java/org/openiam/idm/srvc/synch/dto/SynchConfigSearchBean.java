package org.openiam.idm.srvc.synch.dto;

import org.openiam.idm.searchbeans.AbstractSearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SynchConfigSearchBean", propOrder = {
        "name",
        "synchType",
        "excludeBooleanProperties"
})
public class SynchConfigSearchBean extends AbstractSearchBean<SynchConfig, String> {
    private String name;
    private String synchType;
    private boolean excludeBooleanProperties;

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

	public boolean isExcludeBooleanProperties() {
		return excludeBooleanProperties;
	}

	public void setExcludeBooleanProperties(boolean excludeBooleanProperties) {
		this.excludeBooleanProperties = excludeBooleanProperties;
	}

    
}