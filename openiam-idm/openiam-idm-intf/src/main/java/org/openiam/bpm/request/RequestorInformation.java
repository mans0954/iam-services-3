package org.openiam.bpm.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.BaseObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestorInformation", propOrder = {
    "callerUserId"
})
public abstract class RequestorInformation extends BaseObject {

	
	private static final long serialVersionUID = 4447312981650177367L;

	protected String callerUserId;

	public String getCallerUserId() {
		return callerUserId;
	}
	
	public void setCallerUserId(String callerUserId) {
		this.callerUserId = callerUserId;
	}
}
