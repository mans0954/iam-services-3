package org.openiam.idm.srvc.meta.domain;

import java.util.List;

public enum MetadataTypeGrouping {
	
	OBJECT_TYPE(true),
	USER_TYPE(true),
	OPERATION(true),
	PHONE(true),
	UI_WIDGET(true),
	USER(false),
	USER_2ND_STATUS(false),
	ADDRESS(true),
	EMAIL(true),
	AUTH_LEVEL(true),
	JOB_CODE(true),
	SERVICE_STATUS(true),
	GROUP_TYPE(true),
	ROLE_TYPE(true),
	ORG_TYPE(true),
	RESOURCE_TYPE(true),
	USER_OBJECT_TYPE(true),
	GROUP_CLASSIFICATION(true),
	AD_GROUP_TYPE(false),
	AD_GROUP_SCOPE(false),
	RISK(false),
	OAUTH_SCOPE(true);

	private boolean creatable;
	
	MetadataTypeGrouping(final boolean createable) {
		this.creatable = createable;
	}

	public boolean isCreatable() {
		return creatable;
	}
	
	public static MetadataTypeGrouping getByName(final String name) {
		MetadataTypeGrouping retVal = null;
		if(name != null) {
			for(final MetadataTypeGrouping grouping : MetadataTypeGrouping.values()) {
				if(grouping.name().equals(name)) {
					retVal = grouping;
					break;
				}
			}
		}
		return retVal;
	}
}