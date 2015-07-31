package org.openiam.bpm.activiti.model;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.util.CustomJacksonMapper;

public class ActivitiJSONStringWrapper implements Serializable {

	private static transient final Log LOG = LogFactory.getLog(ActivitiJSONStringWrapper.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7392131240583652268L;
	
	private String value;
	
	public ActivitiJSONStringWrapper() {
		
	}
	
	public ActivitiJSONStringWrapper(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public <T extends Object> T getObject(final String key, final CustomJacksonMapper mapper, final Class<T> type) {
	    T retVal = null;
	    try {
	    	retVal = mapper.readValue(value, type);
	    } catch(Throwable e) {
	    	LOG.warn(String.format("Can't get variable '%s'", key), e);
	    }
	    return retVal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivitiJSONStringWrapper other = (ActivitiJSONStringWrapper) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("ActivitiJSONStringWrapper [value=%s]", value);
	}
	
	
}
