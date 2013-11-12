package org.openiam.bpm.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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
    "customApproverAssociationIds",
    "customApproverIds",
    "userCentricUserId",
    "jsonSerializedParams"
})
public class GenericWorkflowRequest extends RequestorInformation {

	private AssociationType associationType;
	private String name;
	private String description;
	private String associationId;
	private String activitiRequestType;
	private Map<String, String> jsonSerializedParams;
	private Map<String, Object> parameters;
	private List<String> customApproverAssociationIds;
	private Set<String> customApproverIds;
	private String userCentricUserId;

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
	
	public void addParameter(final String key, final Object value) {
		if(this.parameters == null) {
			this.parameters = new HashMap<String, Object>();
		}
		this.parameters.put(key, value);
	}
	
	public boolean isEmpty() {
		return StringUtils.isBlank(activitiRequestType) ||
			   StringUtils.isBlank(callerUserId);
	}

	public List<String> getCustomApproverAssociationIds() {
		return customApproverAssociationIds;
	}

	public void setCustomApproverAssociationIds(
			List<String> customApproverAssociationIds) {
		this.customApproverAssociationIds = customApproverAssociationIds;
	}

	public Set<String> getCustomApproverIds() {
		return customApproverIds;
	}

	public void setCustomApproverIds(Set<String> customApproverIds) {
		this.customApproverIds = customApproverIds;
	}

	public String getUserCentricUserId() {
		return userCentricUserId;
	}

	public void setUserCentricUserId(String userCentricUserId) {
		this.userCentricUserId = userCentricUserId;
	}

	public void addCustomApproverId(final String customApproverId) {
		if(customApproverId != null) {
			if(this.customApproverIds == null) {
				this.customApproverIds = new HashSet<String>();
			}
			this.customApproverIds.add(customApproverId);
		}
	}
	
	public Map<String, String> getJsonSerializedParams() {
		return jsonSerializedParams;
	}

	public void setJsonSerializedParams(Map<String, String> jsonSerializedParams) {
		this.jsonSerializedParams = jsonSerializedParams;
	}

	public void addJSONParameter(final String key, final Object value, final ObjectMapper mapper) throws Exception {
		if(key != null && value != null && mapper != null) {
			jsonSerializedParams.put(key, mapper.writeValueAsString(value));
		}
	}
}