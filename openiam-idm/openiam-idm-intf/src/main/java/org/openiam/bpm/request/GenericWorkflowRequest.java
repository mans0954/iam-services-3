package org.openiam.bpm.request;

import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseObject;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenericWorkflowRequest", propOrder = {
        "name",
        "activitiRequestType",
        "associationId",
        "associationType",
        "memberAssociationId",
        "memberAssociationType",
        "description",
        "parameters",
        "customApproverAssociationIds",
        "customApproverIds",
        "isCustomApproversSequential",
        "userCentricUserId",
        "jsonSerializedParams",
        "additionalApproverIds",
        "deletable",
        "accessRights",
        "startDate",
        "endDate",
        "userNotes"
})
public class GenericWorkflowRequest extends BaseObject {

    private String associationId;
    private AssociationType associationType;

    private String memberAssociationId;
    private AssociationType memberAssociationType;

    private String name;
    private String description;
    private String activitiRequestType;
    private Map<String, String> jsonSerializedParams;
    private Map<String, Object> parameters;
    private List<String> customApproverAssociationIds;
    private Set<String> customApproverIds;
    private String userCentricUserId;
    private boolean deletable = true;
    private boolean isCustomApproversSequential;
    private Set<String> additionalApproverIds;
    private Set<String> accessRights;

    @XmlSchemaType(name = "dateTime")
    private Date startDate;
    @XmlSchemaType(name = "dateTime")
    private Date endDate;
    private String userNotes;

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
        if (this.parameters == null) {
            this.parameters = new HashMap<String, Object>();
        }
        this.parameters.put(key, value);
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(activitiRequestType) || StringUtils.isBlank(requestorUserId);
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
        if (customApproverId != null) {
            if (this.customApproverIds == null) {
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
        if (key != null && value != null && mapper != null) {
            if (jsonSerializedParams == null) {
                jsonSerializedParams = new HashMap<String, String>();
            }
            jsonSerializedParams.put(key, mapper.writeValueAsString(value));
        }
    }

    public String getMemberAssociationId() {
        return memberAssociationId;
    }

    public void setMemberAssociationId(String memberAssociationId) {
        this.memberAssociationId = memberAssociationId;
    }

    public AssociationType getMemberAssociationType() {
        return memberAssociationType;
    }

    public void setMemberAssociationType(AssociationType memberAssociationType) {
        this.memberAssociationType = memberAssociationType;
    }

    public Set<String> getAdditionalApproverIds() {
        return additionalApproverIds;
    }

    public void setAdditionalApproverIds(Set<String> additionalApproverIds) {
        this.additionalApproverIds = additionalApproverIds;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }


    public boolean isCustomApproversSequential() {
        return isCustomApproversSequential;
    }

    public void setCustomApproversSequential(boolean isCustomApproversSequential) {
        this.isCustomApproversSequential = isCustomApproversSequential;
    }

	public Set<String> getAccessRights() {
		return accessRights;
	}

	public void setAccessRights(Set<String> accessRights) {
		this.accessRights = accessRights;
	}

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }
}