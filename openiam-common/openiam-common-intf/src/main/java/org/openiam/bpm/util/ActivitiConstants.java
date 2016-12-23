package org.openiam.bpm.util;

import java.util.List;

import org.hibernate.type.AssociationType;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;

public enum ActivitiConstants {
	COMMENT("Comment", "comment", true),
	REQUEST("Request",  null, false),
	IS_TASK_APPROVED("IsTaskApproved", "approved", true),
	NEW_USER_ID("NewUserId", "newUserId", true),
	CANDIDATE_USERS_IDS("candidateUsersIds",  "candidateUserIds", true),
	TASK_NAME("TaskName", "taskName", false),
	TASK_DESCRIPTION("TaskDescription", "taskDescription", false),
	LANGUAGE_ID("LanguageId", "languageId", false),
	REQUESTOR("TaskOwner", "requestor", false),
	REQUESTOR_NAME("TaskOwnerName", "taskOwnerName", false),
	EXECUTOR_ID("ExecutorId", "executorId", true),
	APPROVER_ASSOCIATION_IDS("ApproverAssociationIds", "approverAssociationIds", false),
	ASSOCIATION_ID("AssociationId", "associationId", false),
	ASSOCIATION_TYPE("AssociationType", "associationType", false),
	IS_ADMIN("IsAdmin", null, false),
	IS_COMPLETE("IsComplete", null, false),
	MEMBER_ASSOCIATION_ID("MemberAssociationId", "memberAssociationId", false),
	ACCESS_RIGHTS("AccessRights", "accessRights", false),
	MEMBER_ASSOCIATION_TYPE("MemberAssociationType", "memberAssociationType", false),
	APPROVER_CARDINALTITY("ApproverCardindality", null, false),
	REQUEST_METADATA_MAP("RequestMetadataMap", null, false),
	CUSTOM_APPROVER_IDS("CustomApproverIds", "customApproverIds", false),
	EMPLOYEE_ID("EmployeeId", "employeeId", false),
    GROUP_ID("GroupId", "groupId", false),
	ATTESTATION_URL("ATTESTATION_URL", "attestationURL", false),
	WORKFLOW_NAME("WorkflowName", "workflowName", false),
	DELETABLE("Deletable", null, false),
	LOGIN("Login",  "login", false),
	AUDIT_LOG_ID("AuditLogId", null, false),
	ASSIGNEE_ID("AssigneeUserId", "assigneeUserId", true),
	TERMINATED_BY_OWNER("TermintedByOwner", "terminatedByOwner", true),
	OPENIAM_VERSION("OPENIAM_VERSION", "openiamVersion", false),
	WORKFLOW_RESOURCE_ID("WORKFLOW_RESOURCE_ID", null, false),
	
	LOOP_COUNTER("loopCounter", null, false),
	
	CARDINALITY_OBJECT("cardinalityObject", null, false),
	
	RESOURCE("Resource", "resource", false),
	GROUP("Group", "group", false),
	ROLE("Role", "role", false),
	ORGANIZATION("Organization", "organization", false),
	
	IS_REQUESTOR_ONLY_APROVER("IsRequestorOnlyApprover", null, false),
	IS_REQUESTOR_CANDIDATE("IsRequestorCandidate", null, false),

	CANDIDATE_USER_IDS("CandidateUserIds", null, false),
	ATTESTATION_MANAGED_SYS_RESOURCES("ATTESTATION_MANAGED_SYS_RESOURCES", null, false),

	// TODO: need to fix field name once start/end date will be added to the entity model
	START_DATE("StartDate", null, false),
	END_DATE("EndDate", null, false),
	USER_NOTE("UserNotes", null, false);
	
	private String name;
	
	/*
	 * The name of the field on java classes
	 */
	private String fieldName;
	
	/*
	 * Signifies if this variable is local or not 
	 */
	private boolean local;
	
	ActivitiConstants(final String name, final String fieldName, final boolean local) {
		this.name = name;
		this.fieldName = fieldName;
		this.local = local;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isLocal() {
		return local;
	}

	public static ActivitiConstants getByName(final String name) {
		ActivitiConstants retVal = null;
		for(final ActivitiConstants k : ActivitiConstants.values()) {
			if(k.getName().equals(name)) {
				retVal = k;
				break;
			}
		}
		return retVal;
	}

	public static ActivitiConstants getByDeclarationName(final String name) {
		ActivitiConstants retVal = null;
		for(final ActivitiConstants k : ActivitiConstants.values()) {
			if(k.name().equals(name)) {
				retVal = k;
				break;
			}
		}
		return retVal;
	}
}