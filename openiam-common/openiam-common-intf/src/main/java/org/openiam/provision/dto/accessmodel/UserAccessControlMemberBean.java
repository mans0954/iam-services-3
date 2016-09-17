package org.openiam.provision.dto.accessmodel;

import org.apache.commons.collections.CollectionUtils;

import javax.xml.bind.annotation.*;
import java.util.Set;

/**
 * Created by zaporozhec on 7/28/16.
 */
@XmlType(propOrder = {"objectType", "type", "name", "rights", "metadataType", "managedSystem", "binaryLink"})
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccessControlMemberBean {
    private String objectType;
    private String type;
    private String metadataType;
    private String managedSystem;
    private String name;
    private boolean binaryLink;
    @XmlElementWrapper(name = "rights")
    @XmlElements({@XmlElement(name = "right")})
    private Set<String> rights;

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getRights() {
        return rights;
    }

    public void setRights(Set<String> rights) {
        this.rights = rights;
    }

    public String getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(String metadataType) {
        this.metadataType = metadataType;
    }

    public String getManagedSystem() {
        return managedSystem;
    }

    public void setManagedSystem(String managedSystem) {
        this.managedSystem = managedSystem;
    }

    public boolean isBinaryLink() {
        return CollectionUtils.isEmpty(rights);
    }

    public void setBinaryLink(boolean binaryLink) {
        this.binaryLink = binaryLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccessControlMemberBean)) return false;

        UserAccessControlMemberBean bean = (UserAccessControlMemberBean) o;

        if (objectType != null ? !objectType.equals(bean.objectType) : bean.objectType != null) return false;
        if (type != null ? !type.equals(bean.type) : bean.type != null) return false;
        if (metadataType != null ? !metadataType.equals(bean.metadataType) : bean.metadataType != null) return false;
        if (managedSystem != null ? !managedSystem.equals(bean.managedSystem) : bean.managedSystem != null)
            return false;
        if (name != null ? !name.equals(bean.name) : bean.name != null) return false;
        return rights != null ? rights.equals(bean.rights) : bean.rights == null;

    }

    @Override
    public int hashCode() {
        int result = objectType != null ? objectType.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (metadataType != null ? metadataType.hashCode() : 0);
        result = 31 * result + (managedSystem != null ? managedSystem.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (rights != null ? rights.hashCode() : 0);
        return result;
    }
}
