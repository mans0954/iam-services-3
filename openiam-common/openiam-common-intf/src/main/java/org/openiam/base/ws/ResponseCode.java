package org.openiam.base.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * ResponseStatus provides valid values that an operation in a service can return.
 *
 * @author suneet
 */
@XmlType(name = "ResponseErrorCode")
@XmlEnum
public enum ResponseCode {
	
	@XmlEnumValue("resourcePropResourceIdMissing")
	RESOURCE_PROP_RESOURCE_ID_MISSING("resourcePropResourceIdMissing"),
	
	@XmlEnumValue("resourcePropValueMissing")
	RESOURCE_PROP_VALUE_MISSING("resourcePropValueMissing"),
	
	@XmlEnumValue("resourcePropMissing")
	RESOURCE_PROP_MISSING("resourcePropMissing"),
	
	@XmlEnumValue("hangingChildren")
	HANGING_CHILDREN("hangingChildren"),
	
	@XmlEnumValue("hangingGroups")
	HANGING_GROUPS("hangingGroups"),
	
	@XmlEnumValue("hangingRoles")
	HANGING_ROLES("hangingRoles"),
	
	@XmlEnumValue("menuDoesNotExist")
	MENU_DOES_NOT_EXIST("menuDoesNotExist"),
	
	@XmlEnumValue("invalidResourceType")
	INVALID_RESOURCE_TYPE("invalidResourceType"),
	
	@XmlEnumValue("invalidRoleDomain")
	INVALID_ROLE_DOMAIN("invalidRoleDomain"),
	
	@XmlEnumValue("noName")
	NO_NAME("noName"),
	
	@XmlEnumValue("nameTaken")
	NAME_TAKEN("nameTaken"),
	
	@XmlEnumValue("RULE_NOT_SET")
	RULE_NOT_SET("RULE_NOT_SET"),
	
	@XmlEnumValue("membershipExists")
	MEMBERSHIP_EXISTS("membershipExists"),
	
    @XmlEnumValue("resourceTypesNotEqual")
    RESOURCE_TYPES_NOT_EQUAL("resourceTypesNotEqual"),

    @XmlEnumValue("relationshipExists")
    RELATIONSHIP_EXISTS("relationshipExists"),
    
    @XmlEnumValue("cantAddYourselfAsChild")
    CANT_ADD_YOURSELF_AS_CHILD("cantAddYourselfAsChild"),
    
    @XmlEnumValue("circularDependency")
    CIRCULAR_DEPENDENCY("circularDependency"),
	
    @XmlEnumValue("objectNotFound")
    OBJECT_NOT_FOUND("objectNotFound"),
    
    @XmlEnumValue("hangingChildRoles")
    ROLE_HANGING_CHILD_ROLES("hangingChildRoles"),
    
    @XmlEnumValue("hangingRoleGroups")
    ROLE_HANGING_GROUPS("hangingRoleGroups"),
    
    @XmlEnumValue("hangingRoleResources")
    ROLE_HANGING_RESOURCES("hangingRoleResources"),
    
    @XmlEnumValue("hangingRoleUsers")
    ROLE_HANGING_USERS("hangingRoleUsers"),

    @XmlEnumValue("classNotFound")
    CLASS_NOT_FOUND("classNotFound"),

    @XmlEnumValue("principalNotFound")
    PRINCIPAL_NOT_FOUND("principalNotFound"),

    @XmlEnumValue("userNotFound")
    USER_NOT_FOUND("userNotFound"),

    @XmlEnumValue("userStatus")
    USER_STATUS("userStatus"),

    @XmlEnumValue("supervisorlNotFound")
    SUPERVISOR_NOT_FOUND("supervisorNotFound"),

    @XmlEnumValue("DUPLICATE_PRINCIPAL")
    DUPLICATE_PRINCIPAL("DUPLICATE_PRINCIPAL"),

    // PASSWORD ERROR CODES
    @XmlEnumValue("failPasswordPolicy")
    FAIL_PASSWORD_POLICY("failPasswordPolicy"),

    // AUTHENTICATION ERROR CODES

    //GROUP ERROR CODES
    @XmlEnumValue("groupIdNull")
    GROUP_ID_NULL("groupIdNull"),

    @XmlEnumValue("groupIdInvalid")
    GROUP_ID_INVALID("groupIdInvalid"),

