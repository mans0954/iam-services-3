package org.openiam.am.srvc.constants;

public enum Frequency {

    FIVE_MIN("5MIN", "Every 5 min"), FIFTEEN_MIN("15MIN", "Every 15 min"), SIXTY_MIN(
            "60MIN", "Every 1 Hour"), NIGHTLY("NIGHTLY", "Run Nightly");

    private String value;
    private String label;

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    private Frequency(String value, String label) {
        this.value = value;
        this.label = label;
    }

}
