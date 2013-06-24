package org.openiam.idm.srvc.role.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.role.dto.RoleAttribute;

@Entity
@Table(name="ROLE_ATTRIBUTE")
@DozerDTOCorrespondence(RoleAttribute.class)
public class RoleAttributeEntity implements Serializable {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="ROLE_ATTR_ID", length=32)
    private String roleAttrId;
    
    @Column(name="ROLE_ID", length=32)
    private String roleId;
    
    @Column(name="METADATA_ID",length=20)
    private String metadataElementId;
    
    @Column(name="NAME", length=20)
    private String name;
    
    @Column(name="VALUE")
    private String value;
    
    @Column(name="ATTR_GROUP",length=20)
    private String attrGroup;

	public String getRoleAttrId() {
		return roleAttrId;
	}

	public void setRoleAttrId(String roleAttrId) {
		this.roleAttrId = roleAttrId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getMetadataElementId() {
		return metadataElementId;
	}

	public void setMetadataElementId(String metadataElementId) {
		this.metadataElementId = metadataElementId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAttrGroup() {
		return attrGroup;
	}

	public void setAttrGroup(String attrGroup) {
		this.attrGroup = attrGroup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attrGroup == null) ? 0 : attrGroup.hashCode());
		result = prime
				* result
				+ ((metadataElementId == null) ? 0 : metadataElementId
						.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((roleAttrId == null) ? 0 : roleAttrId.hashCode());
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
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
		RoleAttributeEntity other = (RoleAttributeEntity) obj;
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

	@Override
	public String toString() {
		return String
				.format("RoleAttributeEntity [roleAttrId=%s, roleId=%s, metadataElementId=%s, name=%s, value=%s, attrGroup=%s]",
						roleAttrId, roleId, metadataElementId, name, value,
						attrGroup);
	}
    
    
}
