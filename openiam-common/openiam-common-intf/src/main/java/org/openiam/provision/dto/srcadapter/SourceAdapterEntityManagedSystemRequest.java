package org.openiam.provision.dto.srcadapter;

import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = {"managedSystemId"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterEntityManagedSystemRequest extends SourceAdapterEntityRequest {
    private String managedSystemId;

    public String getManagedSystemId() {
        return managedSystemId;
    }

    public void setManagedSystemId(String managedSystemId) {
        this.managedSystemId = managedSystemId;
    }
}
