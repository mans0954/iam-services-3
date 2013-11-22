package org.openiam.bpm.util;

public enum ActivitiRequestType {
	
	NEW_HIRE_WITH_APPROVAL("newHireWithApprovalProcess", "New Hire"),
	NEW_HIRE_NO_APPROVAL("newHireWithNoApproval", "New Hire"),
	SELF_REGISTRATION("selfRegistration", "Self Registration"),
	ADD_USER_TO_ROLE("addUserToRole", "Add User To Role"),
	REMOVE_USER_FROM_ROLE("removeUserFromRole", "Remove User From Role"),
	ADD_USER_TO_GROUP("addUserToGroup", "Add User To Group"),
	REMOVE_USER_FROM_GROUP("removeUserFromGroup", "Remove User From Group"),
	ENTITLE_USER_TO_RESOURCE("entitleUserToResource", "Entitle User To Resoruce"),
	DISENTITLE_USR_FROM_RESOURCE("disentitleUserFromResource", "Disentitle User from Resource"),
	EDIT_USER("editUser", "Edit User"),
	
	ADD_USER_TO_ORG("addUserToOrganization", "Add User To Organization"),
	REMOVE_USER_FROM_ORG("removeUserFromOrganization", "Remove User from Organization"),
	
	ATTESTATION("attestationWorkflow", "Attestation"),
	
	ADD_SUPERIOR("addSuperior", "Add Superior"),
	REMOVE_SUPERIOR("removeSuperior", "Remove Superior"),
	
	SAVE_LOGIN("saveLogin", "Save Login"),
	DELETE_LOGIN("deleteLogin", "Delete Login"),
	
	NEW_RESOURCE("newResource", "New Resource"),
	EDIT_RESOURCE("editResource", "Edit Resource"),
	DELETE_RESOURCE("deleteResource", "Delete Resource"),
	
	NEW_GROUP("newGroup", "New Group"),
	EDIT_GROUP("editGroup", "Edit Group"),
	DELETE_GROUP("deleteGroup", "Delete Group"),
	
	NEW_ROLE("newRole", "New Role"),
	EDIT_ROLE("editRole", "Edit Role"),
	DELETE_ROLE("deleteRole", "Delete Role");
	
	private String description;
	private String key;
	
	ActivitiRequestType(final String key, final String description) {
		this.key = key;
		this.description = description;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getDescription() {
		return description;
	}
}
