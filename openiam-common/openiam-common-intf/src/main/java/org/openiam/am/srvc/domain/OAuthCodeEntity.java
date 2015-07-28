package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
@DozerDTOCorrespondence(OAuthCode.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "OAUTH_CODE_ID"))
})
public class OAuthCodeEntity extends KeyEntity {

    @Column(name = "EXPIRED_ON", nullable = false)
    private Long expiredOn;

    @Column(name = "CODE", length = 100, nullable = false)
    private String code;

    @Column(name = "REDIRECT_URL", length = 255, nullable = true)
    private String redirectUrl;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = true, updatable = true, nullable=true)
    private AuthProviderEntity client;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "USER_ID",  insertable = true, updatable = true, nullable=true)
    private UserEntity user;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (redirectUrl != null ? !redirectUrl.equals(that.redirectUrl) : that.redirectUrl != null) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (expiredOn != null ? expiredOn.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (redirectUrl != null ? redirectUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAuthTokenEntity{" +
                " expiredOn=" + expiredOn +
                ", client=" + client +
                ", user=" + user +
                ", code=" + code +
                ", redirectUrl=" + redirectUrl +
                '}';
    }
}
