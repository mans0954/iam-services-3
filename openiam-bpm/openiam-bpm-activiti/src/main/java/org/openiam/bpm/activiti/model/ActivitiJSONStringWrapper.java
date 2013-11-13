package org.openiam.bpm.activiti.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.openiam.idm.util.CustomJacksonMapper;

public class ActivitiJSONStringWrapper implements Serializable {

	private static transient final Logger LOG = Logger.getLogger(ActivitiJSONStringWrapper.class);
	
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
}
