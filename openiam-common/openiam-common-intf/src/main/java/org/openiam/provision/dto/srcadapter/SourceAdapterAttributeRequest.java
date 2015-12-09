package org.openiam.provision.dto.srcadapter;

import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = {"name", "newName", "value", "values", "operation"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterAttributeRequest {
    private String name;
    private String newName;
    private String value;
    @XmlElementWrapper(name = "multivalues")
    @XmlElements({@XmlElement(name = "item")})
    private List<String> values;

    private AttributeOperationEnum operation;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SourceAdapterAttributeRequest{");
        sb.append("name='").append(name).append('\'');
        sb.append(", value='").append(value).append('\'');
        if (values != null)
            sb.append(", values=").append(values);
        sb.append('}');
        return sb.toString();
    }
}
