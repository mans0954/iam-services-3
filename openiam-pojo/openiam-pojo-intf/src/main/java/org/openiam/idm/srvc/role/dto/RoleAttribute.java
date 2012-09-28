package org.openiam.idm.srvc.role.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.GenericGenerator;


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
        "roleId",
        "metadataElementId",
        "name",
        "value",
        "attrGroup"
})
@Entity
@Table(name="ROLE_ATTRIBUTE")
public class RoleAttribute implements java.io.Serializable {

    protected String roleAttrId;
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

    @Column(name="METADATA_ID",length=20)
    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String value) {
        this.metadataElementId = value;
    }

    @Column(name="NAME", length=20)
    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="ROLE_ATTR_ID", length=32)
    public String getRoleAttrId() {
        return roleAttrId;
    }

    public void setRoleAttrId(String value) {
        this.roleAttrId = value;
    }

    @Column(name="VALUE")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name="ROLE_ID", length=32,nullable=false)
    public String getRoleId() {
        return roleId;
    }


    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Column(name="ATTR_GROUP",length=20)
    public String getAttrGroup() {
        return attrGroup;
    }


    public void setAttrGroup(String attrGroup) {
        this.attrGroup = attrGroup;
    }


	@Override
	public String toString() {
		return String
				.format("RoleAttribute [roleAttrId=%s, roleId=%s, metadataElementId=%s, name=%s, value=%s, attrGroup=%s]",
						roleAttrId, roleId, metadataElementId, name,
						value, attrGroup);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleAttrId == null) ? 0 : roleAttrId.hashCode());
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
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	
}
