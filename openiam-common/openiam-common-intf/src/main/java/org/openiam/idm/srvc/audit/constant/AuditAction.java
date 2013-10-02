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
    LOGOUT("LOGOUT");

    private String value;

    private AuditAction(String val){
        this.value=val;
    }
    public String value(){
        return this.value;
    }
}
