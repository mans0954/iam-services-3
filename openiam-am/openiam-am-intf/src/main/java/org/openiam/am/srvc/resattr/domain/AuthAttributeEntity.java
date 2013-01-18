package org.openiam.am.srvc.resattr.domain;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AUTH_ATTRIBUTE")
public class AuthAttributeEntity implements Serializable {
    @Id
    @Column(name="ATTRIBUTE_NAME", length = 100, nullable = false)
    private String attributeName;
    @Column(name="PROVIDER_TYPE", length = 32, nullable = false)
    private String providerType;
    @Column(name="DESCRIPTION", length = 255, nullable = true)
    private String description;
    @Column(name="REQUIRED")
    @Type(type = "yes_no")
    private boolean isRequired = false;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="PROVIDER_TYPE", referencedColumnName = "PROVIDER_TYPE", insertable = false, updatable = false)
    private AuthProviderTypeEntity type;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public AuthProviderTypeEntity getType() {
        return type;
    }

    public void setType(AuthProviderTypeEntity type) {
        this.type = type;
    }
}
