package org.openiam.am.srvc.domain.pk;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Created by alexander on 07/08/15.
 */
@Embeddable
public class OAuthCodeIdEntity implements Serializable {
    @ManyToOne(cascade = CascadeType.ALL)
    private UserEntity user;
    @ManyToOne(cascade = CascadeType.ALL)
    private AuthProviderEntity client;
    @Column(name = "CODE", length = 100, nullable = false)
    private String code;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public AuthProviderEntity getClient() {
        return client;
    }

    public void setClient(AuthProviderEntity client) {
        this.client = client;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAuthCodeIdEntity that = (OAuthCodeIdEntity) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        return !(code != null ? !code.equals(that.code) : that.code != null);

    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }
}
