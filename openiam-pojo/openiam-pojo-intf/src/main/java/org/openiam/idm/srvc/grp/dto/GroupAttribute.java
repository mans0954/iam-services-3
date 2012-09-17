package org.openiam.idm.srvc.grp.dto;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * GroupAttribute represents an individual attribute that is associated with a group. A group may
 * have many attributes. A GroupAttribute should also be associated
 * with a MetadataElement. This approach is used as a way to extend the attributes associated with
 * group without having to extend the schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupAttribute", propOrder = {
        "id",
        "name",
        "value",
        "metadataElementId",
        "groupId"
})
public class GroupAttribute implements java.io.Serializable {

    // Fields

    protected String id;
    protected String name;
    protected String value;
    protected String metadataElementId;
    protected String groupId;

    /**
     * minimal constructor
     */
    public GroupAttribute(String id) {
        this.id = id;
    }

    /**
     * full constructor
     */

    public GroupAttribute(String id,
                          String metadataElementId, String name, String value) {
        this.id = id;
        this.metadataElementId = metadataElementId;
        this.name = name;
        this.value = value;
    }


    /**
     * default constructor
     */
    public GroupAttribute() {
    }

    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String metadataElementId) {
        this.metadataElementId = metadataElementId;
    }


    // Property accessors
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("GroupAttribute [id=%s, name=%s, value=%s, metadataElementId=%s, groupId=%s]",
						id, name, value, metadataElementId, groupId);
	}


    
}
