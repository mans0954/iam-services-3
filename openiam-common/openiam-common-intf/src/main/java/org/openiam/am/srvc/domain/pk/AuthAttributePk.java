package org.openiam.am.srvc.domain.pk;

import javax.persistence.Column;
import java.io.Serializable;

public class AuthAttributePk implements Serializable {
    @Column(name="ATTRIBUTE_NAME", length = 100, nullable = false)
    private String attributeName;
    @Column(name="PROVIDER_TYPE", length = 32, nullable = false)
    private String providerType;

    public AuthAttributePk(){}
    public AuthAttributePk(String attributeName, String providerType){
       this.attributeName=attributeName;
        this.providerType=providerType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AuthAttributePk that = (AuthAttributePk) o;

        if (!attributeName.equals(that.attributeName)) {
            return false;
        }
        return providerType.equals(that.providerType);

    }

    @Override
    public int hashCode() {
        int result = attributeName.hashCode();
        result = 31 * result + providerType.hashCode();
        return result;
    }
}
