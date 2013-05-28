package org.openiam.bpm.util;

public enum ActivitiRequestType {
	
	NEW_HIRE_WITH_APPROVAL("newHireWithApprovalProcess"),
	NEW_HIRE_NO_APPROVAL("newHireWithNoApproval"),
	SELF_REGISTRATION("selfRegistration");
	
	private String key;
	
	ActivitiRequestType(final String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}
