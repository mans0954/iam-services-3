
package org.openiam.idm.srvc.key.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResponseErrorCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ResponseErrorCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="resourcePropResourceIdMissing"/>
 *     &lt;enumeration value="resourcePropValueMissing"/>
 *     &lt;enumeration value="resourcePropMissing"/>
 *     &lt;enumeration value="hangingChildren"/>
 *     &lt;enumeration value="hangingGroups"/>
 *     &lt;enumeration value="hangingRoles"/>
 *     &lt;enumeration value="menuDoesNotExist"/>
 *     &lt;enumeration value="invalidResourceType"/>
 *     &lt;enumeration value="invalidRoleDomain"/>
 *     &lt;enumeration value="noName"/>
 *     &lt;enumeration value="nameTaken"/>
 *     &lt;enumeration value="READONLY"/>
 *     &lt;enumeration value="RULE_NOT_SET"/>
 *     &lt;enumeration value="membershipExists"/>
 *     &lt;enumeration value="resourceTypesNotEqual"/>
 *     &lt;enumeration value="relationshipExists"/>
 *     &lt;enumeration value="cantAddYourselfAsChild"/>
 *     &lt;enumeration value="RESOURCE_TYPE_NOT_SUPPORTS_HIERARCHY"/>
 *     &lt;enumeration value="circularDependency"/>
 *     &lt;enumeration value="objectNotFound"/>
 *     &lt;enumeration value="questNotSelected"/>
 *     &lt;enumeration value="answerNotTaken"/>
 *     &lt;enumeration value="hangingChildRoles"/>
 *     &lt;enumeration value="hangingRoleGroups"/>
 *     &lt;enumeration value="hangingRoleResources"/>
 *     &lt;enumeration value="hangingRoleUsers"/>
 *     &lt;enumeration value="classNotFound"/>
 *     &lt;enumeration value="principalNotFound"/>
 *     &lt;enumeration value="userNotFound"/>
 *     &lt;enumeration value="userStatus"/>
 *     &lt;enumeration value="supervisorlNotFound"/>
 *     &lt;enumeration value="supervisorlError"/>
 *     &lt;enumeration value="DUPLICATE_PRINCIPAL"/>
 *     &lt;enumeration value="failPasswordPolicy"/>
 *     &lt;enumeration value="groupIdNull"/>
 *     &lt;enumeration value="groupIdInvalid"/>
 *     &lt;enumeration value="roleIdNull"/>
 *     &lt;enumeration value="roleIdInvalid"/>
 *     &lt;enumeration value="objectIdInvalid"/>
 *     &lt;enumeration value="success"/>
 *     &lt;enumeration value="FAIL_SQL_ERORR"/>
 *     &lt;enumeration value="FAIL_CONNECTION"/>
 *     &lt;enumeration value="FAIL_ALPHA_CHAR_RULE"/>
 *     &lt;enumeration value="FAIL_LOWER_CASE_RULE"/>
 *     &lt;enumeration value="FAIL_UPPER_CASE_RULE"/>
 *     &lt;enumeration value="FAIL_NON_APHANUMERIC_RULE"/>
 *     &lt;enumeration value="FAIL_NUMERIC_CHAR_RULE"/>
 *     &lt;enumeration value="FAIL_HISTORY_RULE"/>
 *     &lt;enumeration value="FAIL_LENGTH_RULE"/>
 *     &lt;enumeration value="FAIL_NEQ_NAME"/>
 *     &lt;enumeration value="FAIL_NEQ_PASSWORD"/>
 *     &lt;enumeration value="FAIL_NEQ_PRINCIPAL"/>
 *     &lt;enumeration value="FAIL_PASSWORD_CHANGE_FREQUENCY"/>
 *     &lt;enumeration value="PASSWORD_POLICY_NOT_FOUND"/>
 *     &lt;enumeration value="FAIL_PASSWORD_CHANGE_ALLOW"/>
 *     &lt;enumeration value="FAIL_REJECT_CHARS_IN_PSWD"/>
 *     &lt;enumeration value="FAIL_MIN_WORDS_PASSPHRASE_RULE"/>
 *     &lt;enumeration value="FAIL_REPEAT_SAME_WORD_PASSPHRASE_RULE"/>
 *     &lt;enumeration value="FAIL_ENCRYPTION"/>
 *     &lt;enumeration value="FAIL_DECRYPTION"/>
 *     &lt;enumeration value="DIRECTORY_NAMING_EXCEPTION"/>
 *     &lt;enumeration value="COMMUNICATION_EXCEPTION"/>
 *     &lt;enumeration value="FAIL_CONNECTOR"/>
 *     &lt;enumeration value="INVALID_ARGUMENTS"/>
 *     &lt;enumeration value="IO_EXCEPTION"/>
 *     &lt;enumeration value="INTERRUPTED_EXCEPTION"/>
 *     &lt;enumeration value="FILE_EXCEPTION"/>
 *     &lt;enumeration value="SQL_EXCEPTION"/>
 *     &lt;enumeration value="WS_SERVICE_EXCEPTION"/>
 *     &lt;enumeration value="SYNCHRONIZATION_EXCEPTION"/>
 *     &lt;enumeration value="SYNCHRONIZATION_PRE_SRIPT_FAILURE"/>
 *     &lt;enumeration value="SYNCHRONIZATION_POST_SRIPT_FAILURE"/>
 *     &lt;enumeration value="LIMIT_EXCEEDED_EXCEPTION"/>
 *     &lt;enumeration value="AUTHENTICATION_EXCEPTION"/>
 *     &lt;enumeration value="PERMISSION_EXCEPTION"/>
 *     &lt;enumeration value="SERVICE_UNAVAILABLE_EXCEPTION"/>
 *     &lt;enumeration value="SCHEMA_VIOLATION_EXCEPTION"/>
 *     &lt;enumeration value="FAIL_PREPROCESSOR"/>
 *     &lt;enumeration value="FAIL_POSTPROCESSOR"/>
 *     &lt;enumeration value="FAIL_PROCESS_ALREADY_RUNNING"/>
 *     &lt;enumeration value="FAIL_PROCESS_INACTIVE"/>
 *     &lt;enumeration value="FAIL_OTHER"/>
 *     &lt;enumeration value="AUTH_PROVIDER_TYPE_NOT_SET"/>
 *     &lt;enumeration value="AUTH_ATTRIBUTE_NAME_NOT_SET"/>
 *     &lt;enumeration value="MANAGED_SYS_NOT_SET"/>
 *     &lt;enumeration value="AUTH_PROVIDER_NAME_NOT_SET"/>
 *     &lt;enumeration value="AUTH_PROVIDER_NOT_SET"/>
 *     &lt;enumeration value="AUTH_ATTRIBUTE_NOT_SET"/>
 *     &lt;enumeration value="AUTH_ATTRIBUTE_VALUE_NOT_SET"/>
 *     &lt;enumeration value="AUTH_RESOURCE_ATTRIBUTE_NAME_NOT_SET"/>
 *     &lt;enumeration value="AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET"/>
 *     &lt;enumeration value="AUTH_REQUIRED_PROVIDER_ATTRIBUTE_NOT_SET"/>
 *     &lt;enumeration value="AUTH_RESOURCE_AM_ATTRIBUTE_NOT_SET"/>
 *     &lt;enumeration value="AUTH_RESOURCE_AM_ATTRIBUTE_ID_NOT_SET"/>
 *     &lt;enumeration value="AUTH_RESOURCE_AM_ATTRIBUTE_ID_NOT_SET"/>
 *     &lt;enumeration value="AUTH_RESOURCE_AM_ATTRIBUTE_NAME_NOT_SET"/>
 *     &lt;enumeration value="AUTH_RESOURCE_ATTRIBUTE_MAP_NOT_SET"/>
 *     &lt;enumeration value="AUTH_RESOURCE_TARGET_ATTRIBUTE_NOT_SET"/>
 *     &lt;enumeration value="AUTH_RESOURCE_ATTRIBUTE_MAP_COLLECTION_NOT_SET"/>
 *     &lt;enumeration value="AUTH_RESOURCE_ATTRIBUTE_MAP_ID_NOT_SET"/>
 *     &lt;enumeration value="AUTH_RESOURCE_ATTRIBUTE_TYPE_NOT_SET"/>
 *     &lt;enumeration value="USER_NOT_SET"/>
 *     &lt;enumeration value="USER_ATTRIBUTE_NAME_NOT_SET"/>
 *     &lt;enumeration value="CONTENT_PROVIDER_NAME_NOT_SET"/>
 *     &lt;enumeration value="CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET"/>
 *     &lt;enumeration value="CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET"/>
 *     &lt;enumeration value="CONTENT_PROVIDER_SERVER_URL_NOT_SET"/>
 *     &lt;enumeration value="CONTENT_PROVIDER_NOT_SET"/>
 *     &lt;enumeration value="URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND"/>
 *     &lt;enumeration value="URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER"/>
 *     &lt;enumeration value="URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_CP"/>
 *     &lt;enumeration value="URI_FEDERATION_NOT_ENTITLED_TO_PATTERN"/>
 *     &lt;enumeration value="URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_PATTERN"/>
 *     &lt;enumeration value="INVALID_URI"/>
 *     &lt;enumeration value="URI_PATTERN_RULE_PROCESS_ERROR"/>
 *     &lt;enumeration value="CONTENT_PROVIDER_URI_PATTERN_NOT_SET"/>
 *     &lt;enumeration value="URI_PATTERN_NOT_SET"/>
 *     &lt;enumeration value="URI_PATTERN_META_TYPE_NOT_SET"/>
 *     &lt;enumeration value="URI_PATTERN_META_EXISTS"/>
 *     &lt;enumeration value="CONTENT_PROVIDER_SERVER_EXISTS"/>
 *     &lt;enumeration value="CONTENT_PROVIDER_WITH_NAME_EXISTS"/>
 *     &lt;enumeration value="CONTENT_PROVIDER_DOMAIN_PATTERN_EXISTS"/>
 *     &lt;enumeration value="URL_PATTERN_META_VALUE_NAME_NOT_SET"/>
 *     &lt;enumeration value="URL_PATTERN_META_VALUE_MAP_NOT_SET"/>
 *     &lt;enumeration value="URI_PATTERN_INVALID"/>
 *     &lt;enumeration value="URI_PATTERN_META_NAME_NOT_SET"/>
 *     &lt;enumeration value="INTERNAL_ERROR"/>
 *     &lt;enumeration value="LOGIN_EXISTS"/>
 *     &lt;enumeration value="MISSING_REQUIRED_ATTRIBUTE"/>
 *     &lt;enumeration value="NO_IDENTITY_QUESTION"/>
 *     &lt;enumeration value="NO_IDENTITY_QUESTION_GROUP"/>
 *     &lt;enumeration value="NO_ANSWER_TO_QUESTION"/>
 *     &lt;enumeration value="IDENTICAL_QUESTIONS"/>
 *     &lt;enumeration value="IDENTITY_NOT_FOUND"/>
 *     &lt;enumeration value="ATTRIBUTE_NAME_MISSING"/>
 *     &lt;enumeration value="METADATA_TYPE_MISSING"/>
 *     &lt;enumeration value="CATEGORIES_COLLECTION_CHILDREN"/>
 *     &lt;enumeration value="UNAUTHORIZED"/>
 *     &lt;enumeration value="INVALID_VALUE"/>
 *     &lt;enumeration value="REQUIRED"/>
 *     &lt;enumeration value="FIRST_NAME_REQUIRED"/>
 *     &lt;enumeration value="LAST_NAME_REQUIRED"/>
 *     &lt;enumeration value="EMAIL_REQUIRED"/>
 *     &lt;enumeration value="LOGIN_REQUIRED"/>
 *     &lt;enumeration value="SEND_EMAIL_FAILED"/>
 *     &lt;enumeration value="LINKED_TO_AUTHENTICATION_PROVIDER"/>
 *     &lt;enumeration value="LINKED_TO_CONTENT_PROVIDER"/>
 *     &lt;enumeration value="LINKED_TO_URI_PATTERN"/>
 *     &lt;enumeration value="LINKED_TO_METADATA_ELEMENT"/>
 *     &lt;enumeration value="LINKED_TO_MANAGED_SYSTEM"/>
 *     &lt;enumeration value="LINKED_TO_PAGE_TEMPLATE"/>
 *     &lt;enumeration value="RESOURCE_IS_AN_ADMIN_OF_RESOURCE"/>
 *     &lt;enumeration value="RESOURCE_IS_AN_ADMIN_OF_ROLE"/>
 *     &lt;enumeration value="RESOURCE_IS_AN_ADMIN_OF_GROUP"/>
 *     &lt;enumeration value="RESOURCE_IS_AN_ADMIN_OF_ORG"/>
 *     &lt;enumeration value="ORGANIZATION_NAME_NOT_SET"/>
 *     &lt;enumeration value="NAME_MISSING"/>
 *     &lt;enumeration value="URL_REQUIRED"/>
 *     &lt;enumeration value="REPORT_NAME_NOT_SET"/>
 *     &lt;enumeration value="REPORT_PARAM_NAME_NOT_SET"/>
 *     &lt;enumeration value="REPORT_PARAM_TYPE_NOT_SET"/>
 *     &lt;enumeration value="REPORT_NOT_SET"/>
 *     &lt;enumeration value="REPORT_PARAMETER_EXISTS"/>
 *     &lt;enumeration value="REPORT_DATASOURCE_NOT_SET"/>
 *     &lt;enumeration value="REPORT_URL_NOT_SET"/>
 *     &lt;enumeration value="SUBSCRIBED_VALUE_NOT_SET"/>
 *     &lt;enumeration value="SUBSCRIBED_DELIVERY_METHOD_NOT_SET"/>
 *     &lt;enumeration value="SUBSCRIBED_DELIVERY_AUDIENCE_NOT_SET"/>
 *     &lt;enumeration value="SUBSCRIBED_DELIVERY_FORMAT_NOT_SET"/>
 *     &lt;enumeration value="SUBSCRIBED_ID_NOT_SET"/>
 *     &lt;enumeration value="SUBSCRIBED_NAME_NOT_SET"/>
 *     &lt;enumeration value="POLICY_NAME_NOT_SET"/>
 *     &lt;enumeration value="ORGANIZATION_TYPE_NOT_SET"/>
 *     &lt;enumeration value="CLASSIFICATION_NOT_SET"/>
 *     &lt;enumeration value="NO_APPROVER_ASSOCIATIONS"/>
 *     &lt;enumeration value="NO_REQUEST_APPROVERS"/>
 *     &lt;enumeration value="REQUEST_APPROVERS_NOT_SET"/>
 *     &lt;enumeration value="MANAGED_SYSTEM_NOT_SET"/>
 *     &lt;enumeration value="IT_POLICY_EXISTS"/>
 *     &lt;enumeration value="ORGANIZATION_TYPE_CHILDREN_EXIST"/>
 *     &lt;enumeration value="ORGANIZATION_TYPE_PARENTS_EXIST"/>
 *     &lt;enumeration value="ORGANIZATION_TYPE_TIED_TO_ORGANIZATION"/>
 *     &lt;enumeration value="NO_EXEUCUTION_TIME"/>
 *     &lt;enumeration value="INVALID_CRON_EXRPESSION"/>
 *     &lt;enumeration value="DATE_INVALID"/>
 *     &lt;enumeration value="FILE_DOES_NOT_EXIST"/>
 *     &lt;enumeration value="SPRING_BEAN_OR_SCRIPT_REQUIRED"/>
 *     &lt;enumeration value="INVALID_SPRING_BEAN"/>
 *     &lt;enumeration value="TEMPLATE_TYPE_REQUIRED"/>
 *     &lt;enumeration value="ADDRESS_TYPE_REQUIRED"/>
 *     &lt;enumeration value="EMAIL_ADDRESS_TYPE_REQUIRED"/>
 *     &lt;enumeration value="PHONE_TYPE_REQUIRED"/>
 *     &lt;enumeration value="PHONE_TYPE_DUPLICATED"/>
 *     &lt;enumeration value="EMAIL_ADDRESS_TYPE_DUPLICATED"/>
 *     &lt;enumeration value="ADDRESS_TYPE_DUPLICATED"/>
 *     &lt;enumeration value="VALIDATION_ERROR"/>
 *     &lt;enumeration value="META_NAME_MISSING"/>
 *     &lt;enumeration value="META_VALUE_MISSING"/>
 *     &lt;enumeration value="CONNECTOR_REQUIRED"/>
 *     &lt;enumeration value="FAIL_LIMIT_NUM_REPEAT_CHAR"/>
 *     &lt;enumeration value="NOT_ALLOWED_ROLE_IN_SEARCH"/>
 *     &lt;enumeration value="NOT_ALLOWED_GROUP_IN_SEARCH"/>
 *     &lt;enumeration value="NOT_ALLOWED_ORGANIZATION_IN_SEARCH"/>
 *     &lt;enumeration value="INVALID_USER_SEARCH_REQUEST"/>
 *     &lt;enumeration value="NO_SUBJECT"/>
 *     &lt;enumeration value="NO_SSO_TOKEN"/>
 *     &lt;enumeration value="AUTH_LEVEL_GROUPING_HAS_PATTERNS"/>
 *     &lt;enumeration value="AUTH_LEVEL_GROUPING_HAS_CONTENT_PROVIDERS"/>
 *     &lt;enumeration value="METATYPE_LINKED_WITH_METAELEMENT"/>
 *     &lt;enumeration value="TYPE_REQUIRED"/>
 *     &lt;enumeration value="GROUPING_REQUIRED"/>
 *     &lt;enumeration value="VALUE_REQUIRED"/>
 *     &lt;enumeration value="DISPLAY_NAME_REQUIRED"/>
 *     &lt;enumeration value="LOCALE_ALREADY_EXISTS"/>
 *     &lt;enumeration value="NO_DEFAULT_LANGUAGE"/>
 *     &lt;enumeration value="LANGUAGE_CODE_MISSING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ResponseErrorCode")
