package org.openiam.idm.srvc.grp.domain;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.internationalization.Internationalized;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="GRP_ATTRIBUTES")
@DozerDTOCorrespondence(GroupAttribute.class)
@AttributeOverride(name = "id", column = @Column(name = "ID"))
@Internationalized
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GroupAttributeEntity extends AbstractAttributeEntity {

    @ElementCollection
    @CollectionTable(name="GROUP_ATTRIBUTE_VALUES", joinColumns=@JoinColumn(name="GROUP_ATTRIBUTE_ID", referencedColumnName="ID"))
    @Column(name="VALUE", length = 4000)
    private List<String> values = new ArrayList<String>();

    @Column(name = "IS_MULTIVALUED", nullable = false)
    @Type(type = "yes_no")
    private boolean isMultivalued = false;

    /*
    @Column(name="GRP_ID",length=32)
    private String groupId;
    */
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "GRP_ID", referencedColumnName = "GRP_ID", insertable = true, updatable = false)
    private GroupEntity group;

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

	public GroupEntity getGroup() {
		return group;
	}

	public void setGroup(GroupEntity group) {
		this.group = group;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((element == null) ? 0 : element.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		GroupAttributeEntity other = (GroupAttributeEntity) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		return "GroupAttributeEntity [id=" + id + ", name=" + name + ", value="
				+ value + ", group=" + group + "]";
	}

	    
    
}
