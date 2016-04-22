package org.openiam.idm.srvc.synch.dto;

import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.SearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SynchConfigSearchBean", propOrder = {
        "name",
        "synchType",
        "excludeBooleanProperties"
})
public class SynchConfigSearchBean extends AbstractSearchBean<SynchConfig, String> implements SearchBean {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (excludeBooleanProperties ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((synchType == null) ? 0 : synchType.hashCode());
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
		SynchConfigSearchBean other = (SynchConfigSearchBean) obj;
		if (excludeBooleanProperties != other.excludeBooleanProperties)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (synchType == null) {
			if (other.synchType != null)
				return false;
		} else if (!synchType.equals(other.synchType))
			return false;
		return true;
	}

	
}
