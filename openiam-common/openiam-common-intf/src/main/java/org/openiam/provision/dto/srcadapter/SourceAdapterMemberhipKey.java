package org.openiam.provision.dto.srcadapter;

import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by zaporozhec on 10/29/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterMemberhipKey extends SourceAdapterKey {
    private AttributeOperationEnum operation;

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }
}
