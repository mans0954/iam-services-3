package org.openiam.bpm.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.BaseObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivitiClaimRequest", propOrder = {
    "taskId"
})
public class ActivitiClaimRequest extends BaseObject {

	private String taskId;
	
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}
