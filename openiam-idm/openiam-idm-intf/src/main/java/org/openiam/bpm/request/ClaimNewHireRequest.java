package org.openiam.bpm.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClaimNewHireRequest", propOrder = {
    "taskId",
    "requestorInformation"
})
public class ClaimNewHireRequest implements Serializable {

	private String taskId;
	private RequestorInformation requestorInformation = new RequestorInformation();
	
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public RequestorInformation getRequestorInformation() {
		return requestorInformation;
	}
	
	public void setRequestorInformation(RequestorInformation requestorInformation) {
		this.requestorInformation = requestorInformation;
	}
}
