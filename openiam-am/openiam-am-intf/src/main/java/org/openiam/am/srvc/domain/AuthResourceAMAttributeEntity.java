package org.openiam.am.srvc.domain;

import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AUTH_RESOURCE_AM_ATTRIBUTE")
@DozerDTOCorrespondence(AuthResourceAMAttribute.class)
public class AuthResourceAMAttributeEntity implements Serializable {
    @Id
    @Column(name="AM_ATTRIBUTE_ID", length=100, nullable = false)
    private String amAttributeId;
    @Column(name="ATTRIBUTE_NAME", length=100, nullable = false)
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
