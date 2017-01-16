package org.openiam.idm.srvc.meta.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "MetadataTypeGrouping")
@XmlEnum
public enum MetadataTypeGrouping {
	@XmlEnumValue("OBJECT_TYPE")
	OBJECT_TYPE(true),
	@XmlEnumValue("USER_TYPE")
	USER_TYPE(true),
	@XmlEnumValue("OPERATION")
	OPERATION(true),
	@XmlEnumValue("PHONE")
	PHONE(true),
	@XmlEnumValue("UI_WIDGET")
	UI_WIDGET(true),
	@XmlEnumValue("USER")
	USER(false),
	@XmlEnumValue("USER_2ND_STATUS")
	USER_2ND_STATUS(false),
	@XmlEnumValue("ADDRESS")
	ADDRESS(true),
	@XmlEnumValue("EMAIL")
	EMAIL(true),
	@XmlEnumValue("KERB_AUTH_LEVEL")
	KERB_AUTH_LEVEL(true),
	@XmlEnumValue("CERT_AUTH_LEVEL")
	CERT_AUTH_LEVEL(true),
	@XmlEnumValue("JOB_CODE")
	JOB_CODE(true),
	@XmlEnumValue("SERVICE_STATUS")
	SERVICE_STATUS(true),
	@XmlEnumValue("GROUP_TYPE")
	GROUP_TYPE(true),
	@XmlEnumValue("ROLE_TYPE")
	ROLE_TYPE(true),
	@XmlEnumValue("ORG_TYPE")
	ORG_TYPE(true),
	@XmlEnumValue("RESOURCE_TYPE")
	RESOURCE_TYPE(true),
	@XmlEnumValue("USER_OBJECT_TYPE")
	USER_OBJECT_TYPE(true),
	@XmlEnumValue("GROUP_CLASSIFICATION")
	GROUP_CLASSIFICATION(true),
	@XmlEnumValue("AD_GROUP_TYPE")
	AD_GROUP_TYPE(false),
	@XmlEnumValue("AD_GROUP_SCOPE")
	AD_GROUP_SCOPE(false),
	@XmlEnumValue("AFFILIATIONS")
    AFFILIATIONS(true),
	@XmlEnumValue("USER_SUB_TYPES")
    USER_SUB_TYPES(true),
	@XmlEnumValue("RISK")
	RISK(false),
	@XmlEnumValue("PROV_OBJECT")
	PROV_OBJECT(true),
	@XmlEnumValue("OAUTH_CLIENT_AUTH_TYPE")
	OAUTH_CLIENT_AUTH_TYPE(false),
	@XmlEnumValue("OAUTH_AUTH_GRANT")
	OAUTH_AUTH_GRANT(false),
	@XmlEnumValue("CONNECTOR_TYPE")
	CONNECTOR_TYPE(true);


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