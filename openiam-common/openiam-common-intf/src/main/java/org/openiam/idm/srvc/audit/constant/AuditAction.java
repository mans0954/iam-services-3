package org.openiam.idm.srvc.audit.constant;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/2/13
 * Time: 3:23 AM
 * To change this template use File | Settings | File Templates.
 */
public enum AuditAction {
    AUTHENTICATION("AUTHENTICATION"),
    LOGOUT("LOGOUT"),
	LOGIN("LOGIN"),
	SAVE_CONNECTOR("SAVE_CONNECTOR"),
	CONFIRM_SMS_OTP_TOKEN("CONFIRM_SMS_OTP_TOKEN"),
	CLEAR_SMS_OTP_STATUS("CLEAR_SMS_OTP_STATUS"),
	GET_SMS_OTP_STATUS("GET_SMS_OTP_STATUS"),
	SEND_SMS_OTP_TOKEN("SEND_SMS_OTP_TOKEN"),
	GET_QR_CODE("GET_QR_CODE"),
	SAML_SP_REQUEST("SAML_SP_REQUEST"),
	SAML_LOGIN("SAML_LOGIN"),
	SAML_SP_RESPONSE_PROCESS("SAML_SP_RESPONSE_PROCESS"),
	SAML_LOGOUT_RESPONSE_PROCESS("SAML_LOGOUT_RESPONSE_PROCESS"),
    SAVE_GROUP("SAVE GROUP"),
    ADD_GROUP("CREATE GROUP"),
    EDIT_GROUP("EDIT GROUP"),
    EDIT_GROUP_IDENTITY("EDIT GROUP IDENTITY"),
    GET_GROUP("GET GROUP"),
    DELETE_GROUP("DELETE GROUP"),
    ADD_ORG("CREATE ORG"),
    EDIT_ORG("EDIT ORG"),
    GET_ORG("GET ORG"),
    DELETE_ORG("DELETE ORG"),
    GET_CHILD_GROUP_NUM("GET NUMBER OF CHILD GROUPS"),
    PASSWORD_POLICY_CHANGE("PASSWORD_POLICY_CHANGE"),
    PASSWORD_POLICY_CREATE("PASSWORD_POLICY_CREATE"),
    PASSWORD_POLICY_DELETE("PASSWORD_POLICY_DELETE"),

