package org.openiam.provision.dto.srcadapter;

import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = {"email", "name", "primary", "active", "newTypeId", "typeId", "operation"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterEmailRequest  implements Serializable {
    private String email;
    private String name;
    @XmlElement(name = "default")
    private boolean primary;
    private boolean active;
    private String typeId;
    private String newTypeId;
    private AttributeOperationEnum operation;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getNewTypeId() {
        return newTypeId;
    }

    public void setNewTypeId(String newTypeId) {
        this.newTypeId = newTypeId;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }
}
