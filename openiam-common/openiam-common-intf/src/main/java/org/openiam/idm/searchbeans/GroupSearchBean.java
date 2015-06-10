package org.openiam.idm.searchbeans;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.Tuple;
import org.openiam.idm.srvc.grp.dto.Group;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupSearchBean", propOrder = {
        "keySet",
        "name",
        "isRootsOnly",
        "managedSysId",
        "attributes",
        "updatedSince",
        "type",
        "risk",
		"adminResourceId"
})
public class GroupSearchBean extends EntitlementsSearchBean<Group, String> implements SearchBean<Group, String>, Serializable {

	private static final long serialVersionUID = 1L;
    private Set<String> keySet;
	private String name;
	private String managedSysId;
    private String type;
    private String risk;
	private boolean isRootsOnly;
	private List<Tuple<String, String>> attributes;
	private String adminResourceId;


    @XmlSchemaType(name = "dateTime")
    protected Date updatedSince;

    public Date getUpdatedSince() {
        return updatedSince;
    }

    public void setUpdatedSince(Date updatedSince) {
        this.updatedSince = updatedSince;
    }

    public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsRootsOnly() {
		return isRootsOnly;
	}

	public void setIsRootsOnly(boolean isRootsOnly) {
		this.isRootsOnly = isRootsOnly;
	}

	public String getManagedSysId() {
		return managedSysId;
	}

	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}
	
	public void addAttribute(final String key, final String value) {
		if(StringUtils.isNotBlank(key) || StringUtils.isNotBlank(value)) {
			if(this.attributes == null) {
				this.attributes = new LinkedList<Tuple<String,String>>();
			}
			final Tuple<String, String> tuple = new Tuple<String, String>(key, value);
			this.attributes.add(tuple);
		}
	}

	public List<Tuple<String, String>> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Tuple<String, String>> attributes) {
		this.attributes = attributes;
	}

    public String getType() {
        return type;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

	public String getRisk() {
		return risk;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAdminResourceId() {
		return adminResourceId;
	}

	public void setAdminResourceId(String adminResourceId) {
		this.adminResourceId = adminResourceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isRootsOnly ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		GroupSearchBean other = (GroupSearchBean) obj;
		if (isRootsOnly != other.isRootsOnly)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"GroupSearchBean [name=%s, isRootsOnly=%s, keySet=%s, toString()=%s]",
				name, isRootsOnly, keySet, super.toString());
	}

    @Override
    public String getKey() {
        return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next() : null;
    }

    @Override
    public void setKey(final String key) {
        if(keySet == null) {
            keySet = new HashSet<String>();
        }
        keySet.add(key);
    }

    public Set<String> getKeys() {
        return keySet;
    }

    public void addKey(final String key) {
        if(this.keySet == null) {
            this.keySet = new HashSet<String>();
        }
        this.keySet.add(key);
    }

    public boolean hasMultipleKeys() {
        return (keySet != null && keySet.size() > 1);
    }

    public void setKeys(final Set<String> keySet) {
        this.keySet = keySet;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(name != null ? name : "")
                .append(managedSysId != null ? managedSysId : "")
                .append(type != null ? type : "")
                .append(isRootsOnly)
                .append(adminResourceId != null ? adminResourceId : "")
                .append(attributes != null ? attributes.toString().hashCode() : "")
                .append(getKey() != null ? getKey() : "")
                .append(getKeys() != null ? getKeys().hashCode() : "")
                .toString();
    }
}
