package org.openiam.bpm.util;

public enum ActivitiRequestType {
	
	NEW_HIRE_WITH_APPROVAL("newHireWithApprovalProcess", "New Hire"),
	NEW_HIRE_NO_APPROVAL("newHireWithNoApproval", "New Hire"),
	SELF_REGISTRATION("selfRegistration", "Self Registration");
	
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