    // ROLE ERROR CODES
    @XmlEnumValue("roleIdNull")
    ROLE_ID_NULL("roleIdNull"),

    @XmlEnumValue("roleIdInvalid")
    ROLE_ID_INVALID("roleIdInvalid"),

    @XmlEnumValue("objectIdInvalid")
    OBJECT_ID_INVALID("objectIdInvalid"),


    @XmlEnumValue("success")
    SUCCESS("success"),

    // GENERAL ERROR CODES
    @XmlEnumValue("FAIL_SQL_ERORR")
    FAIL_SQL_ERROR("FAIL_SQL_ERROR"),

    @XmlEnumValue("FAIL_CONNECTION")
    FAIL_CONNECTION("FAIL_CONNECTION"),

    // Password Policy ERROR CODES

    @XmlEnumValue("FAIL_ALPHA_CHAR_RULE")
    FAIL_ALPHA_CHAR_RULE("FAIL_ALPHA_CHAR_RULE"),

    @XmlEnumValue("FAIL_LOWER_CASE_RULE")
    FAIL_LOWER_CASE_RULE("FAIL_LOWER_CASE_RULE"),

    @XmlEnumValue("FAIL_UPPER_CASE_RULE")
    FAIL_UPPER_CASE_RULE("FAIL_UPPER_CASE_RULE"),

    @XmlEnumValue("FAIL_NON_APHANUMERIC_RULE")
    FAIL_NON_APHANUMERIC_RULE("FAIL_NON_APHANUMERIC_RULE"),

    @XmlEnumValue("FAIL_NUMERIC_CHAR_RULE")
    FAIL_NUMERIC_CHAR_RULE("FAIL_NUMERIC_CHAR_RULE"),

    @XmlEnumValue("FAIL_HISTORY_RULE")
    FAIL_HISTORY_RULE("FAIL_HISTORY_RULE"),

    @XmlEnumValue("FAIL_LENGTH_RULE")
    FAIL_LENGTH_RULE("FAIL_LENGTH_RULE"),

    @XmlEnumValue("FAIL_NEQ_NAME")
    FAIL_NEQ_NAME("FAIL_NEQ_NAME"),

    @XmlEnumValue("FAIL_NEQ_PASSWORD")
    FAIL_NEQ_PASSWORD("FAIL_NEQ_PASSWORD"),

    @XmlEnumValue("FAIL_NEQ_PRINCIPAL")
    FAIL_NEQ_PRINCIPAL("FAIL_NEQ_PRINCIPAL"),

    @XmlEnumValue("FAIL_PASSWORD_CHANGE_FREQUENCY")
    FAIL_PASSWORD_CHANGE_FREQUENCY("FAIL_PASSWORD_CHANGE_FREQUENCY"),

    @XmlEnumValue("PASSWORD_POLICY_NOT_FOUND")
    PASSWORD_POLICY_NOT_FOUND("PASSWORD_POLICY_NOT_FOUND"),

    @XmlEnumValue("FAIL_PASSWORD_CHANGE_ALLOW")
    FAIL_PASSWORD_CHANGE_ALLOW("FAIL_PASSWORD_CHANGE_ALLOW"),

    @XmlEnumValue("FAIL_REJECT_CHARS_IN_PSWD")
    FAIL_REJECT_CHARS_IN_PSWD("FAIL_REJECT_CHARS_IN_PSWD"),

    @XmlEnumValue("FAIL_MIN_WORDS_PASSPHRASE_RULE")
    FAIL_MIN_WORDS_PASSPHRASE_RULE("FAIL_MIN_WORDS_PASSPHRASE_RULE"),

    @XmlEnumValue("FAIL_REPEAT_SAME_WORD_PASSPHRASE_RULE")
    FAIL_REPEAT_SAME_WORD_PASSPHRASE_RULE("FAIL_REPEAT_SAME_WORD_PASSPHRASE_RULE"),

    @XmlEnumValue("FAIL_ENCRYPTION")
    FAIL_ENCRYPTION("FAIL_ENCRYPTION"),

    @XmlEnumValue("FAIL_DECRYPTION")
    FAIL_DECRYPTION("FAIL_DECRYPTION"),

    @XmlEnumValue("DIRECTORY_NAMING_EXCEPTION")
    DIRECTORY_NAMING_EXCEPTION("DIRECTORY_NAMING_EXCEPTION"),

