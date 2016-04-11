package org.openiam.provision.dto.srcadapter;

import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = {"managedSystemId", "password", "sendToUser", "activate"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterPasswordRequest  implements Serializable {
    private String managedSystemId;
    private String password;
    private boolean sendToUser;
    private boolean activate;

    public String getManagedSystemId() {
        return managedSystemId;
    }

    public void setManagedSystemId(String managedSystemId) {
        this.managedSystemId = managedSystemId;
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSendToUser() {
        return sendToUser;
    }

    public void setSendToUser(boolean sendToUser) {
        this.sendToUser = sendToUser;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }
}
