package org.openiam.idm.srvc.grp.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;

/**
 * Created by alexander on 15.01.15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupOwner", propOrder = {
        "type",
        "id",
		"name"
})
public class GroupOwner implements Serializable {
    private String type;
    private String id;
	private String name;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GroupOwner that = (GroupOwner) o;

		if (type != null ? !type.equals(that.type) : that.type != null) return false;
		return id != null ? id.equals(that.id) : that.id == null;

	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (id != null ? id.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "GroupOwner [type=" + type + ", id=" + id + "]";
	}
    
    
}
