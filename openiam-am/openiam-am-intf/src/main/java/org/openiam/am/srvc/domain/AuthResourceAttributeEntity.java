package org.openiam.am.srvc.domain;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AUTH_RESOURCE_ATTRIBUTE")
public class AuthResourceAttributeEntity implements Serializable {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="ATTRIBUTE_MAP_ID", length=32, nullable = false)
    private String attributeMapId;
    @Column(name="RESOURCE_ID", length=32, nullable = false)
    private String resourceId;
    @Column(name="TARGET_ATTRIBUTE_NAME", length=100, nullable = false)
    private String targetAttributeName;
    @Column(name="AM_ATTRIBUTE_NAME", length=100, nullable = true)
    private String amAttributeName;
    @Column(name="AM_POLICY_URL", length=100, nullable = true)
    private String amPolicyUrl;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = false, updatable = false)
    private ResourceEntity resource;

    public String getAttributeMapId() {
        return attributeMapId;
    }

    public void setAttributeMapId(String attributeMapId) {
        this.attributeMapId = attributeMapId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTargetAttributeName() {
        return targetAttributeName;
    }

    public void setTargetAttributeName(String targetAttributeName) {
        this.targetAttributeName = targetAttributeName;
    }

    public String getAmAttributeName() {
        return amAttributeName;
    }

    public void setAmAttributeName(String amAttributeName) {
        this.amAttributeName = amAttributeName;
    }

    public String getAmPolicyUrl() {
        return amPolicyUrl;
    }

    public void setAmPolicyUrl(String amPolicyUrl) {
        this.amPolicyUrl = amPolicyUrl;
    }

    public ResourceEntity getResource() {
        return resource;
    }

    public void setResource(ResourceEntity resource) {
        this.resource = resource;
    }
}
