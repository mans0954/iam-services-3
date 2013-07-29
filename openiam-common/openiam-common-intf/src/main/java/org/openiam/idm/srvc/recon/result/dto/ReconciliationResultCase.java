package org.openiam.idm.srvc.recon.result.dto;

public enum ReconciliationResultCase {
    HEADER("CASE", "#ffffff"), BROKEN_CSV("Broken record in CSV", "#ff6666"), NOT_EXIST_IN_IDM_DB(
            "Record don't exist in DB, but exists in Resource", "#FFDDFF"), NOT_UNIQUE_KEY(
            "Defined key is not unique", "#bbbbff"), IDM_DELETED(
            " Found in IDM but Marked as 'Deleted'", "#afafaf"), LOGIN_NOT_FOUND(
            "Login for current user is not founded", "#4444aa"), MATCH_FOUND(
            "Records is matched", "#DDFFDD"), MATCH_FOUND_DIFFERENT(
            "Found in Both But Different", "#CCFFCC"), NOT_EXIST_IN_RESOURCE(
            "Found in IDM, but not in Resource", "#FFFFDD"), RESOURCE_DELETED(
            "Found in Resource but Marked as 'Deleted'", "#607eae");

    String value;
    String color;

    ReconciliationResultCase(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }
}
