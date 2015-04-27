package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.OAuthCodeEntity;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by alexander on 23.04.15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OAuthCode", propOrder = {
        "code",
        "expiredOn",
        "clientId",
        "userId",
        "scopeSet",
})
@DozerDTOCorrespondence(OAuthCodeEntity.class)
public class OAuthCode extends KeyDTO {
    private String code;
    private Long expiredOn;
    private String clientId;
    private String userId;
    private Set<MetadataType> scopeSet = new HashSet<MetadataType>(0);

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Set<MetadataType> getScopeSet() {
        return scopeSet;
    }

    public void setScopeSet(Set<MetadataType> scopeSet) {
        this.scopeSet = scopeSet;
    }
    public void addScope(MetadataType scope) {
        if(this.scopeSet==null)
            this.scopeSet=new HashSet<MetadataType>(0);
        this.scopeSet.add(scope);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OAuthCode that = (OAuthCode) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (expiredOn != null ? !expiredOn.equals(that.expiredOn) : that.expiredOn != null) return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return !(scopeSet != null ? !scopeSet.equals(that.scopeSet) : that.scopeSet != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (expiredOn != null ? expiredOn.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (scopeSet != null ? scopeSet.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAuthCode{" +
                "code='" + code + '\'' +
                ", expiredOn=" + expiredOn +
                ", clientId=" + clientId +
                ", userId=" + userId +
                ", scopeSet=" + scopeSet +
                '}';
    }
}
