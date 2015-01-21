package org.openiam.access.review.model;

import org.openiam.base.KeyNameDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

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
        "managedSys"
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



    public String getManagedSys() {
        return managedSys;
    }

    public void setManagedSys(String managedSys) {
        this.managedSys = managedSys;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessViewBean that = (AccessViewBean) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getBeanType() != null ? !getBeanType().equals(that.getBeanType()) : that.getBeanType() != null) return false;

        return true;
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
