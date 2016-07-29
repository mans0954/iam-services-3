package org.openiam.provision.dto.accessmodel;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Set;

/**
 * Created by zaporozhec on 7/28/16.
 */
@XmlType(propOrder = {"objectType", "type", "name", "rights"})
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccessControlMemberBean {
    private String objectType;
    private String type;
    private String name;
    @XmlElementWrapper(name = "rights")
    @XmlElements({
            @XmlElement(name = "right")}
    )
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccessControlMemberBean)) return false;

        UserAccessControlMemberBean bean = (UserAccessControlMemberBean) o;

        if (objectType != null ? !objectType.equals(bean.objectType) : bean.objectType != null) return false;
        if (type != null ? !type.equals(bean.type) : bean.type != null) return false;
        return name != null ? name.equals(bean.name) : bean.name == null;

    }

    @Override
    public int hashCode() {
        int result = objectType != null ? objectType.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
