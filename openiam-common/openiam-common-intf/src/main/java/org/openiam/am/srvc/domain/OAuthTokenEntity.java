package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.am.srvc.dto.OAuthToken;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by alexander on 23.04.15.
 */
@Entity
@Table(name = "OAUTH_TOKEN")
@DozerDTOCorrespondence(OAuthToken.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "OAUTH_TOKEN_ID"))
})
public class OAuthTokenEntity extends KeyEntity {
    @Column(name = "TOKEN", length = 255, nullable = false)
    private String token;
    @Column(name = "REFRESH_TOKEN", length = 255)
    private String refreshToken;

    @Column(name = "EXPIRED_ON", nullable = false)
    private Long expiredOn;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID", referencedColumnName = "PROVIDER_ID", insertable = true, updatable = true, nullable=true)
    private AuthProviderEntity client;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "USER_ID",  insertable = true, updatable = true, nullable=true)
    private UserEntity user;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "OAUTH_TOKEN_SCOPES", joinColumns = { @JoinColumn(name = "OAUTH_TOKEN_ID") }, inverseJoinColumns = { @JoinColumn(name = "TYPE_ID") })
    private Set<MetadataTypeEntity> scopeSet = new HashSet<MetadataTypeEntity>(0);

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiredOn() {
        return expiredOn;
    }

    public void setExpiredOn(Long expiredOn) {
        this.expiredOn = expiredOn;
    }

    public AuthProviderEntity getClient() {
        return client;
    }

    public void setClient(AuthProviderEntity client) {
        this.client = client;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Set<MetadataTypeEntity> getScopeSet() {
        return scopeSet;
    }

    public void setScopeSet(Set<MetadataTypeEntity> scopeSet) {
        this.scopeSet = scopeSet;
    }
    public void addScope(MetadataTypeEntity scope) {
        if(this.scopeSet==null)
            this.scopeSet=new HashSet<MetadataTypeEntity>(0);
        this.scopeSet.add(scope);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OAuthTokenEntity that = (OAuthTokenEntity) o;

        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        if (refreshToken != null ? !refreshToken.equals(that.refreshToken) : that.refreshToken != null) return false;
        if (expiredOn != null ? !expiredOn.equals(that.expiredOn) : that.expiredOn != null) return false;
        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return !(scopeSet != null ? !scopeSet.equals(that.scopeSet) : that.scopeSet != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (refreshToken != null ? refreshToken.hashCode() : 0);
        result = 31 * result + (expiredOn != null ? expiredOn.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (scopeSet != null ? scopeSet.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAuthTokenEntity{" +
                "token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiredOn=" + expiredOn +
                ", client=" + client +
                ", user=" + user +
                ", scopeSet=" + scopeSet +
                '}';
    }
}
