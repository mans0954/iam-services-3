package org.openiam.am.srvc.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.constants.AuthAttributeDataType;
import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AUTH_ATTRIBUTE", uniqueConstraints = {
        @UniqueConstraint(columnNames={"ATTRIBUTE_NAME", "PROVIDER_TYPE"})
})
@DozerDTOCorrespondence(AuthAttribute.class)
public class AuthAttributeEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "AUTH_ATTRIBUTE_ID", length = 32, nullable = false)
    private String authAttributeId;
    @Column(name="ATTRIBUTE_NAME", length = 100, nullable = false)
    private String attributeName;
    @Column(name="PROVIDER_TYPE", length = 32, nullable = false)
    private String providerType;
    @Column(name="DESCRIPTION", length = 255, nullable = true)
    private String description;
    @Column(name="DATA_TYPE")
    @Enumerated(EnumType.STRING)
    private AuthAttributeDataType dataType = AuthAttributeDataType.singleValue;
    @Column(name="REQUIRED")
    @Type(type = "yes_no")
    private boolean isRequired = false;
    @Column(name="DEFAULT_VALUE", length = 4000)
    private String defaultValue;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="PROVIDER_TYPE", referencedColumnName = "PROVIDER_TYPE", insertable = false, updatable = false)
    private AuthProviderTypeEntity type;

    public String getAuthAttributeId() {
        return authAttributeId;
    }

    public void setAuthAttributeId(String authAttributeId) {
        this.authAttributeId = authAttributeId;
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

    public AuthAttributeDataType getDataType() {
        return dataType;
    }

    public void setDataType(AuthAttributeDataType dataType) {
        this.dataType = dataType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
