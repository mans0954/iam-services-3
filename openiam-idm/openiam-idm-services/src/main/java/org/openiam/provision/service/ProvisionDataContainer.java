package org.openiam.provision.service;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.provision.dto.ProvOperationEnum;
import org.openiam.provision.dto.ProvisionUser;

import java.io.Serializable;
import java.util.Map;

public class ProvisionDataContainer implements Serializable {

    private String requestId;
    private ProvOperationEnum operation;
    private String resourceId;
    private Login identity;
    private ProvisionUser provUser;
    private Map<String, Object> bindingMap;
    private String parentAuditLogId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public ProvOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(ProvOperationEnum operation) {
        this.operation = operation;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public ProvisionUser getProvUser() {
        return provUser;
    }

    public void setProvUser(ProvisionUser provUser) {
        this.provUser = provUser;
    }

    public Login getIdentity() {
        return identity;
    }

    public void setIdentity(Login identity) {
        this.identity = identity;
    }

    public Map<String, Object> getBindingMap() {
        return bindingMap;
    }

    public void setBindingMap(Map<String, Object> bindingMap) {
        this.bindingMap = bindingMap;
    }

    public String getParentAuditLogId() {
        return parentAuditLogId;
    }

    public void setParentAuditLogId(String parentAuditLogId) {
        this.parentAuditLogId = parentAuditLogId;
    }
}
