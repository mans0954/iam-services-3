package org.openiam.idm.srvc.audit.constant;

public enum AuditSource {
    WEBCONSOLE("WEBCONSOLE"),
    SELFSERVICE("SELFSERVICE"),
    SELFSERVICE_EXT("SELFSERVICE_EXT"),
    WORKFLOW("WORKFLOW"),
    IDP("IDP"),
    SP("SP"),
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
