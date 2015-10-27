package org.openiam.am.srvc.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.openiam.am.srvc.constants.AuthAttributeDataType;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "AUTH_PROVIDER_ATTRIBUTE", uniqueConstraints = {
        @UniqueConstraint(columnNames={"PROVIDER_ID", "AUTH_ATTRIBUTE_ID"})
})
@DozerDTOCorrespondence(AuthProviderAttribute.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AuthProviderAttributeEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name="PROVIDER_ATTRIBUTE_ID", length = 32, nullable = false)
    private String providerAttributeId;
    @Column(name="PROVIDER_ID", length = 32, nullable = false)
    private String providerId;
    @Column(name="AUTH_ATTRIBUTE_ID", length = 100, nullable = false)
    private String attributeId;
    @Column(name="VALUE", length = 4000, nullable = false)
    private String value;
    @Column(name="DATA_TYPE")
    @Enumerated(EnumType.STRING)
    private AuthAttributeDataType dataType = AuthAttributeDataType.singleValue;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = false, updatable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private AuthProviderEntity provider;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="AUTH_ATTRIBUTE_ID", referencedColumnName = "AUTH_ATTRIBUTE_ID", insertable = false, updatable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
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

    public AuthAttributeDataType getDataType() {
        return dataType;
    }

    public void setDataType(AuthAttributeDataType dataType) {
        this.dataType = dataType;
    }
}
