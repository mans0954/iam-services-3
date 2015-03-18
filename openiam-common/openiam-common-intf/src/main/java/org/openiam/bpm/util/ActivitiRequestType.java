package org.openiam.bpm.util;

public enum ActivitiRequestType {
	
	NEW_HIRE_WITH_APPROVAL("newHireWithApprovalProcess", "New Hire", true),
	NEW_HIRE_NO_APPROVAL("newHireWithNoApproval", "New Hire", true),
	SELF_REGISTRATION("selfRegistration", "Self Registration", true),
	ADD_USER_TO_ROLE("addUserToRole", "Add User To Role", true),
	REMOVE_USER_FROM_ROLE("removeUserFromRole", "Remove User From Role", true),
	ADD_USER_TO_GROUP("addUserToGroup", "Add User To Group", true),
	REMOVE_USER_FROM_GROUP("removeUserFromGroup", "Remove User From Group", true),
	ENTITLE_USER_TO_RESOURCE("entitleUserToResource", "Entitle User To Resoruce", true),
	DISENTITLE_USR_FROM_RESOURCE("disentitleUserFromResource", "Disentitle User from Resource", true),
	EDIT_USER("editUser", "Edit User", true),
	
	ADD_USER_TO_ORG("addUserToOrganization", "Add User To Organization", true),
	REMOVE_USER_FROM_ORG("removeUserFromOrganization", "Remove User from Organization", true),
	
	ATTESTATION("attestationWorkflow", "Attestation", true),
    GROUP_ATTESTATION("groupAttestationWorkflow", "Group Attestation", true),
	RESOURCE_CERTIFICATION("resourceCertification", "Resource Certification", true),
	
	ADD_SUPERIOR("addSuperior", "Add Superior", true),
	REMOVE_SUPERIOR("removeSuperior", "Remove Superior", true),
	
	SAVE_LOGIN("saveLogin", "Save Login", false),
	DELETE_LOGIN("deleteLogin", "Delete Login", false),
	
	NEW_RESOURCE("newResource", "New Resource", false),
	EDIT_RESOURCE("editResource", "Edit Resource", false),
	DELETE_RESOURCE("deleteResource", "Delete Resource", false),
	
	NEW_GROUP("newGroup", "New Group", false),
	EDIT_GROUP("editGroup", "Edit Group", false),
	DELETE_GROUP("deleteGroup", "Delete Group", false),
	
	NEW_ROLE("newRole", "New Role", false),
	EDIT_ROLE("editRole", "Edit Role", false),
	DELETE_ROLE("deleteRole", "Delete Role", false),
	
	NEW_ORGANIZATION("newOrganization", "New Organization", false),
	EDIT_ORGANIZATION("editOrganization", "Edit Organization", false),
	DELETE_ORGANIZATION("deleteOrganization", "DeleteOrganization", false),
	
	ADD_GROUP_TO_GROUP("addGroup2Group", "Add Group to Group", false),
	REMOVE_GROUP_FROM_GROUP("removeGroupFromGroup", "Remove Group from Group", false),
	
	ADD_ROLE_TO_GROUP("addRole2Group", "Add Role to Group", false),
	REMOVE_ROLE_FROM_GROUP("removeRoleFromGroup", "Remove Role from Group", false),
	
	ADD_ROLE_TO_ROLE("addRole2Role", "Add Role to Role", false),
	REMOVE_ROLE_FROM_ROLE("removeRoleFromRole", "Remove Role from Role", false),
	
	ENTITLE_RESOURCE_TO_GROUP("addResource2Group", "Add Resource to Group", false),
	DISENTITLE_RESOURCE_FROM_GROUP("removeResourceFromGroup", "Remove Resource from Group", false),
	
	ENTITLE_RESOURCE_TO_ROLE("addResource2Role", "Add Resource to Role", false),
	DISENTITLE_RESOURCE_FROM_ROLE("removeResourceFromRole", "Remove Resource from Role", false),
	
	ADD_RESOURCE_TO_RESOURCE("addResource2Resource", "Add Resource to Resource", false),
	REMOVE_RESOURCE_FROM_RESOURCE("removeResourceFromResource", "Remove Resource from Resource", false);
	
	private boolean userCentric;
	private String description;
	private String key;
	
	ActivitiRequestType(final String key, final String description, final boolean userCentric) {
		this.key = key;
		this.description = description;
		this.userCentric = userCentric;
	}
	
	public boolean isUserCentric() {
		return userCentric;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static ActivitiRequestType getByName(final String key) {
		ActivitiRequestType type = null;
		if(key != null) {
			for(final ActivitiRequestType k : ActivitiRequestType.values()) {
				if(k.getKey().equals(key)) {
					type = k;
					break;
				}
			}
		}
		return type;
	}
}
