package org.openiam.idm.srvc.role.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;

import java.util.ArrayList;
import java.util.List;


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
        "values",
        "isMultivalued",
        "attrGroup"
})
@DozerDTOCorrespondence(RoleAttributeEntity.class)
public class RoleAttribute implements java.io.Serializable {

    protected String roleAttrId;
    protected String roleId;
    protected String metadataElementId;
    protected String name;
    protected String value;
    protected List<String> values = new ArrayList<String>();
    protected Boolean isMultivalued = Boolean.FALSE;
    protected String attrGroup;

    public RoleAttribute() {
    }


    public RoleAttribute(String roleAttrId) {
        this.roleAttrId = roleAttrId;
    }

    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String value) {
        this.metadataElementId = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getRoleAttrId() {
        return roleAttrId;
    }

    public void setRoleAttrId(String value) {
        this.roleAttrId = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public Boolean getMultivalued() {
        return isMultivalued;
    }

    public void setMultivalued(Boolean multivalued) {
        isMultivalued = multivalued;
    }

    public String getRoleId() {
        return roleId;
    }


    public void setRoleId(String roleId) {
        this.roleId = roleId;
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
				.format("RoleAttribute [roleAttrId=%s, roleId=%s, metadataElementId=%s, name=%s, value=%s, attrGroup=%s]",
						roleAttrId, roleId, metadataElementId, name,
						value, attrGroup);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleAttrId == null) ? 0 : roleAttrId.hashCode());
        result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
        result = prime * result + ((metadataElementId == null) ? 0 : metadataElementId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((isMultivalued == null) ? 0 : isMultivalued.hashCode());

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
        if (isMultivalued == null) {
            if (other.isMultivalued != null)
                return false;
        } else if (!isMultivalued.equals(other.isMultivalued))
            return false;

		return true;
	}

	
}