@XmlEnum
public enum ResponseErrorCode {

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
    READONLY("READONLY"),
    RULE_NOT_SET("RULE_NOT_SET"),
    @XmlEnumValue("membershipExists")
    MEMBERSHIP_EXISTS("membershipExists"),
    @XmlEnumValue("resourceTypesNotEqual")
    RESOURCE_TYPES_NOT_EQUAL("resourceTypesNotEqual"),
    @XmlEnumValue("relationshipExists")
    RELATIONSHIP_EXISTS("relationshipExists"),
    @XmlEnumValue("cantAddYourselfAsChild")
    CANT_ADD_YOURSELF_AS_CHILD("cantAddYourselfAsChild"),
    RESOURCE_TYPE_NOT_SUPPORTS_HIERARCHY("RESOURCE_TYPE_NOT_SUPPORTS_HIERARCHY"),
    @XmlEnumValue("circularDependency")
    CIRCULAR_DEPENDENCY("circularDependency"),
    @XmlEnumValue("objectNotFound")
    OBJECT_NOT_FOUND("objectNotFound"),
    @XmlEnumValue("questNotSelected")
    QUEST_NOT_SELECTED("questNotSelected"),
    @XmlEnumValue("answerNotTaken")
    ANSWER_NOT_TAKEN("answerNotTaken"),
    @XmlEnumValue("hangingChildRoles")
    HANGING_CHILD_ROLES("hangingChildRoles"),
    @XmlEnumValue("hangingRoleGroups")
    HANGING_ROLE_GROUPS("hangingRoleGroups"),
    @XmlEnumValue("hangingRoleResources")
    HANGING_ROLE_RESOURCES("hangingRoleResources"),
    @XmlEnumValue("hangingRoleUsers")
    HANGING_ROLE_USERS("hangingRoleUsers"),
    @XmlEnumValue("classNotFound")
    CLASS_NOT_FOUND("classNotFound"),
    @XmlEnumValue("principalNotFound")
    PRINCIPAL_NOT_FOUND("principalNotFound"),
    @XmlEnumValue("userNotFound")
    USER_NOT_FOUND("userNotFound"),
    @XmlEnumValue("userStatus")
    USER_STATUS("userStatus"),
    @XmlEnumValue("supervisorlNotFound")
    SUPERVISORL_NOT_FOUND("supervisorlNotFound"),
    @XmlEnumValue("supervisorlError")
    SUPERVISORL_ERROR("supervisorlError"),
    DUPLICATE_PRINCIPAL("DUPLICATE_PRINCIPAL"),
    @XmlEnumValue("failPasswordPolicy")
    FAIL_PASSWORD_POLICY("failPasswordPolicy"),
    @XmlEnumValue("groupIdNull")
    GROUP_ID_NULL("groupIdNull"),
    @XmlEnumValue("groupIdInvalid")
    GROUP_ID_INVALID("groupIdInvalid"),
    @XmlEnumValue("roleIdNull")
    ROLE_ID_NULL("roleIdNull"),
    @XmlEnumValue("roleIdInvalid")
    ROLE_ID_INVALID("roleIdInvalid"),
    @XmlEnumValue("objectIdInvalid")
    OBJECT_ID_INVALID("objectIdInvalid"),
    @XmlEnumValue("success")
    SUCCESS("success"),
    FAIL_SQL_ERORR("FAIL_SQL_ERORR"),
    FAIL_CONNECTION("FAIL_CONNECTION"),
    FAIL_ALPHA_CHAR_RULE("FAIL_ALPHA_CHAR_RULE"),
    FAIL_LOWER_CASE_RULE("FAIL_LOWER_CASE_RULE"),
    FAIL_UPPER_CASE_RULE("FAIL_UPPER_CASE_RULE"),
    FAIL_NON_APHANUMERIC_RULE("FAIL_NON_APHANUMERIC_RULE"),
    FAIL_NUMERIC_CHAR_RULE("FAIL_NUMERIC_CHAR_RULE"),
    FAIL_HISTORY_RULE("FAIL_HISTORY_RULE"),
    FAIL_LENGTH_RULE("FAIL_LENGTH_RULE"),
    FAIL_NEQ_NAME("FAIL_NEQ_NAME"),
    FAIL_NEQ_PASSWORD("FAIL_NEQ_PASSWORD"),
    FAIL_NEQ_PRINCIPAL("FAIL_NEQ_PRINCIPAL"),
    FAIL_PASSWORD_CHANGE_FREQUENCY("FAIL_PASSWORD_CHANGE_FREQUENCY"),
    PASSWORD_POLICY_NOT_FOUND("PASSWORD_POLICY_NOT_FOUND"),
    FAIL_PASSWORD_CHANGE_ALLOW("FAIL_PASSWORD_CHANGE_ALLOW"),
    FAIL_REJECT_CHARS_IN_PSWD("FAIL_REJECT_CHARS_IN_PSWD"),
    FAIL_MIN_WORDS_PASSPHRASE_RULE("FAIL_MIN_WORDS_PASSPHRASE_RULE"),
    FAIL_REPEAT_SAME_WORD_PASSPHRASE_RULE("FAIL_REPEAT_SAME_WORD_PASSPHRASE_RULE"),
    FAIL_ENCRYPTION("FAIL_ENCRYPTION"),
    FAIL_DECRYPTION("FAIL_DECRYPTION"),
    DIRECTORY_NAMING_EXCEPTION("DIRECTORY_NAMING_EXCEPTION"),
    COMMUNICATION_EXCEPTION("COMMUNICATION_EXCEPTION"),
    FAIL_CONNECTOR("FAIL_CONNECTOR"),
    INVALID_ARGUMENTS("INVALID_ARGUMENTS"),
    IO_EXCEPTION("IO_EXCEPTION"),
    INTERRUPTED_EXCEPTION("INTERRUPTED_EXCEPTION"),
    FILE_EXCEPTION("FILE_EXCEPTION"),
    SQL_EXCEPTION("SQL_EXCEPTION"),
    WS_SERVICE_EXCEPTION("WS_SERVICE_EXCEPTION"),
    SYNCHRONIZATION_EXCEPTION("SYNCHRONIZATION_EXCEPTION"),
    SYNCHRONIZATION_PRE_SRIPT_FAILURE("SYNCHRONIZATION_PRE_SRIPT_FAILURE"),
    SYNCHRONIZATION_POST_SRIPT_FAILURE("SYNCHRONIZATION_POST_SRIPT_FAILURE"),
    LIMIT_EXCEEDED_EXCEPTION("LIMIT_EXCEEDED_EXCEPTION"),
    AUTHENTICATION_EXCEPTION("AUTHENTICATION_EXCEPTION"),
    PERMISSION_EXCEPTION("PERMISSION_EXCEPTION"),
    SERVICE_UNAVAILABLE_EXCEPTION("SERVICE_UNAVAILABLE_EXCEPTION"),
    SCHEMA_VIOLATION_EXCEPTION("SCHEMA_VIOLATION_EXCEPTION"),
    FAIL_PREPROCESSOR("FAIL_PREPROCESSOR"),
    FAIL_POSTPROCESSOR("FAIL_POSTPROCESSOR"),
    FAIL_PROCESS_ALREADY_RUNNING("FAIL_PROCESS_ALREADY_RUNNING"),
    FAIL_PROCESS_INACTIVE("FAIL_PROCESS_INACTIVE"),
    FAIL_OTHER("FAIL_OTHER"),
    AUTH_PROVIDER_TYPE_NOT_SET("AUTH_PROVIDER_TYPE_NOT_SET"),
    AUTH_ATTRIBUTE_NAME_NOT_SET("AUTH_ATTRIBUTE_NAME_NOT_SET"),
    MANAGED_SYS_NOT_SET("MANAGED_SYS_NOT_SET"),
    AUTH_PROVIDER_NAME_NOT_SET("AUTH_PROVIDER_NAME_NOT_SET"),
    AUTH_PROVIDER_NOT_SET("AUTH_PROVIDER_NOT_SET"),
    AUTH_ATTRIBUTE_NOT_SET("AUTH_ATTRIBUTE_NOT_SET"),
    AUTH_ATTRIBUTE_VALUE_NOT_SET("AUTH_ATTRIBUTE_VALUE_NOT_SET"),
    AUTH_RESOURCE_ATTRIBUTE_NAME_NOT_SET("AUTH_RESOURCE_ATTRIBUTE_NAME_NOT_SET"),
    AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET("AUTH_PROVIDER_SECUTITY_KEYS_NOT_SET"),
    AUTH_REQUIRED_PROVIDER_ATTRIBUTE_NOT_SET("AUTH_REQUIRED_PROVIDER_ATTRIBUTE_NOT_SET"),
    AUTH_RESOURCE_AM_ATTRIBUTE_NOT_SET("AUTH_RESOURCE_AM_ATTRIBUTE_NOT_SET"),
    AUTH_RESOURCE_AM_ATTRIBUTE_ID_NOT_SET("AUTH_RESOURCE_AM_ATTRIBUTE_ID_NOT_SET"),
    AUTH_RESOURCE_AM_ATTRIBUTE_NAME_NOT_SET("AUTH_RESOURCE_AM_ATTRIBUTE_NAME_NOT_SET"),
    AUTH_RESOURCE_ATTRIBUTE_MAP_NOT_SET("AUTH_RESOURCE_ATTRIBUTE_MAP_NOT_SET"),
    AUTH_RESOURCE_TARGET_ATTRIBUTE_NOT_SET("AUTH_RESOURCE_TARGET_ATTRIBUTE_NOT_SET"),
    AUTH_RESOURCE_ATTRIBUTE_MAP_COLLECTION_NOT_SET("AUTH_RESOURCE_ATTRIBUTE_MAP_COLLECTION_NOT_SET"),
    AUTH_RESOURCE_ATTRIBUTE_MAP_ID_NOT_SET("AUTH_RESOURCE_ATTRIBUTE_MAP_ID_NOT_SET"),
    AUTH_RESOURCE_ATTRIBUTE_TYPE_NOT_SET("AUTH_RESOURCE_ATTRIBUTE_TYPE_NOT_SET"),
    USER_NOT_SET("USER_NOT_SET"),
    USER_ATTRIBUTE_NAME_NOT_SET("USER_ATTRIBUTE_NAME_NOT_SET"),
    CONTENT_PROVIDER_NAME_NOT_SET("CONTENT_PROVIDER_NAME_NOT_SET"),
    CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET("CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET"),
    CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET("CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET"),
    CONTENT_PROVIDER_SERVER_URL_NOT_SET("CONTENT_PROVIDER_SERVER_URL_NOT_SET"),
    CONTENT_PROVIDER_NOT_SET("CONTENT_PROVIDER_NOT_SET"),
    URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND("URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND"),
    URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER("URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER"),
    URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_CP("URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_CP"),
    URI_FEDERATION_NOT_ENTITLED_TO_PATTERN("URI_FEDERATION_NOT_ENTITLED_TO_PATTERN"),
    URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_PATTERN("URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_PATTERN"),
    INVALID_URI("INVALID_URI"),
    URI_PATTERN_RULE_PROCESS_ERROR("URI_PATTERN_RULE_PROCESS_ERROR"),
    CONTENT_PROVIDER_URI_PATTERN_NOT_SET("CONTENT_PROVIDER_URI_PATTERN_NOT_SET"),
    URI_PATTERN_NOT_SET("URI_PATTERN_NOT_SET"),
    URI_PATTERN_META_TYPE_NOT_SET("URI_PATTERN_META_TYPE_NOT_SET"),
    URI_PATTERN_META_EXISTS("URI_PATTERN_META_EXISTS"),
    CONTENT_PROVIDER_SERVER_EXISTS("CONTENT_PROVIDER_SERVER_EXISTS"),
    CONTENT_PROVIDER_WITH_NAME_EXISTS("CONTENT_PROVIDER_WITH_NAME_EXISTS"),
    CONTENT_PROVIDER_DOMAIN_PATTERN_EXISTS("CONTENT_PROVIDER_DOMAIN_PATTERN_EXISTS"),
    URL_PATTERN_META_VALUE_NAME_NOT_SET("URL_PATTERN_META_VALUE_NAME_NOT_SET"),
    URL_PATTERN_META_VALUE_MAP_NOT_SET("URL_PATTERN_META_VALUE_MAP_NOT_SET"),
    URI_PATTERN_INVALID("URI_PATTERN_INVALID"),
    URI_PATTERN_META_NAME_NOT_SET("URI_PATTERN_META_NAME_NOT_SET"),
    INTERNAL_ERROR("INTERNAL_ERROR"),
    LOGIN_EXISTS("LOGIN_EXISTS"),
    MISSING_REQUIRED_ATTRIBUTE("MISSING_REQUIRED_ATTRIBUTE"),
    NO_IDENTITY_QUESTION("NO_IDENTITY_QUESTION"),
    NO_IDENTITY_QUESTION_GROUP("NO_IDENTITY_QUESTION_GROUP"),
    NO_ANSWER_TO_QUESTION("NO_ANSWER_TO_QUESTION"),
    IDENTICAL_QUESTIONS("IDENTICAL_QUESTIONS"),
    IDENTITY_NOT_FOUND("IDENTITY_NOT_FOUND"),
    ATTRIBUTE_NAME_MISSING("ATTRIBUTE_NAME_MISSING"),
    METADATA_TYPE_MISSING("METADATA_TYPE_MISSING"),
    CATEGORIES_COLLECTION_CHILDREN("CATEGORIES_COLLECTION_CHILDREN"),
    UNAUTHORIZED("UNAUTHORIZED"),
    INVALID_VALUE("INVALID_VALUE"),
    REQUIRED("REQUIRED"),
    FIRST_NAME_REQUIRED("FIRST_NAME_REQUIRED"),
    LAST_NAME_REQUIRED("LAST_NAME_REQUIRED"),
    EMAIL_REQUIRED("EMAIL_REQUIRED"),
    LOGIN_REQUIRED("LOGIN_REQUIRED"),
    SEND_EMAIL_FAILED("SEND_EMAIL_FAILED"),
    LINKED_TO_AUTHENTICATION_PROVIDER("LINKED_TO_AUTHENTICATION_PROVIDER"),
    LINKED_TO_CONTENT_PROVIDER("LINKED_TO_CONTENT_PROVIDER"),
    LINKED_TO_URI_PATTERN("LINKED_TO_URI_PATTERN"),
    LINKED_TO_METADATA_ELEMENT("LINKED_TO_METADATA_ELEMENT"),
    LINKED_TO_MANAGED_SYSTEM("LINKED_TO_MANAGED_SYSTEM"),
    LINKED_TO_PAGE_TEMPLATE("LINKED_TO_PAGE_TEMPLATE"),
    RESOURCE_IS_AN_ADMIN_OF_RESOURCE("RESOURCE_IS_AN_ADMIN_OF_RESOURCE"),
    RESOURCE_IS_AN_ADMIN_OF_ROLE("RESOURCE_IS_AN_ADMIN_OF_ROLE"),
    RESOURCE_IS_AN_ADMIN_OF_GROUP("RESOURCE_IS_AN_ADMIN_OF_GROUP"),
    RESOURCE_IS_AN_ADMIN_OF_ORG("RESOURCE_IS_AN_ADMIN_OF_ORG"),
    ORGANIZATION_NAME_NOT_SET("ORGANIZATION_NAME_NOT_SET"),
    NAME_MISSING("NAME_MISSING"),
    URL_REQUIRED("URL_REQUIRED"),
    REPORT_NAME_NOT_SET("REPORT_NAME_NOT_SET"),
    REPORT_PARAM_NAME_NOT_SET("REPORT_PARAM_NAME_NOT_SET"),
    REPORT_PARAM_TYPE_NOT_SET("REPORT_PARAM_TYPE_NOT_SET"),
    REPORT_NOT_SET("REPORT_NOT_SET"),
    REPORT_PARAMETER_EXISTS("REPORT_PARAMETER_EXISTS"),
    REPORT_DATASOURCE_NOT_SET("REPORT_DATASOURCE_NOT_SET"),
    REPORT_URL_NOT_SET("REPORT_URL_NOT_SET"),
    SUBSCRIBED_VALUE_NOT_SET("SUBSCRIBED_VALUE_NOT_SET"),
    SUBSCRIBED_DELIVERY_METHOD_NOT_SET("SUBSCRIBED_DELIVERY_METHOD_NOT_SET"),
    SUBSCRIBED_DELIVERY_AUDIENCE_NOT_SET("SUBSCRIBED_DELIVERY_AUDIENCE_NOT_SET"),
    SUBSCRIBED_DELIVERY_FORMAT_NOT_SET("SUBSCRIBED_DELIVERY_FORMAT_NOT_SET"),
    SUBSCRIBED_ID_NOT_SET("SUBSCRIBED_ID_NOT_SET"),
    SUBSCRIBED_NAME_NOT_SET("SUBSCRIBED_NAME_NOT_SET"),
    POLICY_NAME_NOT_SET("POLICY_NAME_NOT_SET"),
    ORGANIZATION_TYPE_NOT_SET("ORGANIZATION_TYPE_NOT_SET"),
    CLASSIFICATION_NOT_SET("CLASSIFICATION_NOT_SET"),
    NO_APPROVER_ASSOCIATIONS("NO_APPROVER_ASSOCIATIONS"),
    NO_REQUEST_APPROVERS("NO_REQUEST_APPROVERS"),
    REQUEST_APPROVERS_NOT_SET("REQUEST_APPROVERS_NOT_SET"),
    MANAGED_SYSTEM_NOT_SET("MANAGED_SYSTEM_NOT_SET"),
    IT_POLICY_EXISTS("IT_POLICY_EXISTS"),
    ORGANIZATION_TYPE_CHILDREN_EXIST("ORGANIZATION_TYPE_CHILDREN_EXIST"),
    ORGANIZATION_TYPE_PARENTS_EXIST("ORGANIZATION_TYPE_PARENTS_EXIST"),
    ORGANIZATION_TYPE_TIED_TO_ORGANIZATION("ORGANIZATION_TYPE_TIED_TO_ORGANIZATION"),
    NO_EXEUCUTION_TIME("NO_EXEUCUTION_TIME"),
    INVALID_CRON_EXRPESSION("INVALID_CRON_EXRPESSION"),
    DATE_INVALID("DATE_INVALID"),
    FILE_DOES_NOT_EXIST("FILE_DOES_NOT_EXIST"),
    SPRING_BEAN_OR_SCRIPT_REQUIRED("SPRING_BEAN_OR_SCRIPT_REQUIRED"),
    INVALID_SPRING_BEAN("INVALID_SPRING_BEAN"),
    TEMPLATE_TYPE_REQUIRED("TEMPLATE_TYPE_REQUIRED"),
    ADDRESS_TYPE_REQUIRED("ADDRESS_TYPE_REQUIRED"),
    EMAIL_ADDRESS_TYPE_REQUIRED("EMAIL_ADDRESS_TYPE_REQUIRED"),
    PHONE_TYPE_REQUIRED("PHONE_TYPE_REQUIRED"),
    PHONE_TYPE_DUPLICATED("PHONE_TYPE_DUPLICATED"),
    EMAIL_ADDRESS_TYPE_DUPLICATED("EMAIL_ADDRESS_TYPE_DUPLICATED"),
    ADDRESS_TYPE_DUPLICATED("ADDRESS_TYPE_DUPLICATED"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    META_NAME_MISSING("META_NAME_MISSING"),
    META_VALUE_MISSING("META_VALUE_MISSING"),
    CONNECTOR_REQUIRED("CONNECTOR_REQUIRED"),
    FAIL_LIMIT_NUM_REPEAT_CHAR("FAIL_LIMIT_NUM_REPEAT_CHAR"),
    NOT_ALLOWED_ROLE_IN_SEARCH("NOT_ALLOWED_ROLE_IN_SEARCH"),
    NOT_ALLOWED_GROUP_IN_SEARCH("NOT_ALLOWED_GROUP_IN_SEARCH"),
    NOT_ALLOWED_ORGANIZATION_IN_SEARCH("NOT_ALLOWED_ORGANIZATION_IN_SEARCH"),
    INVALID_USER_SEARCH_REQUEST("INVALID_USER_SEARCH_REQUEST"),
    NO_SUBJECT("NO_SUBJECT"),
    NO_SSO_TOKEN("NO_SSO_TOKEN"),
    AUTH_LEVEL_GROUPING_HAS_PATTERNS("AUTH_LEVEL_GROUPING_HAS_PATTERNS"),
    AUTH_LEVEL_GROUPING_HAS_CONTENT_PROVIDERS("AUTH_LEVEL_GROUPING_HAS_CONTENT_PROVIDERS"),
    METATYPE_LINKED_WITH_METAELEMENT("METATYPE_LINKED_WITH_METAELEMENT"),
    TYPE_REQUIRED("TYPE_REQUIRED"),
    GROUPING_REQUIRED("GROUPING_REQUIRED"),
    VALUE_REQUIRED("VALUE_REQUIRED"),
    DISPLAY_NAME_REQUIRED("DISPLAY_NAME_REQUIRED"),
    LOCALE_ALREADY_EXISTS("LOCALE_ALREADY_EXISTS"),
    NO_DEFAULT_LANGUAGE("NO_DEFAULT_LANGUAGE"),
    LANGUAGE_CODE_MISSING("LANGUAGE_CODE_MISSING");
    private final String value;

    ResponseErrorCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResponseErrorCode fromValue(String v) {
        for (ResponseErrorCode c: ResponseErrorCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
