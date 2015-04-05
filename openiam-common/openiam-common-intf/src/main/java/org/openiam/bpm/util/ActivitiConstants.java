package org.openiam.bpm.util;

import java.util.List;

import org.hibernate.type.AssociationType;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;

public enum ActivitiConstants {
	COMMENT("Comment", "comment"),
	REQUEST("Request",  null),
	IS_TASK_APPROVED("IsTaskApproved", "approved"),
	NEW_USER_ID("NewUserId", "newUserId"),
	CANDIDATE_USERS_IDS("candidateUsersIds",  "candidateUserIds"),
	TASK_NAME("TaskName", "taskName"),
	TASK_DESCRIPTION("TaskDescription", "taskDescription"),
	REQUESTOR("TaskOwner", "requestor"),
	EXECUTOR_ID("ExecutorId", "executorId"),
	APPROVER_ASSOCIATION_IDS("ApproverAssociationIds", "approverAssociationIds"),
	ASSOCIATION_ID("AssociationId", "associationId"),
	ASSOCIATION_TYPE("AssociationType", "associationType"),
	IS_ADMIN("IsAdmin", null),
	IS_COMPLETE("IsComplete", null),
	MEMBER_ASSOCIATION_ID("MemberAssociationId", "memberAssociationId"),
	MEMBER_ASSOCIATION_TYPE("MemberAssociationType", "memberAssociationType"),
	APPROVER_CARDINALTITY("ApproverCardindality", null),
	REQUEST_METADATA_MAP("RequestMetadataMap", null),
	CUSTOM_APPROVER_IDS("CustomApproverIds", "customApproverIds"),
	EMPLOYEE_ID("EmployeeId", "employeeId"),
    GROUP_ID("GroupId", "groupId"),
	ATTESTATION_URL("ATTESTATION_URL", "attestationURL"),
	WORKFLOW_NAME("WorkflowName", "workflowName"),
	DELETABLE("Deletable", null),
	LOGIN("Login",  "login"),
	AUDIT_LOG_ID("AuditLogId", null),
	ASSIGNEE_ID("AssigneeUserId", "assigneeUserId"),
	OPENIAM_VERSION("OPENIAM_VERSION", "openiamVersion"),
	
	LOOP_COUNTER("loopCounter", null),
	
	CARDINALITY_OBJECT("cardinalityObject", null),
	
	RESOURCE("Resource", "resource"),
	GROUP("Group", "group"),
	ROLE("Role", "role"),
	ORGANIZATION("Organization", "organization"),
	
	IS_REQUESTOR_ONLY_APROVER("IsRequestorOnlyApprover", null),
	IS_REQUESTOR_CANDIDATE("IsRequestorCandidate", null);
	
	private String name;
	
	/*
	 * The name of the field on java classes
	 */
	private String fieldName;
	
	ActivitiConstants(final String name, final String fieldName) {
		this.name = name;
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public String getName() {
		return name;
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