package org.openiam.base;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.util.StringUtil;

/**
 * Base object for all attributes
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseAttribute", propOrder = { "attributeId", "selected", "name", "value", "parentId", "operationEnum",
        "properties" })
public class BaseAttribute implements Serializable {

    protected String attributeId;
    protected String name;
    protected String value;
    protected String parentId;
    protected AttributeOperationEnum operationEnum;
    protected Boolean selected = false;
    private List<BaseProperty> properties;

    public BaseAttribute() {
    }

    public BaseAttribute(String name, String value) {
        this(name, value, AttributeOperationEnum.ADD);
    }

    public BaseAttribute(String name, String value, AttributeOperationEnum operation) {
        this.name = name;
        setValue(value);
        this.operationEnum = operation;
    }

    public BaseAttribute(String name, String value, String parentId, Boolean selected) {
        this.name = name;
        setValue(value);
        this.parentId = parentId;
        this.selected = selected;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return StringUtil.fromBase64(value);
    }

    public void setValue(String value) {
        // Values are base64 encoded internally to keep
        // values that are not really strings (e.g. binary values from Active
        // Directory) are set here.
        this.value = StringUtil.toBase64(value);
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public AttributeOperationEnum getOperationEnum() {
        return operationEnum;
    }

    public void setOperationEnum(AttributeOperationEnum operationEnum) {
        this.operationEnum = operationEnum;
    }

    public List<BaseProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<BaseProperty> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "BaseAttribute [attributeId=" + attributeId + ", name=" + name + ", value=" + value + ", parentId="
                + parentId + ", operationEnum=" + operationEnum + ", selected=" + selected + ", properties="
                + properties + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributeId == null) ? 0 : attributeId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((operationEnum == null) ? 0 : operationEnum.hashCode());
        result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((selected == null) ? 0 : selected.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        BaseAttribute other = (BaseAttribute) obj;
        if (attributeId == null) {
            if (other.attributeId != null)
                return false;
        } else if (!attributeId.equals(other.attributeId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (operationEnum != other.operationEnum)
            return false;
        if (parentId == null) {
            if (other.parentId != null)
                return false;
        } else if (!parentId.equals(other.parentId))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (selected == null) {
            if (other.selected != null)
                return false;
        } else if (!selected.equals(other.selected))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
