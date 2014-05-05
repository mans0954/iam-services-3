package org.openiam.idm.srvc.audit.constant;

public enum AuditTarget {
    USER("USER"),
    GROUP("GROUP"),
    ROLE("ROLE"),
    RESOURCE("RESOURCE"),
    MANAGED_SYS("MANAGED_SYS"),
    POLICY("POLICY"),
    ORG("ORG");

    private String value;

    private AuditTarget(String val){
        this.value=val;
    }
    public String value(){
        return this.value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AuditTarget");
        sb.append("{value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
