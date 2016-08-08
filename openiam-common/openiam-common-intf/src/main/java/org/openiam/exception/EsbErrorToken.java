package org.openiam.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/21/13
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EsbErrorToken", propOrder = {
        "className",
        "fieldName",
        "message",
        "lengthConstraint",
        "value"
})
public class EsbErrorToken implements Serializable {
    private String className;
    private String fieldName;
    private String message;
    private Long lengthConstraint;
    private Object value;

    public EsbErrorToken(){}

    public EsbErrorToken(String message){
        this.message=message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getLengthConstraint() {
        return lengthConstraint;
    }

    public void setLengthConstraint(Long lengthConstraint) {
        this.lengthConstraint = lengthConstraint;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "EsbErrorToken{" +
                "className='" + className + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", mq='" + message + '\'' +
                ", lengthConstraint=" + lengthConstraint +
                ", value=" + value +
                '}';
    }
}
