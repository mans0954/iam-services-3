package org.openiam.bpm.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.provision.dto.ProvisionUser;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NewHireRequest", propOrder = {
    "provisionUser",
    "provisionRequest",
    "requestorInformation",
    "taskId",
    "comment"
})
public class NewHireRequest implements Serializable {
	private String comment;
	private String taskId;
	private ProvisionUser provisionUser;
	private ProvisionRequest provisionRequest;
	private RequestorInformation requestorInformation = new RequestorInformation();
	
	public NewHireRequest() {
		
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public ProvisionUser getProvisionUser() {
		return provisionUser;
	}

	public void setProvisionUser(ProvisionUser provisionUser) {
		this.provisionUser = provisionUser;
	}

	public ProvisionRequest getProvisionRequest() {
		return provisionRequest;
	}

	public void setProvisionRequest(ProvisionRequest provisionRequest) {
		this.provisionRequest = provisionRequest;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(final String comment) {
		this.comment = comment;
	}

	public RequestorInformation getRequestorInformation() {
		return requestorInformation;
	}

	public void setRequestorInformation(RequestorInformation requestorInformation) {
		this.requestorInformation = requestorInformation;
	}
}