    @XmlEnumValue("COMMUNICATION_EXCEPTION")
    COMMUNICATION_EXCEPTION("COMMUNICATION_EXCEPTION"),

    @XmlEnumValue("FAIL_CONNECTOR")
    FAIL_CONNECTOR("FAIL_CONNECTOR"),

    @XmlEnumValue("INVALID_ARGUMENTS")
    INVALID_ARGUMENTS("INVALID_ARGUMENTS"),

    @XmlEnumValue("IO_EXCEPTION")
    IO_EXCEPTION("IO_EXCEPTION"),

    @XmlEnumValue("INTERRUPTED_EXCEPTION")
    INTERRUPTED_EXCEPTION("INTERRUPTED_EXCEPTION"),

    @XmlEnumValue("FILE_EXCEPTION")
    FILE_EXCEPTION("FILE_EXCEPTION"),

    @XmlEnumValue("SQL_EXCEPTION")
    SQL_EXCEPTION("SQL_EXCEPTION"),

    @XmlEnumValue("WS_SERVICE_EXCEPTION")
    WS_SERVICE_EXCEPTION("WS_SERVICE_EXCEPTION"),

    @XmlEnumValue("SYNCHRONIZATION_EXCEPTION")
    SYNCHRONIZATION_EXCEPTION("SYNCHRONIZATION_EXCEPTION"),

    @XmlEnumValue("LIMIT_EXCEEDED_EXCEPTION")
    LIMIT_EXCEEDED_EXCEPTION("LIMIT_EXCEEDED_EXCEPTION"),

    @XmlEnumValue("AUTHENTICATION_EXCEPTION")
    AUTHENTICATION_EXCEPTION("AUTHENTICATION_EXCEPTION"),

    @XmlEnumValue("PERMISSION_EXCEPTION")
    PERMISSION_EXCEPTION("PERMISSION_EXCEPTION"),

    @XmlEnumValue("SERVICE_UNAVAILABLE_EXCEPTION")
    SERVICE_UNAVAILABLE_EXCEPTION("SERVICE_UNAVAILABLE_EXCEPTION"),

    @XmlEnumValue("SCHEMA_VIOLATION_EXCEPTION")
    SCHEMA_VIOLATION_EXCEPTION("SCHEMA_VIOLATION_EXCEPTION"),

    @XmlEnumValue("FAIL_PREPROCESSOR")
    FAIL_PREPROCESSOR("FAIL_PREPROCESSOR"),

    @XmlEnumValue("FAIL_POSTPROCESSOR")
    FAIL_POSTPROCESSOR("FAIL_POSTPROCESSOR"),

    @XmlEnumValue("FAIL_PROCESS_ALREADY_RUNNING")
    FAIL_PROCESS_ALREADY_RUNNING("FAIL_PROCESS_ALREADY_RUNNING"),

    @XmlEnumValue("FAIL_OTHER")
    FAIL_OTHER("FAIL_OTHER"),

    @XmlEnumValue("AUTH_PROVIDER_TYPE_NOT_SET")
    AUTH_PROVIDER_TYPE_NOT_SET("AUTH_PROVIDER_TYPE_NOT_SET"),

    @XmlEnumValue("AUTH_ATTRIBUTE_NAME_NOT_SET")
    AUTH_ATTRIBUTE_NAME_NOT_SET("AUTH_PROVIDER_TYPE_NOT_SET"),

    @XmlEnumValue("MANAGED_SYS_NOT_SET")
    MANAGED_SYS_NOT_SET("MANAGED_SYS_NOT_SET"),

    @XmlEnumValue("AUTH_PROVIDER_NAME_NOT_SET")
    AUTH_PROVIDER_NAME_NOT_SET("AUTH_PROVIDER_NAME_NOT_SET"),

    @XmlEnumValue("AUTH_PROVIDER_NOT_SET")
    AUTH_PROVIDER_NOT_SET("AUTH_PROVIDER_NOT_SET"),

    @XmlEnumValue("AUTH_ATTRIBUTE_NOT_SET")
    AUTH_ATTRIBUTE_NOT_SET("AUTH_ATTRIBUTE_NOT_SET"),

    @XmlEnumValue("AUTH_ATTRIBUTE_VALUE_NOT_SET")
    AUTH_ATTRIBUTE_VALUE_NOT_SET("AUTH_ATTRIBUTE_VALUE_NOT_SET"),