    CHANGE_PASSWORD("CHANGE_PASSWORD"),
    SAVE_IT_POLICY("SAVE_IT_POLICY"),
    RESET_IT_POLICY("RESET_IT_POLICY"),
    CONFIRM_IT_POLICY("CONFIRM_IT_POLICY"),
    DECLINE_IT_POLICY("DECLINE_IT_POLICY"),
    DELETE_POLICY_MAP("DELETE_POLICY_MAP"),
    SAVE_POLICY_MAP("SAVE_POLICY_MAP"),
    GET_CHILD_GROUP("GET CHILD GROUPS"),
    GET_PARENT_GROUP_NUM("GET NUMBER OF PARENT GROUPS"),
    GET_PARENT_GROUP("GET PARENT GROUPS"),
    IS_USER_GROUP("CHECK IF USER IS IN GROUP"),
    ADD_USER_TO_GROUP("ADD USER TO GROUP"),
    REMOVE_USER_FROM_GROUP("REMOVE USER FROM GROUP"),
    ADD_ATTRIBUTE_TO_GROUP("ADD ATTRIBUTE TO GROUP"),
    REMOVE_GROUP_ATTRIBUTE("REMOVE GROUP ATTRIBUTE"),
    ADD_CHILD_GROUP("ADD CHILD GROUP"),
    REMOVE_CHILD_GROUP("REMOVE CHILD GROUP"),
    REMOVE_PARENT_GROUP("REMOVE PARENT GROUP"),
    CAN_ADD_USER_TO_GROUP("CHECK IF USER CAN BE ADDED TO GROUP"),
    CAN_REMOVE_USER_FROM_GROUP("CHECK IF USER CAN BE REMOVED FROM GROUP"),
    SEARCH_GROUP("SEARCH GROUP"),
    GET_GROUP_NUM("GET NUMBER OF GROUP"),
    GET_GROUP_NUM_FOR_USER("GET NUMBER OF GROUP FOR USER"),
    GET_GROUP_NUM_FOR_RESOURCE("GET NUMBER OF GROUP FOR RESOURCE"),
    GET_GROUP_NUM_FOR_ROLE("GET NUMBER OF GROUP FOR ROLE"),
    GET_GROUP_FOR_USER("GET GROUPS FOR USER"),
    GET_GROUP_FOR_RESOURCE("GET GROUPS FOR RESOURCE"),
    GET_GROUP_FOR_ROLE("GET GROUPS FOR ROLE"),
    ADD_GROUP_TO_ROLE("ADD GROUP TO ROLE"),
    ADD_USER_TO_ROLE("ADD USER TO ROLE"),
    GET_ROLE("GET ROLE"),
    GET_ROLE_IN_GROUP("GET ROLES IN GROUP"),
    GET_ROLE_FOR_USER("GET ROLES FOR USER"),
    GET_ROLE_NUM_FOR_USER("GET NUMBER OF ROLES FOR USER"),
    REMOVE_GROUP_FROM_ROLE("REMOVE GROUP FROM ROLE"),
    DELETE_ROLE("DELETE ROLE"),
    REMOVE_USER_FROM_ROLE("REMOVE USER FROM ROLE"),
    SAVE_ROLE("SAVE ROLE"),
    ADD_ROLE("CREATE ROLE"),
    EDIT_ROLE("EDIT ROLE"),
    ADD_ROLE_POLICY("ADD ROLE POLICY"),
    UPDATE_ROLE_POLICY("UPDATE ROLE POLICY"),
    GET_ROLE_POLICY("GET ROLE POLICY"),
    REMOVE_ROLE_POLICY("REMOVE ROLE POLICY"),
    SEARCH_ROLE("SEARCH ROLES"),
    GET_ROLE_NUM("GET NUMBER OF ROLES"),
    GET_ROLE_FOR_RESOURCE("GET ROLES FOR RESOURCE"),
    GET_ROLE_NUM_FOR_RESOURCE("GET NUMBER OF ROLES FOR RESOURCE"),
    GET_CHILD_ROLE("GET CHILD ROLE"),
    GET_CHILD_ROLE_NUM("GET NUMBER OF CHILD ROLES"),
    GET_PARENT_ROLE("GET PARENT ROLES"),
    GET_PARENT_ROLE_NUM("GET NUMBER OF PARENT ROLES"),
    ADD_CHILD_ROLE("ADD CHILD ROLE"),
    REMOVE_CHILD_ROLE("REMOVE CHILD ROLE"),
    GET_ROLE_NUM_FOR_GROUP("GET NUMBER OF ROLES FOR GROUP"),
    CAN_ADD_USER_TO_ROLE("CHECK IF USER CAN BE ADDED TO ROLE"),
    CAN_REMOVE_USER_FROM_ROLE("CHECK IF USER CAN BE REMOVED FROM ROLE"),
    GET_RESOURCE("GET RESOURCE"),
    GET_RESOURCE_NUM("GET NUMBER OF RESOURCES"),
    GET_ROOT_RESOURCE_NUM("GET NUMBER OF ROOT RESOURCES"),
    SEARCH_RESOURCE("SEARCH RESOURCE"),
    SAVE_RESOURCE("SAVE RESOURCE"),
    ADD_RESOURCE("CREATE RESOURCE"),
    EDIT_RESOURCE("EDIT RESOURCE"),
    ADD_RESOURCE_TYPE("ADD RESOURCE TYPE"),
    GET_RESOURCE_TYPE("GET RESOURCE TYPE"),
    UPDATE_RESOURCE_TYPE("UPDATE RESOURCE TYPE"),
    GET_ALL_RESOURCE_TYPE("GET ALL RESOURCE TYPES"),
    ADD_RESOURCE_PROP("ADD RESOURCE PROPERTY"),
    UPDATE_RESOURCE_PROP("UPDATE RESOURCE PROPERTY"),
    REMOVE_RESOURCE_PROP("DELETE RESOURCE PROPERTY"),
    REMOVE_USER_FROM_RESOURCE("REMOVE USER FROM RESOURCE"),
    ADD_USER_TO_RESOURCE("ADD USER TO RESOURCE"),
    UPDATE_USER_TO_RESOURCE("UPDATE USER TO RESOURCE"),
    DELETE_RESOURCE("DELETE RESOURCE"),
    DELETE_RESOURCE_TYPE("DELETE RESOURCE_TYPE"),
    GET_CHILD_RESOURCE("GET CHILD RESOURCES"),
    GET_CHILD_RESOURCE_NUM("GET NUMBER OF CHILD RESOURCES"),
    GET_PARENT_RESOURCE("GET PARENT RESOURCES"),
    GET_PARENT_RESOURCE_NUM("GET NUMBER OF PARENT RESOURCES"),
    ADD_CHILD_RESOURCE("ADD CHILD RESOURCE"),
    REMOVE_CHILD_RESOURCE("REMOVE CHILD RESOURCE"),
    ADD_GROUP_TO_RESOURCE("ADD GROUP TO RESOURCE"),
    REMOVE_GROUP_FROM_RESOURCE("REMOVE GROUP FROM RESOURCE"),
    ADD_ROLE_TO_RESOURCE("ADD ROLE TO RESOURCE"),
    ADD_ROLE_TO_GROUP("ADD ROLE TO GROUP"),
    REMOVE_ROLE_FROM_RESOURCE("REMOVE ROLE FROM RESOURCE"),
    REMOVE_ROLE_FROM_GROUP("REMOVE ROLE FROM GROUP"),

