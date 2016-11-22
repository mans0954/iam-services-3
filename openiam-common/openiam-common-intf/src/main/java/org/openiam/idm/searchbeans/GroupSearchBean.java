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
        "isRootsOnly",
        "managedSysId",
        "attributes",
        "updatedSince"
})
public class GroupSearchBean extends EntitlementsSearchBean<Group, String> {

	private static final long serialVersionUID = 1L;
	private String managedSysId;
	private boolean isRootsOnly;
	private List<Tuple<String, String>> attributes;


    @XmlSchemaType(name = "dateTime")
    protected Date updatedSince;

    public Date getUpdatedSince() {
        return updatedSince;
    }

    public void setUpdatedSince(Date updatedSince) {
        this.updatedSince = updatedSince;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + (isRootsOnly ? 1231 : 1237);
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result
				+ ((updatedSince == null) ? 0 : updatedSince.hashCode());
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
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (isRootsOnly != other.isRootsOnly)
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (updatedSince == null) {
			if (other.updatedSince != null)
				return false;
		} else if (!updatedSince.equals(other.updatedSince))
			return false;
		return true;
	}

    
}
