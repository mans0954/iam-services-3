package org.openiam.access.review.model;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.authmanager.common.model.AuthorizationAccessRight;
import org.openiam.base.KeyNameDTO;
import org.openiam.idm.srvc.access.dto.AccessRight;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 12/31/13.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccessViewBean", propOrder = {
        "hasChild",
        "beanType",
        "description",
        "risk",
        "status",
        "identity",
        "loginId",
        "managedSys",
        "resourceTypeId",
        "accessRights"
})
public class AccessViewBean extends KeyNameDTO implements Comparable<AccessViewBean> {
    private Boolean hasChild=false;
    private String beanType = this.getClass().getSimpleName();
    private String description;
    private String risk;
    private String status;
    private String identity;
    private String loginId;
    private String managedSys;
    private String resourceTypeId;
    private List<String> accessRights;


    public AccessViewBean() {
    }
    public AccessViewBean(String id, String name) {
        this(id, name, null);
    }
    public AccessViewBean(String id, String name, String description) {
        this.id =id;
        this.setName(name);
        this.description=description;
    }

    public Boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }

    public String getBeanType() {
        return beanType;
    }

    public void setBeanType(String beanType) {
        this.beanType = beanType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(String resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public String getManagedSys() {
        return managedSys;
    }

    public void setManagedSys(String managedSys) {
        this.managedSys = managedSys;
    }

    public List<String> getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(List<String> accessRights) {
        this.accessRights = accessRights;
    }

    public void addAccessRights(Collection<String> accessRights) {
        if(CollectionUtils.isNotEmpty(accessRights)) {
            if (this.accessRights == null)
                this.accessRights = new ArrayList<>();
            this.accessRights.addAll(accessRights);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessViewBean that = (AccessViewBean) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        return !(getBeanType() != null ? !getBeanType().equals(that.getBeanType()) : that.getBeanType() != null);

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getId() != null ? getId().hashCode() : 0);
        result = prime * result + (getBeanType() != null ? getBeanType().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AccessViewBean{" +
                "beanType='" + getBeanType() + '\'' +
                ", id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", risk='" + risk + '\'' +
                ", status='" + status + '\'' +
                ", identity='" + identity + '\'' +
                ", loginId='" + loginId + '\'' +
                '}';
    }

    @Override
    public int compareTo(AccessViewBean o) {
        if (this.getName() == null) {
            return o.getName() == null ? 0 : -1;
        } else if (o.getName() == null) {
            return 1;
        } else {
            return this.getName().compareToIgnoreCase(o.getName());
        }
    }
}
