package org.openiam.am.srvc.constants;

public enum Status {

    ACTIVE("ACTIVE"), INACTIVE("INACTIVE");

    private String value;

    public String getValue() {
        return value;
    }

    private Status(String value) {
        this.value = value;
    }

}
