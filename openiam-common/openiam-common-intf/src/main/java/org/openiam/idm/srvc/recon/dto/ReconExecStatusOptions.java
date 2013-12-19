package org.openiam.idm.srvc.recon.dto;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ReconExecStatusOptions")
@XmlEnum
public enum ReconExecStatusOptions {
    @XmlEnumValue("started")
    STARTED("started"),
    @XmlEnumValue("stopped")
    STOPPED("stopped"),
    @XmlEnumValue("finished")
    FINISHED("finished"),
    @XmlEnumValue("failed")
    FAILED("failed");

    private String value;

    public String getValue() {
        return value;
    }

    private ReconExecStatusOptions(String value) {
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
