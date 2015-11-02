package org.openiam.provision.dto.srcadapter;

import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = { "login","newLogin","managedSystemId", "operation"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterLoginRequest {
    private String login;
    private String newLogin;
    private String managedSystemId;
    private AttributeOperationEnum operation;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNewLogin() {
        return newLogin;
    }

    public void setNewLogin(String newLogin) {
        this.newLogin = newLogin;
    }

    public String getManagedSystemId() {
        return managedSystemId;
    }

    public void setManagedSystemId(String managedSystemId) {
        this.managedSystemId = managedSystemId;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }
}
