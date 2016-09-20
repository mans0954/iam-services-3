package org.openiam.bpm.util;

public enum ActivitiConstants {
	COMMENT("Comment"),
	REQUEST("Request"),
	IS_TASK_APPROVED("IsTaskApproved"),
	NEW_USER_ID("NewUserId"),
	CANDIDATE_USERS_IDS("candidateUsersIds"),
	TASK_NAME("TaskName"),
	TASK_DESCRIPTION("TaskDescription"),
	REQUESTOR("TaskOwner"),
    REQUESTOR_NAME("TaskOwnerName"),
	EXECUTOR_ID("ExecutorId"),
	APPROVER_ASSOCIATION_IDS("ApproverAssociationIds"),
	ASSOCIATION_ID("AssociationId"),
	ASSOCIATION_TYPE("AssociationType"),
	IS_ADMIN("IsAdmin"),
	IS_COMPLETE("IsComplete"),
	MEMBER_ASSOCIATION_ID("MemberAssociationId"),
	MEMBER_ASSOCIATION_TYPE("MemberAssociationType"),
	APPROVER_CARDINALTITY("ApproverCardindality"),
	REQUEST_METADATA_MAP("RequestMetadataMap"),
	CUSTOM_APPROVER_IDS("CustomApproverIds"),
	EMPLOYEE_ID("EmployeeId"),
    GROUP_ID("GroupId"),
	ATTESTATION_URL("ATTESTATION_URL"),
	WORKFLOW_NAME("WorkflowName"),
	DELETABLE("Deletable"),
	LOGIN("Login"),
	AUDIT_LOG_ID("AuditLogId"),
	ACCESS_RIGHTS("AccessRights"),
	LOOP_COUNTER("loopCounter"),
	
	CARDINALITY_OBJECT("cardinalityObject"),
	
	RESOURCE("Resource"),
	GROUP("Group"),
	ROLE("Role"),
	ORGANIZATION("Organization"),
	
	IS_REQUESTOR_ONLY_APROVER("IsRequestorOnlyApprover"),
	IS_REQUESTOR_CANDIDATE("IsRequestorCandidate"),
	CANDIDATE_USER_IDS("CandidateUserIds"),
	ATTESTATION_MANAGED_SYS_RESOURCES("ATTESTATION_MANAGED_SYS_RESOURCES"),;
	
	private String name;
	
	ActivitiConstants(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
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