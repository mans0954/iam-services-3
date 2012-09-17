package org.openiam.idm.srvc.role.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for roleAttribute.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="roleAttribute">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="metadataId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="roleAttrId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="roleId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "roleAttribute", propOrder = {
        "roleAttrId",
        "serviceId",
        "roleId",
        "metadataElementId",
        "name",
        "value",
        "attrGroup"

})
public class RoleAttribute implements java.io.Serializable {

    protected String roleAttrId;
    protected String serviceId;
    protected String roleId;
    protected String metadataElementId;
    protected String name;
    protected String value;
    protected String attrGroup;

    public RoleAttribute() {
    }


    public RoleAttribute(String roleAttrId) {
        this.roleAttrId = roleAttrId;
    }

    public RoleAttribute(String roleAttrId, String name, String value, String metadataTypeId) {
        this.roleAttrId = roleAttrId;
        this.name = name;
        this.value = value;
        this.metadataElementId = metadataTypeId;
    }

    /**
     * Gets the value of the metadataId property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMetadataElementId() {
        return metadataElementId;
    }

    /**
     * Sets the value of the metadataId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMetadataElementId(String value) {
        this.metadataElementId = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the roleAttrId property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getRoleAttrId() {
        return roleAttrId;
    }

    /**
     * Sets the value of the roleAttrId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRoleAttrId(String value) {
        this.roleAttrId = value;
    }


    /**
     * Gets the value of the value property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setValue(String value) {
        this.value = value;
    }


    public String getServiceId() {
        return serviceId;
    }


    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getRoleId() {
        return roleId;
    }


    public void setRoleId(String roleId) {
        this.roleId = roleId;

        //this.roleId = role.getId().getRoleId();
        //this.serviceId = role.getId().getServiceId();
    }


    public String getAttrGroup() {
        return attrGroup;
    }


    public void setAttrGroup(String attrGroup) {
        this.attrGroup = attrGroup;
    }


	@Override
	public String toString() {
		return String
				.format("RoleAttribute [roleAttrId=%s, serviceId=%s, roleId=%s, metadataElementId=%s, name=%s, value=%s, attrGroup=%s]",
						roleAttrId, serviceId, roleId, metadataElementId, name,
						value, attrGroup);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoleAttribute other = (RoleAttribute) obj;
		if (attrGroup == null) {
			if (other.attrGroup != null)
				return false;
		} else if (!attrGroup.equals(other.attrGroup))
			return false;
		if (metadataElementId == null) {
			if (other.metadataElementId != null)
				return false;
		} else if (!metadataElementId.equals(other.metadataElementId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (roleAttrId == null) {
			if (other.roleAttrId != null)
				return false;
		} else if (!roleAttrId.equals(other.roleAttrId))
			return false;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

    
}
