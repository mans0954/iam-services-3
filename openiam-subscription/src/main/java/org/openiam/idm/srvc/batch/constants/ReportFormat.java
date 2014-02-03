package org.openiam.idm.srvc.batch.constants;

public enum ReportFormat {
    HTML("html"),
    PDF("pdf");

    private String extension;
    private ReportFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static ReportFormat parseFormat(String deliveryFormat) {
        return Enum.valueOf(ReportFormat.class, deliveryFormat);
    }
}
