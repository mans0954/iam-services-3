package org.openiam.am.srvc.domain;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.constants.SsoAttributeType;
import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AUTH_RESOURCE_ATTRIBUTE_MAP", uniqueConstraints = {
        @UniqueConstraint(columnNames={"PROVIDER_ID","TARGET_ATTRIBUTE_NAME"})
})
@DozerDTOCorrespondence(AuthResourceAttributeMap.class)
public class AuthResourceAttributeMapEntity implements Serializable {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="ATTRIBUTE_MAP_ID", length=32, nullable = false)
    private String attributeMapId;
    @Column(name="PROVIDER_ID", length=32, nullable = false)
    private String providerId;
    @Column(name="TARGET_ATTRIBUTE_NAME", length=100, nullable = false)
    private String targetAttributeName;
    @Column(name="AM_RES_ATTRIBUTE_ID", length=32, nullable = true)
    private String amResAttributeId;
    @Column(name="AM_POLICY_URL", length=100, nullable = true)
    private String amPolicyUrl;

    @Column(name="ATTRIBUTE_VALUE", length=100, nullable = true)
    private String  attributeValue;

    @Enumerated(EnumType.STRING)
    @Column(name="ATTRIBUTE_TYPE", length=32, nullable = false)
    private SsoAttributeType attributeType;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name="PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = false, updatable = false)
    private AuthProviderEntity provider;

    @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name="AM_RES_ATTRIBUTE_ID", referencedColumnName = "AM_RES_ATTRIBUTE_ID", insertable = false, updatable = false)
    private AuthResourceAMAttributeEntity amAttribute;

    public String getAttributeMapId() {
        return attributeMapId;
    }

    public void setAttributeMapId(String attributeMapId) {
        this.attributeMapId = attributeMapId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getTargetAttributeName() {
        return targetAttributeName;
    }

    public void setTargetAttributeName(String targetAttributeName) {
        this.targetAttributeName = targetAttributeName;
    }

    public AuthProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(AuthProviderEntity provider) {
        this.provider = provider;
    }

    public String getAmResAttributeId() {
        return amResAttributeId;
    }

    public void setAmResAttributeId(String amResAttributeId) {
        this.amResAttributeId = amResAttributeId;
    }

    public String getAmPolicyUrl() {
        return amPolicyUrl;
    }

    public void setAmPolicyUrl(String amPolicyUrl) {
        this.amPolicyUrl = amPolicyUrl;
    }

    public AuthResourceAMAttributeEntity getAmAttribute() {
        return amAttribute;
    }

    public void setAmAttribute(AuthResourceAMAttributeEntity amAttribute) {
        this.amAttribute = amAttribute;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public SsoAttributeType getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(SsoAttributeType attributeType) {
        this.attributeType = attributeType;
    }
}
