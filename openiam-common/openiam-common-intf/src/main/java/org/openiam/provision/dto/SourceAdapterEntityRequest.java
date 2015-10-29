package org.openiam.provision.dto;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.*;
import java.util.Set;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = {"id", "name", "operation", "type", "entityAttributes"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterEntityRequest {
    private String id;
    private String name;
    private AttributeOperationEnum operation;
    private SourceAdapterObjectEnum type;
    @XmlElementWrapper(name = "entity-attributes-set")
    @XmlElements({
            @XmlElement(name = "entity-attribute"),
    }
    )
    private Set<SourceAdapterAttributeRequest> entityAttributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public SourceAdapterObjectEnum getType() {
        return type;
    }

    public void setType(SourceAdapterObjectEnum type) {
        this.type = type;
    }

    public Set<SourceAdapterAttributeRequest> getEntityAttributes() {
        return entityAttributes;
    }

    public void setEntityAttributes(Set<SourceAdapterAttributeRequest> entityAttributes) {
        this.entityAttributes = entityAttributes;
    }
}
