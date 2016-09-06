
package org.openiam.connector.type.request;

import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PasswordRequest complex type.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PasswordRequest", propOrder = {
        "password",
        "currentPassword",
        "forceChange"
})
public class PasswordRequest extends RequestType<ExtensibleUser> {
    @XmlElement(required = true)
    protected String password;
    protected String currentPassword;
    protected boolean forceChange;

    public PasswordRequest() {
        super();
    }

    public PasswordRequest(String currentPassword, String password,
                           String userIdentity) {
        super();
        this.currentPassword = currentPassword;
        this.password = password;
        this.objectIdentity = userIdentity;
    }

    public boolean isForceChange() {
        return forceChange;
    }

    public void setForceChange(boolean forceChange) {
        this.forceChange = forceChange;
    }

    /**
     * Gets the value of the password property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the currentPassword property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * Sets the value of the currentPassword property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCurrentPassword(String value) {
        this.currentPassword = value;
    }
}
