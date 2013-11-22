
package org.openiam.idm.srvc.res.service;

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
 *     &lt;enumeration value="objectNotFound"/>
 *     &lt;enumeration value="classNotFound"/>
 *     &lt;enumeration value="principalNotFound"/>
 *     &lt;enumeration value="userNotFound"/>
 *     &lt;enumeration value="userStatus"/>
 *     &lt;enumeration value="supervisorlNotFound"/>
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
 *     &lt;enumeration value="FILE_EXCEPTION"/>
 *     &lt;enumeration value="SQL_EXCEPTION"/>
 *     &lt;enumeration value="WS_SERVICE_EXCEPTION"/>
 *     &lt;enumeration value="SYNCHRONIZATION_EXCEPTION"/>
 *     &lt;enumeration value="LIMIT_EXCEEDED_EXCEPTION"/>
 *     &lt;enumeration value="AUTHENTICATION_EXCEPTION"/>
 *     &lt;enumeration value="PERMISSION_EXCEPTION"/>
 *     &lt;enumeration value="SERVICE_UNAVAILABLE_EXCEPTION"/>
 *     &lt;enumeration value="SCHEMA_VIOLATION_EXCEPTION"/>
 *     &lt;enumeration value="FAIL_PREPROCESSOR"/>
 *     &lt;enumeration value="FAIL_POSTPROCESSOR"/>
 *     &lt;enumeration value="FAIL_PROCESS_ALREADY_RUNNING"/>
 *     &lt;enumeration value="FAIL_OTHER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ResponseErrorCode")
@XmlEnum
public enum ResponseErrorCode {

    @XmlEnumValue("objectNotFound")
    OBJECT_NOT_FOUND("objectNotFound"),
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
    FAIL_LIMIT_NUM_REPEAT_CHAR("FAIL_LIMIT_NUM_REPEAT_CHAR"),
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
    FILE_EXCEPTION("FILE_EXCEPTION"),
    SQL_EXCEPTION("SQL_EXCEPTION"),
    WS_SERVICE_EXCEPTION("WS_SERVICE_EXCEPTION"),
    SYNCHRONIZATION_EXCEPTION("SYNCHRONIZATION_EXCEPTION"),
    LIMIT_EXCEEDED_EXCEPTION("LIMIT_EXCEEDED_EXCEPTION"),
    AUTHENTICATION_EXCEPTION("AUTHENTICATION_EXCEPTION"),
    PERMISSION_EXCEPTION("PERMISSION_EXCEPTION"),
    SERVICE_UNAVAILABLE_EXCEPTION("SERVICE_UNAVAILABLE_EXCEPTION"),
    SCHEMA_VIOLATION_EXCEPTION("SCHEMA_VIOLATION_EXCEPTION"),
    FAIL_PREPROCESSOR("FAIL_PREPROCESSOR"),
    FAIL_POSTPROCESSOR("FAIL_POSTPROCESSOR"),
    FAIL_PROCESS_ALREADY_RUNNING("FAIL_PROCESS_ALREADY_RUNNING"),
    FAIL_OTHER("FAIL_OTHER");
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
