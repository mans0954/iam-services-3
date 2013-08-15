package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAMAttribute", propOrder = {
        "id",
        "reflectionKey",
        "attributeName"
})
@DozerDTOCorrespondence(AuthResourceAMAttributeEntity.class)
public class AuthResourceAMAttribute implements Serializable {
    private String id;
    private String reflectionKey;
    private String attributeName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReflectionKey() {
        return reflectionKey;
    }

    public void setReflectionKey(String reflectionKey) {
        this.reflectionKey = reflectionKey;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((reflectionKey == null) ? 0 : reflectionKey.hashCode());
        result = prime * result
                + ((attributeName == null) ? 0 : attributeName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AuthResourceAMAttribute other = (AuthResourceAMAttribute) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (reflectionKey == null) {
            if (other.reflectionKey != null)
                return false;
        } else if (!reflectionKey.equals(other.reflectionKey))
            return false;
        if (attributeName == null) {
            if (other.attributeName != null)
                return false;
        } else if (!attributeName.equals(other.attributeName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
                .format("AuthResourceAMAttributeEntity [amAttributeId=%s, reflectionKey=%s, attributeName=%s]", id,reflectionKey, attributeName);
    }
    
    
}
