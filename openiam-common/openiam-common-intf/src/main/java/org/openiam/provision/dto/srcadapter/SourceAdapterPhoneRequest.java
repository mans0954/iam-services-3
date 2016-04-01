package org.openiam.provision.dto.srcadapter;

import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = {"phoneNumber", "newTypeId", "areaCode", "countryCode", "name", "primary", "active", "typeId", "operation"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterPhoneRequest  implements Serializable {
    private String phoneNumber;
    private String areaCode;
    private String countryCode;
    private String name;
    @XmlElement(name = "default")
    private boolean primary;
    private boolean active;
    private String typeId;
    private String newTypeId;

    private AttributeOperationEnum operation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNewTypeId() {
        return newTypeId;
    }

    public void setNewTypeId(String newTypeId) {
        this.newTypeId = newTypeId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
