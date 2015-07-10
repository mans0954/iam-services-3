package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.OAuthTokenEntity;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.role.dto.Role;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by alexander on 23.04.15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OAuthToken", propOrder = {
        "token",
        "refreshToken",
        "expiredOn",
        "clientId",
        "userId",
        "code",
        "codeExpiredOn",
        "redirectUrl"
})
@DozerDTOCorrespondence(OAuthTokenEntity.class)
public class OAuthToken extends KeyDTO {
    private String token;
    private String refreshToken;
    private Long expiredOn;
    private String clientId;
    private String userId;
    private String code;
    private Long codeExpiredOn;
    private String redirectUrl;


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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCodeExpiredOn() {
        return codeExpiredOn;
    }

    public void setCodeExpiredOn(Long codeExpiredOn) {
        this.codeExpiredOn = codeExpiredOn;
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

        OAuthToken that = (OAuthToken) o;

        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        if (refreshToken != null ? !refreshToken.equals(that.refreshToken) : that.refreshToken != null) return false;
        if (expiredOn != null ? !expiredOn.equals(that.expiredOn) : that.expiredOn != null) return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (codeExpiredOn != null ? !codeExpiredOn.equals(that.codeExpiredOn) : that.codeExpiredOn != null) return false;
        if (redirectUrl != null ? !redirectUrl.equals(that.redirectUrl) : that.redirectUrl != null) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (refreshToken != null ? refreshToken.hashCode() : 0);
        result = 31 * result + (expiredOn != null ? expiredOn.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (codeExpiredOn != null ? codeExpiredOn.hashCode() : 0);
        result = 31 * result + (redirectUrl != null ? redirectUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAuthToken{" +
                "token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiredOn=" + expiredOn +
                ", clientId=" + clientId +
                ", userId=" + userId +
                ", code=" + code +
                ", codeExpiredOn=" + codeExpiredOn +
                ", redirectUrl=" + redirectUrl +
                '}';
    }
}
