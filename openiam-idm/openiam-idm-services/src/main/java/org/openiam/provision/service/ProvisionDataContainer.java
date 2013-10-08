package org.openiam.provision.service;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.provision.dto.ProvisionUser;

import java.io.Serializable;

public class ProvisionDataContainer implements Serializable {

    private AttributeOperationEnum operation;
    private String managedSysId;
    private ProvisionUser provUser;

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public ProvisionUser getProvUser() {
        return provUser;
    }

    public void setProvUser(ProvisionUser provUser) {
        this.provUser = provUser;
    }

}
