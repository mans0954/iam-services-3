package org.openiam.am.srvc.resattr.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AUTH_PROVIDER_ATTRIBUTE")
public class AuthProviderAttributeEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name="PROVIDER_ATTRIBUTE_ID", length = 32, nullable = false)
    private String providerAttributeId;
    @Column(name="PROVIDER_ID", length = 32, nullable = false)
    private String providerId;
    @Column(name="ATTRIBUTE_NAME", length = 100, nullable = false)
    private String attributeName;
    @Column(name="VALUE", length = 255, nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = false, updatable = false)
    private AuthProviderEntity provider;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="ATTRIBUTE_NAME", referencedColumnName = "ATTRIBUTE_NAME", insertable = false, updatable = false)
    private AuthAttributeEntity attribute;

    public String getProviderAttributeId() {
        return providerAttributeId;
    }

    public void setProviderAttributeId(String providerAttributeId) {
        this.providerAttributeId = providerAttributeId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AuthProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(AuthProviderEntity provider) {
        this.provider = provider;
    }

    public AuthAttributeEntity getAttribute() {
        return attribute;
    }

    public void setAttribute(AuthAttributeEntity attribute) {
        this.attribute = attribute;
    }
}
