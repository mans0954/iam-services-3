package org.openiam.idm.srvc.audit.constant;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/2/13
 * Time: 3:23 AM
 * To change this template use File | Settings | File Templates.
 */
public enum AuditSource {
    WEBCONSOLE("WEBCONSOLE"),
    SELFSERVICE("SELFSERVICE"),
    ESB("ESB"),
    PROXY("PROXY");

    private String value;

    private AuditSource(String val){
        this.value=val;
    }
    public String value(){
        return this.value;
    }
}
