package org.openiam.provision.dto.srcadapter;

import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.*;
import java.util.Set;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = { "name", "operation"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterEntityRequest {
    private String name;
    private AttributeOperationEnum operation;
//    @XmlElementWrapper(name = "entity-attributes-set")
//    @XmlElements({
//            @XmlElement(name = "entity-attribute"),
//    }
//    )
//    private Set<SourceAdapterAttributeRequest> entityAttributes;

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

//    public Set<SourceAdapterAttributeRequest> getEntityAttributes() {
//        return entityAttributes;
//    }

//    public void setEntityAttributes(Set<SourceAdapterAttributeRequest> entityAttributes) {
//        this.entityAttributes = entityAttributes;
//    }
}
