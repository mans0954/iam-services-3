package org.openiam.idm.srvc.user.dto;

// Generated Jun 12, 2007 10:46:13 PM by Hibernate Tools 3.2.0.beta8


import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseObject;
import org.openiam.idm.srvc.grp.dto.Group;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * UserAttribute represents an individual attribute that is associated with a user. A user may
 * have many attributes. A UserAttribute should also be associated
 * with a MetadataElement. This approach is used as a way to extend the attributes associated with
 * a user without having to extend the schema.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userAttribute", propOrder = {
        "id",
        "metadataElementId",
        "name",
        "userId",
        "value",
        "attrGroup",
        "operation",
        "required"
})
public class UserAttribute extends BaseObject {


    protected String id;
    protected String metadataElementId;
    protected String name;
    protected String userId;
    protected String value;
    protected String attrGroup;
    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    protected Boolean required = new Boolean(false);


    // Constructors

    /**
     * default constructor
     */
    public UserAttribute() {
    }

    /**
     * minimal constructor
     */
    public UserAttribute(String id) {
        this.id = id;
    }

    public UserAttribute(String name, String value) {
        this.name = name;
        this.value = value;
        this.id = null;
    }

    public UserAttribute(String id, String users,
                         String metadataElement, String name, String value) {
        this.id = id;
        userId = users;
        metadataElementId = metadataElement;
        this.name = name;
        this.value = value;
    }



    public void updateUserAttribute(UserAttribute attr) {
        this.attrGroup = attr.getAttrGroup();
        this.metadataElementId = attr.getMetadataElementId();
        this.name = attr.getName();
        this.value = attr.getValue();
    }

    // Property accessors
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String metadataElementId) {
        this.metadataElementId = metadataElementId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAttrGroup() {
        return attrGroup;
    }

    public void setAttrGroup(String attrGroup) {
        this.attrGroup = attrGroup;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAttribute)) return false;
        if (!super.equals(o)) return false;

        UserAttribute that = (UserAttribute) o;

        if (attrGroup != null ? !attrGroup.equals(that.attrGroup) : that.attrGroup != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (metadataElementId != null ? !metadataElementId.equals(that.metadataElementId) : that.metadataElementId != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (operation != that.operation) return false;
        if (required != null ? !required.equals(that.required) : that.required != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserAttribute{" +
                "id='" + id + '\'' +
                ", metadataElementId='" + metadataElementId + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", value='" + value + '\'' +
                ", attrGroup='" + attrGroup + '\'' +
                ", operation=" + operation +
                '}';
    }
}
