package org.openiam.am.srvc.constants;

public enum TextSeparator {

    COMMA("comma"), SEMICOLON("semicolon"), TAB("tab"), SPACE("space"), ENTER(
            "enter");
    private String value;

    public String getValue() {
        return value;
    }

    private TextSeparator(String value) {
        this.value = value;
    }

}
