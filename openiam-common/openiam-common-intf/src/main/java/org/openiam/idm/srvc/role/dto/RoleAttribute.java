package org.openiam.idm.srvc.role.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.AbstractAttributeDTO;
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
        "roleId",
        "values",
        "isMultivalued"
})
@DozerDTOCorrespondence(RoleAttributeEntity.class)
public class RoleAttribute extends AbstractAttributeDTO {

    protected String roleId;
    protected List<String> values = new ArrayList<String>();
    protected Boolean isMultivalued = Boolean.FALSE;

    public RoleAttribute() {
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public Boolean getIsMultivalued() {
        return isMultivalued;
    }

    public void setIsMultivalued(Boolean isMultivalued) {
        this.isMultivalued = isMultivalued;
    }

    public String getRoleId() {
        return roleId;
    }


    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((isMultivalued == null) ? 0 : isMultivalued.hashCode());
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoleAttribute other = (RoleAttribute) obj;
		if (isMultivalued == null) {
			if (other.isMultivalued != null)
				return false;
		} else if (!isMultivalued.equals(other.isMultivalued))
			return false;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("RoleAttribute [roleId=%s, values=%s, isMultivalued=%s, toString()=%s]",
						roleId, values, isMultivalued, super.toString());
	}

    
}
