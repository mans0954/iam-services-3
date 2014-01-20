package org.openiam.idm.srvc.res.dto;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Created by: Alexander Duckardt
 * Date: 1/16/14.
 */
public enum ResourceRisk {
    @XmlEnumValue("high")
    HIGH("HIGH"),
    @XmlEnumValue("low")
    LOW("LOW");

    private String value;

    ResourceRisk(String val) {
        value = val;
    }
}
