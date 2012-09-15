package org.openiam.bpm.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcceptOrRejectNewHireRequest", propOrder = {
    "taskId",
    "requestorInformation",
    "comment"
})
public class AcceptOrRejectNewHireRequest implements Serializable {

	private String taskId;
	private String comment;
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}
