package org.openiam.idm.srvc.role.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.internationalization.Internationalized;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="ROLE_ATTRIBUTE")
@AttributeOverride(name = "id", column = @Column(name = "ROLE_ATTR_ID"))
@DozerDTOCorrespondence(RoleAttribute.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Internationalized
public class RoleAttributeEntity extends AbstractAttributeEntity {
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RoleAttributeEntity that = (RoleAttributeEntity) o;

        if (isMultivalued != that.isMultivalued) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;
        return !(values != null ? !values.equals(that.values) : that.values != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (values != null ? values.hashCode() : 0);
        result = 31 * result + (isMultivalued ? 1 : 0);
        return result;
    }

    @Override
	public String toString() {
		return String
				.format("RoleAttributeEntity [role=%s, values=%s, isMultivalued=%s, toString()=%s]",
						role, values, isMultivalued, super.toString());
	}

    
}
