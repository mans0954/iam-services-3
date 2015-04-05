package org.openiam.bpm.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivitiHistoricDetail", propOrder = {
	"comment",
	"approved",
	"newUserId",
	"assigneeUserId",
	"assigneeUser",
	"candidateUserIds",
	"taskName",
	"taskDescription",
	"requestor",
	"executorId",
	"approverAssociationIds",
	"associationId",
	"associationType",
	"memberAssociationId",
	"memberAssociationType",
	"customApproverIds",
	"employeeId",
	"groupId",
	"attestationURL",
	"workflowName",
	"openiamVersion",
	"login",
	"resource",
	"group",
	"role",
	"organization",
	"newUser",
	"candidateUsers",
	"requestorUser",
	"executor",
	"customApprovers"
})
public class ActivitiHistoricDetail extends Response {
	
	public ActivitiHistoricDetail() {}

	private String comment;
	private Boolean approved;
	
	@ActivitiUserField(value="newUser", exposeDetails=true)
	private String newUserId;
	private User newUser;
	
	@ActivitiUserField(value="candidateUsers", exposeDetails=false)
	private List<String> candidateUserIds;
	private List<User> candidateUsers;
	
	private String taskName;
	private String taskDescription;
	
	@ActivitiUserField(value="requestorUser", exposeDetails=false)
	private String requestor;
	private User requestorUser;
	
	@ActivitiUserField(value="executor", exposeDetails=false)
	private String executorId;
	private User executor;
	
	@ActivitiUserField(value="assigneeUser", exposeDetails=false)
	private String assigneeUserId;
	private User assigneeUser;
	
	private List<String> approverAssociationIds;
	private String associationId;
	private String associationType;
	private String memberAssociationId;
	private String memberAssociationType;
	
	@ActivitiUserField(value="customApprovers", exposeDetails=false)
	private List<String> customApproverIds;
	private List<User> customApprovers;
	
	private String employeeId;
	private String groupId;
	private String attestationURL;
	private String workflowName;
	private String openiamVersion;
	
	@ActivitiJSONField
	private Login login;
	
	@ActivitiJSONField
	private Resource resource;
	
	@ActivitiJSONField
	private Group group;
	
	@ActivitiJSONField
	private Role role;
	
	@ActivitiJSONField
	private Organization organization;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public String getNewUserId() {
		return newUserId;
	}

	public void setNewUserId(String newUserId) {
		this.newUserId = newUserId;
	}

	public List<String> getCandidateUserIds() {
		return candidateUserIds;
	}

	public void setCandidateUserIds(List<String> candidateUserIds) {
		this.candidateUserIds = candidateUserIds;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getExecutorId() {
		return executorId;
	}

	public void setExecutorId(String executorId) {
		this.executorId = executorId;
	}

	public List<String> getApproverAssociationIds() {
		return approverAssociationIds;
	}

	public void setApproverAssociationIds(List<String> approverAssociationIds) {
		this.approverAssociationIds = approverAssociationIds;
	}

	public String getAssociationId() {
		return associationId;
	}

	public void setAssociationId(String associationId) {
		this.associationId = associationId;
	}

	public String getAssociationType() {
		return associationType;
	}

	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}

	public String getMemberAssociationId() {
		return memberAssociationId;
	}

	public void setMemberAssociationId(String memberAssociationId) {
		this.memberAssociationId = memberAssociationId;
	}

	public String getMemberAssociationType() {
		return memberAssociationType;
	}

	public void setMemberAssociationType(String memberAssociationType) {
		this.memberAssociationType = memberAssociationType;
	}

	public List<String> getCustomApproverIds() {
		return customApproverIds;
	}

	public void setCustomApproverIds(List<String> customApproverIds) {
		this.customApproverIds = customApproverIds;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getAttestationURL() {
		return attestationURL;
	}

	public void setAttestationURL(String attestationURL) {
		this.attestationURL = attestationURL;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getOpeniamVersion() {
		return openiamVersion;
	}

	public void setOpeniamVersion(String openiamVersion) {
		this.openiamVersion = openiamVersion;
	}

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public User getNewUser() {
		return newUser;
	}

	public void setNewUser(User newUser) {
		this.newUser = newUser;
	}

	public List<User> getCandidateUsers() {
		return candidateUsers;
	}

	public void setCandidateUsers(List<User> candidateUsers) {
		this.candidateUsers = candidateUsers;
	}

	public User getRequestorUser() {
		return requestorUser;
	}

	public void setRequestorUser(User requestorUser) {
		this.requestorUser = requestorUser;
	}

	public User getExecutor() {
		return executor;
	}

	public void setExecutor(User executor) {
		this.executor = executor;
	}

	public List<User> getCustomApprovers() {
		return customApprovers;
	}

	public void setCustomApprovers(List<User> customApprovers) {
		this.customApprovers = customApprovers;
	}

	public String getAssigneeUserId() {
		return assigneeUserId;
	}

	public void setAssigneeUserId(String assigneeUserId) {
		this.assigneeUserId = assigneeUserId;
	}

	public User getAssigneeUser() {
		return assigneeUser;
	}

	public void setAssigneeUser(User assigneeUser) {
		this.assigneeUser = assigneeUser;
	}
	
	
}
