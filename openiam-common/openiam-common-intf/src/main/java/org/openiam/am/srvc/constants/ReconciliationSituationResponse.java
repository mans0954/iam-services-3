package org.openiam.am.srvc.constants;

public enum ReconciliationSituationResponse {

    NOTHING("NOTHING", "DO NOTHING"), CREATE_IDM_ACCOUNT("CREATE_IDM_ACCOUNT",
            "CREATE IDM ACCOUNT"), CREATE_RES_ACCOUNT("CREATE_RES_ACCOUNT",
            "CREATE RESOURCE ACCOUNT"), DEL_RES_ACCOUNT("DEL_RES_ACCOUNT",
            "DELETE RESOURCE ACCOUNT"), DEL_IDM_ACCOUNT("DEL_IDM_ACCOUNT",
            "DELETE IDM ACCOUNT"), DEL_IDM_USER("DEL_IDM_USER",
            "DELETE IDM USER"), DEL_IDM_USER_NOT_TARGET(
            "DEL_IDM_USER-NOT_TARGET", "DELETE IDM USER-EXCLUDE TARGET SYSTEM"), UPD_IDM_USER(
            "UPD_IDM_USER", "UPDATE USER");

    private String value;
    private String label;

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    private ReconciliationSituationResponse(String value, String label) {
        this.value = value;
        this.label = label;
    }

}
