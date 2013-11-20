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
	EXECUTOR_ID("ExecutorId"),
	APPROVER_ASSOCIATION_IDS("ApproverAssociationIds"),
	ASSOCIATION_ID("AssociationId"),
	ASSOCIATION_TYPE("AssociationType"),
	IS_ADMIN("IsAdmin"),
	IS_COMPLETE("IsComplete"),
	MEMBER_ASSOCIATION_ID("MemberAssociationId"),
	APPROVER_CARDINALTITY("ApproverCardindality"),
	REQUEST_METADATA_MAP("RequestMetadataMap"),
	CUSTOM_APPROVER_IDS("CustomApproverIds"),
	EMPLOYEE_ID("EmployeeId"),
	DISASSOCIATED_ROLE_IDS("DisassociatedRoleIds"),
	DISASSOCIATED_GROUP_IDS("DisassociatedGroupIds"),
	DISASSOCIATED_RESOURCE_IDS("DisassociatedResourceIds"),
	CUSTOM_TASK_UI_URL("CustomTaskUIUrl"),
	
	LOGIN("Login"),
	
	LOOP_COUNTER("loopCounter"),
	
	CARDINALITY_OBJECT("cardinalityObject"),
	
	RESOURCE("Resource"),
	
	RESOURCE_ID("ResourceId");
	
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