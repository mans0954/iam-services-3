package org.openiam.idm.srvc.prov.request.dto;

import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationBean", propOrder = {
        "objectType",
        "objectId",
        "objectName",
        "operation"
})
public class OperationBean implements Serializable {

    private BulkOperationObjectType objectType;
    private String objectId;
    private String objectName;
    private BulkOperationEnum operation;

    public OperationBean() {}

    public OperationBean(OperationBean operationBean) {
        this.objectType = operationBean.objectType;
        this.objectId = operationBean.objectId;
        this.objectName = operationBean.objectName;
        this.operation = operationBean.operation;
    }

    public BulkOperationObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(BulkOperationObjectType objectType) {
        this.objectType = objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public BulkOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(BulkOperationEnum operation) {
        this.operation = operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationBean that = (OperationBean) o;

        if (objectId != null ? !objectId.equals(that.objectId) : that.objectId != null) return false;
        if (objectName != null ? !objectName.equals(that.objectName) : that.objectName != null) return false;
        if (objectType != null ? !objectType.equals(that.objectType) : that.objectType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = objectType != null ? objectType.hashCode() : 0;
        result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
        result = 31 * result + (objectName != null ? objectName.hashCode() : 0);
        return result;
    }
}
