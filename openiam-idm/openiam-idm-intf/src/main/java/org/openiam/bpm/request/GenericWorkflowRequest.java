package org.openiam.bpm.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.request.RequestorInformation;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenericWorkflowRequest", propOrder = {
    "name",
	"activitiRequestType",
    "associationId",
    "associationType",
    "description",
    "parameters",
    "customApproverAssociationIds"
})
public class GenericWorkflowRequest extends RequestorInformation {

	private AssociationType associationType;
	private String name;
	private String description;
	private String associationId;
	private String activitiRequestType;
	private Map<String, Object> parameters;
	private Set<String> customApproverAssociationIds;

	public String getActivitiRequestType() {
		return activitiRequestType;
	}

	public void setActivitiRequestType(String activitiRequestType) {
		this.activitiRequestType = activitiRequestType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AssociationType getAssociationType() {
		return associationType;
	}

	public void setAssociationType(AssociationType associationType) {
		this.associationType = associationType;
	}

	public String getAssociationId() {
		return associationId;
	}

	public void setAssociationId(String associationId) {
		this.associationId = associationId;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}	
	
	public void addParameter(final String key, final String value) {
		if(this.parameters == null) {
			this.parameters = new HashMap<String, Object>();
		}
		this.parameters.put(key, value);
	}
	
	public boolean isEmpty() {
		return StringUtils.isBlank(activitiRequestType) ||
			   StringUtils.isBlank(callerUserId);
	}

	public Set<String> getCustomApproverAssociationIds() {
		return customApproverAssociationIds;
	}

	public void setCustomApproverAssociationIds(
			Set<String> customApproverAssociationIds) {
		this.customApproverAssociationIds = customApproverAssociationIds;
	}
	
	
}
