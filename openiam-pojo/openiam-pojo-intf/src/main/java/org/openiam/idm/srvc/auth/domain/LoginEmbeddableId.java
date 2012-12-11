package org.openiam.idm.srvc.auth.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import java.io.Serializable;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Embeddable
public class LoginEmbeddableId implements Serializable {

    @Column(name="SERVICE_ID",length=20)
    private String domainId;
    
    @Field(name = "login", index = Index.TOKENIZED, store = Store.YES)
    @Column(name="LOGIN",length=320)
    private String login;
    
    @Column(name="MANAGED_SYS_ID",length=50)
    private String managedSysId;

    public LoginEmbeddableId() {
    }

    public LoginEmbeddableId(String domainId, String login, String managedSysId) {
        this.domainId = domainId;
        this.login = login;
        this.managedSysId = managedSysId;
    }

    public String getDomainId() {
        return this.domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((domainId == null) ? 0 : domainId.hashCode());
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        result = prime * result
                + ((managedSysId == null) ? 0 : managedSysId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LoginEmbeddableId other = (LoginEmbeddableId) obj;
        if (domainId == null) {
            if (other.domainId != null)
                return false;
        } else if (!domainId.equals(other.domainId))
            return false;
        if (login == null) {
            if (other.login != null)
                return false;
        } else if (!login.equals(other.login))
            return false;
        if (managedSysId == null) {
            if (other.managedSysId != null)
                return false;
        } else if (!managedSysId.equals(other.managedSysId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format(
                "LoginId [domainId=%s, login=%s, managedSysId=%s]", domainId,
                login, managedSysId);
    }
}
