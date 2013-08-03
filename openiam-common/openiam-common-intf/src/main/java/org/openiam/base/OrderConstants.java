package org.openiam.base;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.*;

@XmlType(name = "OrderConstants")
@XmlEnum
public enum OrderConstants {

    @XmlEnumValue("asc")
    ASC("asc"),
    @XmlEnumValue("desc")
    DESC("desc");

    private String value;

    OrderConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        value = val;
    }

    public static OrderConstants getFromString(final String val) {
        for(final OrderConstants e : OrderConstants.values()) {
            if(StringUtils.equalsIgnoreCase(val, e.getValue())) {
                return e;
            }
        }
        return null;
    }

}
