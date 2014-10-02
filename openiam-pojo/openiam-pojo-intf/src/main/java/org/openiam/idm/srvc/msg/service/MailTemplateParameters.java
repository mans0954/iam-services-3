package org.openiam.idm.srvc.msg.service;

/**
 * Predefined parameters that are used by the email templates
 */
public enum MailTemplateParameters {
    TO("to"),
    BCC("bcc"),
    CC("cc"),
    FROM("from"),
    SUBJECT("subject"),
    USER_NAME("userName"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    COMPANY_NAME("companyName"),
    LOGIN_NAME("loginName"),
    PASSWORD("password"),
    EMAIL("email"),
    PHONE("phone"),
    IDENTITY("identity"),
    USER_ID("userId"),
    USER_IDS("userIds"),
    SERVICE_HOST("SERVICE_HOST"),
    SERVICE_CONTEXT("SERVICE_CONTEXT"),
    REQUEST_ID("requestId"),
    REQUEST_REASON("requestReason"),
    REQUESTER("requester"),
    TARGET_USER("targetUser"),
    BASE_URL("baseUrl"),
    TOKEN("token");

    private final String value;

    private MailTemplateParameters(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}
