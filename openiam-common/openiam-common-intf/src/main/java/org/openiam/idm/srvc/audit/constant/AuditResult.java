package org.openiam.idm.srvc.audit.constant;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/2/13
 * Time: 3:23 AM
 * To change this template use File | Settings | File Templates.
 */
public enum AuditResult {
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    SECURITY_BREACH("SECURITY BREACH");

    private String value;

    AuditResult(String val){
        this.value=val;
    }
    public String value(){
        return this.value;
    }
}
