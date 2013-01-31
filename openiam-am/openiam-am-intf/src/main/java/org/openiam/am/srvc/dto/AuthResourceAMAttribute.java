package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthResourceAMAttribute", propOrder = {
        "amAttributeId",
        "attributeName"
})
@DozerDTOCorrespondence(AuthResourceAMAttributeEntity.class)
public class AuthResourceAMAttribute implements Serializable {
    private String amAttributeId;
    private String attributeName;

    public String getAmAttributeId() {
        return amAttributeId;
    }

    public void setAmAttributeId(String amAttributeId) {
        this.amAttributeId = amAttributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