    GET_RESOURCE_NUM_FOR_ROLE("GET NUMBER OF RESOURCES FOR ROLE"),
    GET_RESOURCE_FOR_ROLE("GET RESOURCES FOR ROLE"),
    GET_RESOURCE_FOR_MANAGED_SYS("GET RESOURCES FOR MANAGED SYS"),
    GET_RESOURCE_NUM_FOR_GROUP("GET NUMBER OF RESOURCES FOR GROUP"),
    GET_RESOURCE_FOR_GROUP("GET RESOURCES FOR GROUP"),
    GET_RESOURCE_FOR_USER("GET RESOURCES FOR USER"),
    GET_RESOURCE_NUM_FOR_USER("GET NUMBER OF RESOURCES FOR USER"),
    CAN_ADD_USER_TO_RESOURCE("CHECK IF USER CAN BE ADDED TO RESOURCE"),
    CAN_REMOVE_USER_FROM_RESOURCE("CHECK IF USER CAN BE REMOVED FROM RESOURCE"),
    
    NEW_USER_WORKFLOW("NEW USER WORKFLOW"),
    EDIT_USER_WORKFLOW("EDIT USER WORKFLOW"),
    CLAIM_REQUEST("CLAIM REQUEST"),
    //INITIATE_WORKFLOW("INITIATE WORKFLOW"),
    COMPLETE_WORKFLOW("COMPLETE_WORKFLOW"),
    TERMINATED_WORKFLOW("TERMINATED_WORKFLOW"),
    UNCLAIM_TASK("UNCLAIM_TASK"),
    DELETE_ALL_USER_TASKS("DELETE_ALL_USER_TASKS"),
    PROVISIONING("PROVISIONING"),
    PROVISIONING_DISPATCHER("PROVISIONING DISPATCHER"),
    CREATE_USER("CREATE USER"),
    MODIFY_USER("MODIFY USER"),
    PROVISIONING_SETPASSWORD("PROV SET PASSWORD"),
    PROVISIONING_RESETPASSWORD("PROV RESET PASSWORD"),
    PROVISIONING_LOOKUP("PROV LOOKUP"),
    PROVISIONING_TEST("PROV TEST"),
    PROVISIONING_DELETE("PROV DELETE"),
    PROVISIONING_DISABLE("PROV DISABLE"),
    PROVISIONING_ENABLE("PROV ENABLE"),
    PROVISIONING_DELETE_IDENTITY("PROV IDENTITY DELETE"),
    PROVISIONING_DISABLE_IDENTITY("PROV IDENTITY DISABLE"),
    PROVISIONING_ENABLE_IDENTITY("PROV IDENTITY ENABLE"),
    PROVISIONING_ADD("PROV ADD"),
    PROVISIONING_MODIFY("PROV MODIFY"),
    DE_PROVISIONING("DE-PROV"),
    SYNCHRONIZATION("SYNCHRONIZATION"),
    SYNCH_USER("SYNCH USER"),
    RECONCILIATION("RECONCILIATION"),
    RECONCILIATION_IDM_USER("RECONCILIATION IDM USER"),
    RECONCILIATION_SOURCE_USER("RECONCILIATION SOURCE USER"),

    GET_RESOURCE_FOR_USER_BY_TYPE("GET RESOURCE FOR USER BY RESOURCE TYPE"),
    
    BATCH_TASK_EXECUTE("IDM BATCH TASK"),
    
    MENU_AUTHORIZATON("MENU AUTHORIZATON"),
    USER_ACTIVE("USER ACTIVE"),
    USER_ENABLE("USER ENABLE"),
    USER_DISABLE("USER DISABLE"),
    USER_DEACTIVATE("USER DEACTIVATE"),
    USER_RESETPASSWORD("USER RESET PASSWORD"),
    USER_NOTIFY("USER NOTIFY"),
    ADD_USER_TO_ORG("ADD USER TO ORG"),
    REMOVE_USER_FROM_ORG ("REMOVE USER FROM ORG"),

    SSL_CERT_REQUEST("SSL CERT REQUEST"),

    // USER ATTRIBUTES
    //-----------------------------------------
    DELETE_ADDRESS("DELETE ADDRESS"),
    ADD_ADDRESS("ADD ADDRESS"),
    REPLACE_ADDRESS("REPLACE ADDRESS"),

