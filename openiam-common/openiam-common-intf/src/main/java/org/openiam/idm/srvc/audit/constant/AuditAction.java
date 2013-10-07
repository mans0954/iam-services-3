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
    CHANGE_PASSWORD("CHANGE_PASSWORD");

    private String value;

    private AuditAction(String val){
        this.value=val;
    }
    public String value(){
        return this.value;
    }
}
