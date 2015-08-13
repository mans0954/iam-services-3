package org.openiam.idm.srvc.recon.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ReconExecStatusOptions")
@XmlEnum
public enum ReconExecStatusOptions {
    // Reconciliation has started
    @XmlEnumValue("started")
    STARTED("started"),
    // Start reconciliation request sent from UI to server and waiting for STARTED status
    @XmlEnumValue("starting")
    STARTING("starting"),
    // Reconciliation was stopped
    @XmlEnumValue("stopped")
    STOPPED("stopped"),
    // Stop reconciliation request sent from UI  to server and waiting for STOPPED status
    @XmlEnumValue("stopping")
    STOPPING("stopping"),
    //
    @XmlEnumValue("finished")
    FINISHED("finished"),
    @XmlEnumValue("failed")
    FAILED("failed");

    private String value;

    public String getValue() {

        return value;
    }

    ReconExecStatusOptions(String value) {
        this.value = value;
    }

    public static ReconExecStatusOptions fromString(final String val) {
        for(final ReconExecStatusOptions e : ReconExecStatusOptions.values()) {
            if(e.getValue().equals(val)) {
                return e;
            }
        }
        return null;
    }
}
