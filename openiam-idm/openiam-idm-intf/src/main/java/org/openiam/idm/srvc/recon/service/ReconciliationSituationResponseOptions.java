package org.openiam.idm.srvc.recon.service;

public enum ReconciliationSituationResponseOptions {
    /**
     *
     * •	1) Record in Resource but not in IDM
     *       o	Add records to IDM
     *       o	Remove from Resource
     *       o	Disable in Resource
     *       o	Ignore
     * •  	2)Record in IDM but not in Resource
     *       o	Delete from IDM
     *       o	Disable (or change status in IDM)
     *       o	Add Record to Resource from IDM. Use the password that is in IDM
     * •    3)Record found in both
     *       o	Update IDM from Resource
     *       o	Update Resource from IDM
     *       o	Note: rules will determine how we merge these fields. (it may not be all from the source or all from the target.
     *       It may be a hybrid. We may also decide to only process certain attributes
     *
     */
    NOTHING("NOTHING", "DO NOTHING"),
    ADD_TO_IDM("ADD_TO_IDM", "Add Record to IDM from Resource"),
    DELETE_FROM_RES("DELETE_FROM_RES", "Delete from Resource"),
    DISABLE_IN_RES("DISABLE_IN_RES", "Disable (or change status in Resource)"),
    DELETE_FROM_IDM("DELETE_FROM_IDM", "Delete from IDM"),
    REMOVE_FROM_IDM("REMOVE_FROM_IDM", "Remove from IDM"),
    DISABLE_IN_IDM("DISABLE_IN_IDM", "Disable (or change status in IDM)"),
    ADD_TO_RES("ADD_TO_RES", "Add Record to Resource from IDM"),
    UPDATE_IDM_FROM_RES("UPDATE_IDM_FROM_RES", "Update IDM from Resource"),
    UPDATE_RES_FROM_IDM("UPDATE_RES_FROM_IDM", "Update Resource from IDM");

    private String value;
    private String label;

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    ReconciliationSituationResponseOptions(String value, String label) {
        this.value = value;
        this.label = label;
    }

}