    DELETE_PHONE("DELETE PHONE"),
    ADD_PHONE("ADD PHONE"),
    REPLACE_PHONE("REPLACE PHONE"),

    DELETE_EMAIL("DELETE EMAIL"),
    ADD_EMAIL("ADD EMAIL"),
    REPLACE_EMAIL("REPLACE EMAIL"),

    DELETE_SUPERVISOR("DELETE SUPERVISOR"),
    ADD_SUPERVISOR("ADD SUPERVISOR"),
    REPLACE_SUPERVISOR("REPLACE SUPERVISOR"),

    DELETE_PRINCIPAL("DELETE PRINCIPAL"),
    ADD_PRINCIPAL("ADD PRINCIPAL"),
    REPLACE_PRINCIPAL("REPLACE PRINCIPAL"),

    DELETE_ATTRIBUTE("DELETE ATTRIBUTE"),
    ADD_ATTRIBUTE("ADD ATTRIBUTE"),
    REPLACE_ATTRIBUTE("REPLACE ATTRIBUTE"),

    REPLACE_PROP("REPLACE PROP"),
    BULK_OPERATION("BULK OPERATION"),
    
    SERVICE_TASK("ServiceTask"),
    TASK_LISTENER("TaskListener"),
    NOTIFICATION("Notification"),
    
    ACTIVITI_GROOVY_SCRIPT("ActivitiGroovyScript"),
    SAVE_LOGIN("SaveLogin"),
    
    ENTITLEMENTS_DELEGATE("EntitlementsDelegate"),

    RECERTIFICATION("RECERTIFICATION TASK"),
    RECERTIFICATION_CERTIFY("CERTIFY"),
    RECERTIFICATION_DONT_CERTIFY("DO NOT CERTIFY"),
    GROUP_ATTESTATION("GROUP ATTESTATION TASK"),
    //-----------------------------------------
	PASSWORD_INTERCEPTOR("PASSWORD INTERCEPTOR"),
    ADD_PROFILE_PICTURE_FOR_USER("ADD PROFILE PICTURE FOR USER"),
    UPDATE_PROFILE_PICTURE_FOR_USER("UPDATE PROFILE PICTURE FOR USER"),
    DELETE_PROFILE_PICTURE_FOR_USER("DELETE PROFILE PICTURE FOR USER"),
    EDIT_APPROVER_ASSOCIATIONS("EDIT APPROVER ASSOCIATIONS"),
    ADD_APPROVER_TO_USER("ADD APPROVER TO USER"),
    DELETE_APPROVER_FROM_USER("DELETE APPROVER FROM USER"),
    ADD_APPROVER_TO_ROLE("ADD APPROVER TO ROLE"),
    DELETE_APPROVER_FROM_ROLE("DELETE APPROVER FROM ROLE"),
    ADD_APPROVER_TO_GROUP("ADD APPROVER TO GROUP"),
    DELETE_APPROVER_FROM_GROUP("DELETE APPROVER FROM GROUP"),
    ADD_APPROVER_TO_SUPERVISOR("ADD APPROVER TO SUPERVISOR"),
    DELETE_APPROVER_FROM_SUPERVISOR("DELETE APPROVER FROM SUPERVISOR"),
    ADD_APPROVER_TO_TARGET_USER("ADD APPROVER TO TARGET USER"),
    DELETE_APPROVER_FROM_TARGET_USER("DELETE APPROVER FROM TARGET USER"),
    ADD_APPROVER_TO_RESOURCE("ADD APPROVER TO RESOURCE"),
    DELETE_APPROVER_FROM_RESOURCE("DELETE APPROVER FROM RESOURCE"),
    ADD_APPROVER_TO_ORGANIZATION("ADD APPROVER TO ORGANIZATION"),
    DELETE_APPROVER_FROM_ORGANIZATION("DELETE APPROVER FROM ORGANIZATION"),

    SOURCE_ADAPTER_CALL("SOURCE ADAPTER PERFORM"),
    USER_PRIMARY_EMAIL_CHANGED("USER PRIMARY EMAIL CHANGED"),
    USER_REHIRED("USER REHIRED"),
    USER_PRINCIPAL_CHANGED("USER PRICIPAL CHANGED"),
    USER_LEAVER("USER LEAVER"),
    NOTHING_TO_CERTIFY("Nothing To Certify"),
    
	MODIFY_PROPERTIES("MODIFY_PROPERTIES"),
    
    KEY_MANAGEMENT_INITIALIZATION("KEY_MANAGEMENT_INITIALIZATION"),
    
    CACHE_PUT("CACHE_PUT"),
    CACHE_EVICT("CACHE_EVICT")
	;

    private String value;

    AuditAction(String val){
        this.value=val;
    }
    public String value(){
        return this.value;
    }
}
