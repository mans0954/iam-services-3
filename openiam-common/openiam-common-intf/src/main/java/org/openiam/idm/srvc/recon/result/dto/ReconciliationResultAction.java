package org.openiam.idm.srvc.recon.result.dto;

/**
 * 
 * @author zaporozhec
 * 
 */
public enum ReconciliationResultAction {
    // For 3a: Add to IDM; Remove
    // from Target System;
    // For 3b: Remove from IDM; Add to Target
    // For 3c: <no options>
    // For 3d: show values as <select> with 2 values (idm and source).
    ADD_TO_IDM("Add to IDM"), REMOVE_FROM_TARGET("Remove from target system"), REMOVE_FROM_IDM(
            "Remove from IDM"), ADD_TO_TARGET("Add to Target System");

    String value;

    ReconciliationResultAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
