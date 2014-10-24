package org.openiam.bpm.request;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLStreamConstants;

import org.openiam.base.BaseObject;

import com.thoughtworks.xstream.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivitiRequestDecision", propOrder = {
    "taskId",
    "comment",
    "accepted",
    "customVariables"
})
public class ActivitiRequestDecision extends BaseObject {

	private String taskId;
	private String comment;
	private boolean accepted;
	private Map<String, String> customVariables;
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public boolean isAccepted() {
		return accepted;
	}
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	
	public void addCustomVariable(final String key, final Object value) {
		if(key != null && value != null) {
			if(this.customVariables == null) {
				this.customVariables = new HashMap<String, String>();
			}
			this.customVariables.put(key, new XStream().toXML(value));
		}
	}
	
	public Map<String, String> getCustomVariables() {
		return this.customVariables;
	}
}
