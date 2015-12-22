package org.openiam.idm.srvc.prov.request.dto;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AttributeOperationEnum;
import org.springframework.beans.BeanUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationBean", propOrder = {
        "objectType",
        "objectId",
        "objectName",
        "operation",
        "properties",
        "rightIds",
        "startDate",
        "endDate"
})
public class OperationBean implements Serializable {

    private BulkOperationObjectType objectType;
    private String objectId;
    private String objectName;
    private Set<String> rightIds;
    private BulkOperationEnum operation;
    private Date startDate;
    private Date endDate;
    private Map<String, Object> properties = new HashMap<String, Object>();

    public OperationBean() {}

    public OperationBean(OperationBean operationBean) {
        BeanUtils.copyProperties(operationBean, this);
        if (operationBean.properties != null) {
            Map<String, Object> attrs = new HashMap<String, Object>();
            for (String key : operationBean.properties.keySet()) {
                attrs.put(key, operationBean.properties.get(key));
            }
            properties = attrs;
        }
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    

    public Set<String> getRightIds() {
		return rightIds;
	}

	public void setRightIds(Set<String> rightIds) {
		this.rightIds = rightIds;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationBean that = (OperationBean) o;

        if (objectId != null ? !objectId.equals(that.objectId) : that.objectId != null) return false;
        if (objectName != null ? !objectName.equals(that.objectName) : that.objectName != null) return false;
        if (objectType != null ? !objectType.equals(that.objectType) : that.objectType != null) return false;
        if (rightIds != null ? !rightIds.equals(that.rightIds) : that.rightIds != null) return false;
        
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = objectType != null ? objectType.hashCode() : 0;
        result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
        result = 31 * result + (objectName != null ? objectName.hashCode() : 0);
        result = 31 * result + (rightIds != null ? rightIds.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }
}