    @XmlEnumValue("AUTH_RESOURCE_ATTRIBUTE_NAME_NOT_SET")
    AUTH_RESOURCE_ATTRIBUTE_NAME_NOT_SET("AUTH_RESOURCE_ATTRIBUTE_NAME_NOT_SET"),

    @XmlEnumValue("AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET")
    AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET("AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET"),

    @XmlEnumValue("AUTH_REQUIRED_PROVIDER_ATTRIBUTE_NOT_SET")
    AUTH_REQUIRED_PROVIDER_ATTRIBUTE_NOT_SET("AUTH_REQUIRED_PROVIDER_ATTRIBUTE_NOT_SET"),

    @XmlEnumValue("AUTH_RESOURCE_AM_ATTRIBUTE_NOT_SET")
    AUTH_RESOURCE_AM_ATTRIBUTE_NOT_SET("AUTH_RESOURCE_AM_ATTRIBUTE_NOT_SET"),

    @XmlEnumValue("AUTH_RESOURCE_AM_ATTRIBUTE_ID_NOT_SET")
    AUTH_RESOURCE_AM_ATTRIBUTE_ID_NOT_SET("AUTH_RESOURCE_AM_ATTRIBUTE_ID_NOT_SET"),

    @XmlEnumValue("AUTH_RESOURCE_AM_ATTRIBUTE_NAME_NOT_SET")
    AUTH_RESOURCE_AM_ATTRIBUTE_NAME_NOT_SET("AUTH_RESOURCE_AM_ATTRIBUTE_NAME_NOT_SET"),

    @XmlEnumValue("AUTH_RESOURCE_ATTRIBUTE_MAP_NOT_SET")
    AUTH_RESOURCE_ATTRIBUTE_MAP_NOT_SET("AUTH_RESOURCE_ATTRIBUTE_MAP_NOT_SET"),

    @XmlEnumValue("AUTH_RESOURCE_TARGET_ATTRIBUTE_NOT_SET")
    AUTH_RESOURCE_TARGET_ATTRIBUTE_NOT_SET("AUTH_RESOURCE_TARGET_ATTRIBUTE_NOT_SET"),

    @XmlEnumValue("AUTH_RESOURCE_ATTRIBUTE_MAP_COLLECTION_NOT_SET")
    AUTH_RESOURCE_ATTRIBUTE_MAP_COLLECTION_NOT_SET("AUTH_RESOURCE_ATTRIBUTE_MAP_COLLECTION_NOT_SET"),

    @XmlEnumValue("AUTH_RESOURCE_ATTRIBUTE_MAP_ID_NOT_SET")
    AUTH_RESOURCE_ATTRIBUTE_MAP_ID_NOT_SET("AUTH_RESOURCE_ATTRIBUTE_MAP_ID_NOT_SET"),
    @XmlEnumValue("AUTH_RESOURCE_ATTRIBUTE_TYPE_NOT_SET")
    AUTH_RESOURCE_ATTRIBUTE_TYPE_NOT_SET("AUTH_RESOURCE_ATTRIBUTE_TYPE_NOT_SET"),

    @XmlEnumValue("USER_NOT_SET")
    USER_NOT_SET("USER_NOT_SET"),

    @XmlEnumValue("USER_ATTRIBUTE_NAME_NOT_SET")
    USER_ATTRIBUTE_NAME_NOT_SET("USER_ATTRIBUTE_NAME_NOT_SET"),

    @XmlEnumValue("CONTENT_PROVIDER_NAME_NOT_SET")
    CONTENT_PROVIDER_NAME_NOT_SET("CONTENT_PROVIDER_NAME_NOT_SET"),

    @XmlEnumValue("CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET")
    CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET("CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET"),

    @XmlEnumValue("CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET")
    CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET("CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET"),

    @XmlEnumValue("CONTENT_PROVIDER_SERVER_URL_NOT_SET")
    CONTENT_PROVIDER_SERVER_URL_NOT_SET("CONTENT_PROVIDER_SERVER_URL_NOT_SET"),

    @XmlEnumValue("CONTENT_PROVIDER_NOT_SET")
    CONTENT_PROVIDER_NOT_SET("CONTENT_PROVIDER_NOT_SET"),
    
    @XmlEnumValue("URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND")
	URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND("URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND"),
	
	@XmlEnumValue("URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER")
	URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER("URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER"),
	
