package org.openiam.am.srvc.domain;

import java.io.Serializable;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.am.srvc.domain.pk.OAuthCodeIdEntity;
import org.openiam.am.srvc.dto.OAuthCode;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;

/**
 * Created by alexander on 21/07/15.
 */
@Entity
@Table(name = "OAUTH_CODE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AssociationOverrides({
    @AssociationOverride(name = "primaryKey.user",
            joinColumns = @JoinColumn(name = "USER_ID")),
    @AssociationOverride(name = "primaryKey.client",
            joinColumns = @JoinColumn(name = "PROVIDER_ID"))})
@DozerDTOCorrespondence(OAuthCode.class)
public class OAuthCodeEntity implements  Serializable {

    public OAuthCodeEntity(String userId, String providerId, String code){
        this.primaryKey = new OAuthCodeIdEntity();
        primaryKey.setUser(new UserEntity());
        primaryKey.setClient(new AuthProviderEntity());
        primaryKey.getClient().setId(providerId);
        primaryKey.getUser().setId(userId);
        primaryKey.setCode(code);
    }

    public OAuthCodeEntity(){
    }

    @EmbeddedId
    private OAuthCodeIdEntity primaryKey = new OAuthCodeIdEntity();

    @Column(name = "EXPIRED_ON", nullable = false)
    private Long expiredOn;

    @Column(name = "REDIRECT_URL", length = 255, nullable = true)
    private String redirectUrl;

    public OAuthCodeIdEntity getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(OAuthCodeIdEntity primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Long getExpiredOn() {
        return expiredOn;
    }

    public void setExpiredOn(Long expiredOn) {
        this.expiredOn = expiredOn;
    }
    @Transient
    public AuthProviderEntity getClient() {
        return this.primaryKey.getClient();
    }

    public void setClient(AuthProviderEntity client) {
        this.primaryKey.setClient(client);
    }
    @Transient
    public UserEntity getUser() {
        return primaryKey.getUser();
    }

    public void setUser(UserEntity user) {
        this.primaryKey.setUser(user);
    }
    @Transient
    public String getCode() {
        return primaryKey.getCode();
    }

    public void setCode(String code) {
        this.primaryKey.setCode(code);
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OAuthCodeEntity that = (OAuthCodeEntity) o;

        if (expiredOn != null ? !expiredOn.equals(that.expiredOn) : that.expiredOn != null) return false;
        if (getClient() != null ? !getClient().equals(that.getClient()) : that.getClient() != null) return false;
        if (getUser() != null ? !getUser().equals(that.getUser()) : that.getUser() != null) return false;
        if (getCode() != null ? !getCode().equals(that.getCode()) : that.getCode() != null) return false;
        if (redirectUrl != null ? !redirectUrl.equals(that.redirectUrl) : that.redirectUrl != null) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (expiredOn != null ? expiredOn.hashCode() : 0);
        result = 31 * result + (getClient() != null ? getClient().hashCode() : 0);
        result = 31 * result + (getUser() != null ? getUser().hashCode() : 0);
        result = 31 * result + (getCode() != null ? getCode().hashCode() : 0);
        result = 31 * result + (redirectUrl != null ? redirectUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAuthTokenEntity{" +
                " expiredOn=" + expiredOn +
                ", client=" + getClient() +
                ", user=" + getUser() +
                ", code=" + getCode() +
                ", redirectUrl=" + redirectUrl +
                '}';
    }
}
