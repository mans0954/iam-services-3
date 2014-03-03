package org.openiam.idm.srvc.grp.dto;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupAttribute", propOrder = {
        "id",
        "name",
        "value",
        "values",
        "isMultivalued",
        "metadataElementId",
        "groupId"
})
@DozerDTOCorrespondence(GroupAttributeEntity.class)
public class GroupAttribute implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String id;
    protected String name;
    protected String value;
    protected List<String> values = new ArrayList<String>();
    protected Boolean isMultivalued = Boolean.FALSE;
    protected String metadataElementId;
    protected String groupId;

    public GroupAttribute(String id) {
        this.id = id;
    }

    public GroupAttribute() {
    }
    
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String metadataElementId) {
        this.metadataElementId = metadataElementId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((metadataElementId == null) ? 0 : metadataElementId
						.hashCode());
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
		GroupAttribute other = (GroupAttribute) obj;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
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
        if (isMultivalued == null) {
            if (other.isMultivalued != null)
                return false;
        } else if (!isMultivalued.equals(other.isMultivalued))
            return false;

		return true;
	}
	
	

	@Override
	public String toString() {
		return String
				.format("GroupAttribute [id=%s, name=%s, value=%s, metadataElementId=%s, groupId=%s]",
						id, name, value, metadataElementId, groupId);
	}


    
}