	@XmlEnumValue("URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_CP")
	URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_CP("URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_CP"),
	
	@XmlEnumValue("URI_FEDERATION_NOT_ENTITLED_TO_PATTERN")
	URI_FEDERATION_NOT_ENTITLED_TO_PATTERN("URI_FEDERATION_NOT_ENTITLED_TO_PATTERN"),
	
	@XmlEnumValue("URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_PATTERN")
	URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_PATTERN("URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_PATTERN"),
	
	@XmlEnumValue("INVALID_URI")
	INVALID_URI("INVALID_URI"),
	
	@XmlEnumValue("URI_PATTERN_RULE_PROCESS_ERROR")
	URI_PATTERN_RULE_PROCESS_ERROR("URI_PATTERN_RULE_PROCESS_ERROR"),

    @XmlEnumValue("CONTENT_PROVIDER_URI_PATTERN_NOT_SET")
    CONTENT_PROVIDER_URI_PATTERN_NOT_SET("CONTENT_PROVIDER_URI_PATTERN_NOT_SET"),
    @XmlEnumValue("URI_PATTERN_NOT_SET")
    URI_PATTERN_NOT_SET("URI_PATTERN_NOT_SET"),
    @XmlEnumValue("URI_PATTERN_META_TYPE_NOT_SET")
    URI_PATTERN_META_TYPE_NOT_SET("URI_PATTERN_META_TYPE_NOT_SET"),
//    @XmlEnumValue("URI_PATTERN_META_EXISTS")
//    URI_PATTERN_META_EXISTS("URI_PATTERN_META_EXISTS"),

    @XmlEnumValue("URI_PATTERN_META_EXISTS")
    URI_PATTERN_EXISTS("URI_PATTERN_EXISTS"),

    @XmlEnumValue("CONTENT_PROVIDER_SERVER_EXISTS")
    CONTENT_PROVIDER_SERVER_EXISTS("CONTENT_PROVIDER_SERVER_EXISTS"),

    @XmlEnumValue("CONTENT_PROVIDER_WITH_NAME_EXISTS")
    CONTENT_PROVIDER_WITH_NAME_EXISTS("CONTENT_PROVIDER_WITH_NAME_EXISTS"),

    @XmlEnumValue("CONTENT_PROVIDER_DOMAIN_PATTERN_EXISTS")
    CONTENT_PROVIDER_DOMAIN_PATTERN_EXISTS("CONTENT_PROVIDER_DOMAIN_PATTERN_EXISTS"),

    @XmlEnumValue("URL_PATTERN_META_VALUE_NAME_NOT_SET")
    URL_PATTERN_META_VALUE_NAME_NOT_SET("URL_PATTERN_META_VALUE_NAME_NOT_SET"),

    @XmlEnumValue("URL_PATTERN_META_VALUE_MAP_NOT_SET")
    URL_PATTERN_META_VALUE_MAP_NOT_SET("URL_PATTERN_META_VALUE_MAP_NOT_SET"),

    @XmlEnumValue("URI_PATTERN_INVALID")
    URI_PATTERN_INVALID("URI_PATTERN_INVALID"),

    @XmlEnumValue("URI_PATTERN_META_NAME_NOT_SET")
    URI_PATTERN_META_NAME_NOT_SET("URI_PATTERN_META_NAME_NOT_SET"),
    
    @XmlEnumValue("INTERNAL_ERROR")
	INTERNAL_ERROR("INTERNAL_ERROR"),
	
	@XmlEnumValue("LOGIN_EXISTS")
	LOGIN_EXISTS("LOGIN_EXISTS"),

    @XmlEnumValue("MISSING_REQUIRED_ATTRIBUTE")
    MISSING_REQUIRED_ATTRIBUTE("MISSING_REQUIRED_ATTRIBUTE"),
    
    @XmlEnumValue("NO_IDENTITY_QUESTION")
	NO_IDENTITY_QUESTION("NO_IDENTITY_QUESTION"),
	
	@XmlEnumValue("NO_IDENTITY_QUESTION_GROUP")
	NO_IDENTITY_QUESTION_GROUP("NO_IDENTITY_QUESTION_GROUP"),
	
	@XmlEnumValue("NO_ANSWER_TO_QUESTION")
	NO_ANSWER_TO_QUESTION("NO_ANSWER_TO_QUESTION"),
	
