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
	SAML_LOGIN("SAML_LOGIN"),
    SAVE_GROUP("SAVE GROUP"),
    ADD_GROUP("CREATE GROUP"),
    GET_GROUP("GET GROUP"),
    DELETE_GROUP("DELETE GROUP"),
    GET_CHILD_GROUP_NUM("GET NUMBER OF CHILD GROUPS"),
    PASSWORD_POLICY_CHANGE("PASSWORD_POLICY_CHANGE"),
    CHANGE_PASSWORD("CHANGE_PASSWORD"),
    SAVE_IT_POLICY("SAVE_IT_POLICY"),
    RESET_IT_POLICY("RESET_IT_POLICY"),
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
    REMOVE_ROLE("REMOVE ROLE"),
    REMOVE_USER_FROM_ROLE("REMOVE USER FROM ROLE"),
    SAVE_ROLE("SAVE ROLE"),
    ADD_ROLE("CREATE ROLE"),
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
    ADD_RESOURCE_TYPE("ADD RESOURCE TYPE"),
    GET_RESOURCE_TYPE("GET RESOURCE TYPE"),
    UPDATE_RESOURCE_TYPE("UPDATE RESOURCE TYPE"),
    GET_ALL_RESOURCE_TYPE("GET ALL RESOURCE TYPES"),
    ADD_RESOURCE_PROP("ADD RESOURCE PROPERTY"),
    UPDATE_RESOURCE_PROP("UPDATE RESOURCE PROPERTY"),
    REMOVE_RESOURCE_PROP("DELETE RESOURCE PROPERTY"),
    REMOVE_USER_FROM_RESOURCE("REMOVE USER FROM RESOURCE"),
    ADD_USER_TO_RESOURCE("ADD USER TO RESOURCE"),
    DELETE_RESOURCE("DELETE RESOURCE"),
    GET_CHILD_RESOURCE("GET CHILD RESOURCES"),
    GET_CHILD_RESOURCE_NUM("GET NUMBER OF CHILD RESOURCES"),
    GET_PARENT_RESOURCE("GET PARENT RESOURCES"),
    GET_PARENT_RESOURCE_NUM("GET NUMBER OF PARENT RESOURCES"),
    ADD_CHILD_RESOURCE("ADD CHILD RESOURCE"),
    REMOVE_CHILD_RESOURCE("REMOVE CHILD RESOURCE"),
    ADD_GROUP_TO_RESOURCE("ADD GROUP TO RESOURCE"),
    REMOVE_GROUP_FROM_RESOURCE("REMOVE GROUP FROM RESOURCE"),
    ADD_ROLE_TO_RESOURCE("ADD ROLE TO RESOURCE"),
    REMOVE_ROLE_FROM_RESOURCE("REMOVE ROLE FROM RESOURCE"),
    GET_RESOURCE_NUM_FOR_ROLE("GET NUMBER OF RESOURCES FOR ROLE"),
    GET_RESOURCE_FOR_ROLE("GET RESOURCES FOR ROLE"),
    GET_RESOURCE_FOR_MANAGED_SYS("GET RESOURCES FOR MANAGED SYS"),
    GET_RESOURCE_NUM_FOR_GROUP("GET NUMBER OF RESOURCES FOR GROUP"),
    GET_RESOURCE_FOR_GROUP("GET RESOURCES FOR GROUP"),
    GET_RESOURCE_FOR_USER("GET RESOURCES FOR USER"),
    GET_RESOURCE_NUM_FOR_USER("GET NUMBER OF RESOURCES FOR USER"),
    CAN_ADD_USER_TO_RESOURCE("CHECK IF USER CAN BE ADDED TO RESOURCE"),
    CAN_REMOVE_USER_FROM_RESOURCE("CHECK IF USER CAN BE REMOVED FROM RESOURCE"),
    
    NEW_USER_WORKFLOW("NEW_USER_WORKFLOW"),
    EDIT_USER_WORKFLOW("EDIT_USER_WORKFLOW"),
    CLAIM_REQUEST("CLAIM_REQUEST"),
    WORKFLOW("WORKFLOW");

    private String value;

    private AuditAction(String val){
        this.value=val;
    }
    public String value(){
        return this.value;
    }
}
