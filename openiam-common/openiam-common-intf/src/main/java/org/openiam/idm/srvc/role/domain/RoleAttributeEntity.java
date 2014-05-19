package org.openiam.idm.srvc.role.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.dto.RoleAttribute;

@Entity
@Table(name="ROLE_ATTRIBUTE")
@AttributeOverride(name = "id", column = @Column(name = "ROLE_ATTR_ID"))
@DozerDTOCorrespondence(RoleAttribute.class)
public class RoleAttributeEntity extends AbstractAttributeEntity {
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", insertable = true, updatable = false)
    private RoleEntity role;

    @ElementCollection
    @CollectionTable(name="ROLE_ATTRIBUTE_VALUES", joinColumns=@JoinColumn(name="ROLE_ATTRIBUTE_ID", referencedColumnName="ROLE_ATTR_ID"))
    @Column(name="VALUE", length = 255)
    private List<String> values = new ArrayList<String>();

    @Column(name = "IS_MULTIVALUED", nullable = false)
    @Type(type = "yes_no")
    private boolean isMultivalued = false;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isMultivalued ? 1231 : 1237);
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		RoleAttributeEntity other = (RoleAttributeEntity) obj;
		if (isMultivalued != other.isMultivalued)
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
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
				.format("RoleAttributeEntity [role=%s, values=%s, isMultivalued=%s, toString()=%s]",
						role, values, isMultivalued, super.toString());
	}

    
}
