package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.OAuthUserClientXrefEntity;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 13/07/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OAuthUserClientXref", propOrder = {
        "isAllowed",
        "clientId",
        "userId",
        "scopeId",
        "scopeName"
})
@DozerDTOCorrespondence(OAuthUserClientXrefEntity.class)
public class OAuthUserClientXref extends KeyDTO {

    private boolean isAllowed;
    private String clientId;
    private String userId;
    private String scopeId;
    private String scopeName;

    public boolean isAllowed() {
        return isAllowed;
    }

    public void setIsAllowed(boolean isAllowed) {
        this.isAllowed = isAllowed;
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

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OAuthUserClientXref that = (OAuthUserClientXref) o;

        if (isAllowed != that.isAllowed) return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (scopeId != null ? !scopeId.equals(that.scopeId) : that.scopeId != null) return false;
        return !(scopeName != null ? !scopeName.equals(that.scopeName) : that.scopeName != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isAllowed ? 1 : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (scopeId != null ? scopeId.hashCode() : 0);
        result = 31 * result + (scopeName != null ? scopeName.hashCode() : 0);
        return result;
    }
}
