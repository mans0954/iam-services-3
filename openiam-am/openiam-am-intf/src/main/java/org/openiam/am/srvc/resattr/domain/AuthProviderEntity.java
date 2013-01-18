package org.openiam.am.srvc.resattr.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AUTH_PROVIDER")
public class AuthProviderEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "PROVIDER_ID", length = 32, nullable = false)
    private String providerId;
    @Column(name = "PROVIDER_TYPE", length = 32, nullable = false)
    private String providerType;
    @Column(name = "MANAGED_SYS_ID", length = 32, nullable = false)
    private String managedSysId;
    @Column(name = "RESOURCE_ID", length = 32, nullable = false)
    private String resourceId;
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;
    @Column(name = "DESCRIPTION", length = 255, nullable = true)
    private String description;
    @Column(name="SIGN_REQUEST")
    @Type(type = "yes_no")
    private boolean isSignRequest=false;
    @Column(name = "PUBLIC_KEY", nullable = true, columnDefinition = "text")
    private String publicKey;
    @Column(name = "PRIVATE_KEY", nullable = true, columnDefinition = "text")
    private String privateKey;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="PROVIDER_TYPE", referencedColumnName = "PROVIDER_TYPE", insertable = false, updatable = false)
    private AuthProviderTypeEntity type;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID", insertable = false, updatable = false)
    private ManagedSysEntity managedSys;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = false, updatable = false)
    private ResourceEntity resource;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSignRequest() {
        return isSignRequest;
    }

    public void setSignRequest(boolean signRequest) {
        isSignRequest = signRequest;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public AuthProviderTypeEntity getType() {
        return type;
    }

    public void setType(AuthProviderTypeEntity type) {
        this.type = type;
    }

    public ManagedSysEntity getManagedSys() {
        return managedSys;
    }

    public void setManagedSys(ManagedSysEntity managedSys) {
        this.managedSys = managedSys;
    }

    public ResourceEntity getResource() {
        return resource;
    }

    public void setResource(ResourceEntity resource) {
        this.resource = resource;
    }
}
