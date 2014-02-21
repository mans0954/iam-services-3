package org.openiam.idm.srvc.batch.constants;

public enum ReportFormat {
    HTML(".html"),
    XLS(".xls"),
    PDF(".pdf");

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

    public static ReportFormat parseExtension(String extension) {
        for(ReportFormat format : values()) {
            if(format.getExtension().equals(extension)) {
                return format;
            }
        }
        throw new IllegalArgumentException(
            "No enum extension " + extension);
    }
}
