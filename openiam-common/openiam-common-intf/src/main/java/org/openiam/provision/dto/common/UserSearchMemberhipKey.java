package org.openiam.provision.dto.common;

import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by zaporozhec on 10/29/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserSearchMemberhipKey extends UserSearchKey {
    private AttributeOperationEnum operation;

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }
}
