package org.openiam.idm.srvc.role.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
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
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", insertable = true, updatable = false)
    private RoleEntity role;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "METADATA_ID", insertable = true, updatable = true, nullable=true)
    private MetadataElementEntity element;
    
    @Column(name="NAME", length=100)
    private String name;
    
    @Column(name="VALUE", length=4096)
    private String value;

    @ElementCollection
    @CollectionTable(name="ROLE_ATTRIBUTE_VALUES", joinColumns=@JoinColumn(name="ROLE_ATTRIBUTE_ID", referencedColumnName="ROLE_ATTR_ID"))
    @Column(name="VALUE", length = 255)
    private List<String> values = new ArrayList<String>();

    @Column(name = "IS_MULTIVALUED", nullable = false)
    @Type(type = "yes_no")
    private boolean isMultivalued = false;

    @Column(name="ATTR_GROUP",length=20)
    private String attrGroup;

	public String getRoleAttrId() {
		return roleAttrId;
	}

	public void setRoleAttrId(String roleAttrId) {
		this.roleAttrId = roleAttrId;
	}

	public RoleEntity getRole() {
		return role;
	}

	public void setRole(RoleEntity role) {
		this.role = role;
	}

    public MetadataElementEntity getElement() {
        return element;
    }

    public void setElement(MetadataElementEntity element) {
        this.element = element;
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

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public boolean getIsMultivalued() {
        return isMultivalued;
    }

    public void setIsMultivalued(boolean isMultivalued) {
        this.isMultivalued = isMultivalued;
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
        result = prime * result + ((element == null) ? 0 : element.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result
				+ ((roleAttrId == null) ? 0 : roleAttrId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + (isMultivalued ? 1231 : 1237);
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
        if (element == null) {
            if (other.element != null)
                return false;
        } else if (!element.equals(other.element))
            return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (roleAttrId == null) {
			if (other.roleAttrId != null)
				return false;
		} else if (!roleAttrId.equals(other.roleAttrId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
        if (isMultivalued != other.isMultivalued) return false;
		return true;
	}

	@Override
	public String toString() {
		return "RoleAttributeEntity [roleAttrId=" + roleAttrId + ", role="
				+ role + ", name=" + name + ", value=" + value + ", attrGroup=" + attrGroup + "]";
	}

	
}