	@XmlEnumValue("IDENTICAL_QUESTIONS")
	IDENTICAL_QUESTIONS("IDENTICAL_QUESTIONS"),
	
	@XmlEnumValue("ATTRIBUTE_NAME_MISSING")
	ATTRIBUTE_NAME_MISSING("ATTRIBUTE_NAME_MISSING"),
	
	@XmlEnumValue("METADATA_TYPE_MISSING")
	METADATA_TYPE_MISSING("METADATA_TYPE_MISSING"),

	@XmlEnumValue("CATEGORIES_COLLECTION_CHILDREN")
	CATEGORIES_COLLECTION_CHILDREN("CATEGORIES_COLLECTION_CHILDREN"),

	@XmlEnumValue("UNAUTHORIZED")
	UNAUTHORIZED("UNAUTHORIZED"),
	
	@XmlEnumValue("INVALID_VALUE")
	INVALID_VALUE("INVALID_VALUE"),
	
	@XmlEnumValue("REQUIRED")
	REQUIRED("REQUIRED"),
	
	@XmlEnumValue("FIRST_NAME_REQUIRED")
	FIRST_NAME_REQUIRED("FIRST_NAME_REQUIRED"),
	
	@XmlEnumValue("LAST_NAME_REQUIRED")
	LAST_NAME_REQUIRED("LAST_NAME_REQUIRED"),
	
	@XmlEnumValue("EMAIL_REQUIRED")
	EMAIL_REQUIRED("EMAIL_REQUIRED"),
	
	@XmlEnumValue("LOGIN_REQUIRED")
	LOGIN_REQUIRED("LOGIN_REQUIRED"),
	
	@XmlEnumValue("SEND_EMAIL_FAILED")
	SEND_EMAIL_FAILED("SEND_EMAIL_FAILED"),
	
	@XmlEnumValue("LINKED_TO_AUTHENTICATION_PROVIDER")
	LINKED_TO_AUTHENTICATION_PROVIDER("LINKED_TO_AUTHENTICATION_PROVIDER"),
	
	@XmlEnumValue("LINKED_TO_CONTENT_PROVIDER")
	LINKED_TO_CONTENT_PROVIDER("LINKED_TO_CONTENT_PROVIDER"),
	
	@XmlEnumValue("LINKED_TO_URI_PATTERN")
	LINKED_TO_URI_PATTERN("LINKED_TO_URI_PATTERN"),
	
	@XmlEnumValue("LINKED_TO_METADATA_ELEMENT")
	LINKED_TO_METADATA_ELEMENT("LINKED_TO_METADATA_ELEMENT"),
	
	@XmlEnumValue("LINKED_TO_PAGE_TEMPLATE")
	LINKED_TO_PAGE_TEMPLATE("LINKED_TO_PAGE_TEMPLATE"),

    @XmlEnumValue("ORGANIZATION_NAME_NOT_SET")
    ORGANIZATION_NAME_NOT_SET("ORGANIZATION_NAME_NOT_SET"),

    @XmlEnumValue("POLICY_NAME_NOT_SET")
    POLICY_NAME_NOT_SET("POLICY_NAME_NOT_SET"),
    
    @XmlEnumValue("ORGANIZATION_TYPE_NOT_SET")
    ORGANIZATION_TYPE_NOT_SET("ORGANIZATION_TYPE_NOT_SET"),

    @XmlEnumValue("CLASSIFICATION_NOT_SET")
    CLASSIFICATION_NOT_SET("CLASSIFICATION_NOT_SET"),
    
    @XmlEnumValue("NO_APPROVER_ASSOCIATIONS")
	NO_APPROVER_ASSOCIATIONS("NO_APPROVER_ASSOCIATIONS"),
    
    @XmlEnumValue("NO_REQUEST_APPROVERS")
	NO_REQUEST_APPROVERS("NO_REQUEST_APPROVERS"),
	
	@XmlEnumValue("REQUEST_APPROVERS_NOT_SET")
	REQUEST_APPROVERS_NOT_SET("REQUEST_APPROVERS_NOT_SET"),

    @XmlEnumValue("REQUEST_APPROVERS_NOT_SET")
    OPERATION_NOT_SUPPORTED("REQUEST_APPROVERS_NOT_SET");
	
    private final String value;

    ResponseCode(String val) {
        value = val;
    }

}
