package org.openiam.am.srvc.constants;

public enum SsoAttributeType {
    Any("Any"), 
    String("String"), 
    Integer("Integer"), 
    URI("URI"),
    DateTime("DateTime"),
    LIST_OF_STRINGS("List of Strings"),
    LIST_OF_INTEGERS("List of Integers"),
    LIST_OF_ANY("List of Any");
    
    private String displayName;
    
    SsoAttributeType(final String displayName) {
    	this.displayName = displayName;
    }

	public String getDisplayName() {
		return displayName;
	}
}
