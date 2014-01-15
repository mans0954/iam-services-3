package org.openiam.idm.srvc.auth.context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Enables the capture of credentials needed for password based authentication.
 *
 * @author Suneet Shah
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PasswordCredential", propOrder = {
        "principal",
        "password"
})
public class PasswordCredential extends BaseCredential {

    String principal;
    String password;

    public PasswordCredential() {

    }

    public void setCredentials(String principal, String password) {
        this.password = password;
        this.principal = principal;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
