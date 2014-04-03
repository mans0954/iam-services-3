package org.openiam.idm.srvc.grp.domain;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="GRP_ATTRIBUTES")
@DozerDTOCorrespondence(GroupAttribute.class)
public class GroupAttributeEntity {

	@Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="ID",length=32)
    private String id;
	
	@Column(name="NAME",length=100)
    private String name;
	
	@Column(name="VALUE")
    private String value;

    @ElementCollection
    @CollectionTable(name="GROUP_ATTRIBUTE_VALUES", joinColumns=@JoinColumn(name="GROUP_ATTRIBUTE_ID", referencedColumnName="ID"))
    @Column(name="VALUE", length = 4096)
    private List<String> values = new ArrayList<String>();

    @Column(name = "IS_MULTIVALUED", nullable = false)
    @Type(type = "yes_no")
    private boolean isMultivalued = false;
    
    @Column(name="METADATA_ID",length=20)
    private String metadataElementId;
    
    /*
    @Column(name="GRP_ID",length=32)
    private String groupId;
    */
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "GRP_ID", referencedColumnName = "GRP_ID", insertable = true, updatable = false)
    private GroupEntity group;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

    public String getMetadataElementId() {
		return metadataElementId;
	}

	public void setMetadataElementId(String metadataElementId) {
		this.metadataElementId = metadataElementId;
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
		result = prime
				* result
				+ ((metadataElementId == null) ? 0 : metadataElementId
						.hashCode());
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
				+ value + ", metadataElementId=" + metadataElementId
				+ ", group=" + group + "]";
	}

	    
    
}
