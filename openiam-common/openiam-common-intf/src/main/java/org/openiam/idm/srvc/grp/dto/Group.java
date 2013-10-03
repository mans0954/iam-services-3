package org.openiam.idm.srvc.grp.dto;


import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseObject;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleSetAdapter;
import org.openiam.idm.srvc.role.dto.UserSetAdapter;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Group", propOrder = {
        "roles",
        "attributes",
        "managedSysId",
        "managedSysName",
        "companyId",
        "createDate",
        "createdBy",
        "description",
        "grpId",
        "grpName",
        "lastUpdate",
        "lastUpdatedBy",
        //"provisionMethod",
        //"provisionObjName",
        "status",
        //"metadataTypeId",
        //"ownerId",
        //"internalGroupId",
        "operation",
        "parentGroups",
        "childGroups",
        "resources"
})
@XmlRootElement(name = "Group")
@XmlSeeAlso({
        Role.class,
        GroupAttribute.class,
        Resource.class,
        User.class
})
@DozerDTOCorrespondence(GroupEntity.class)
public class Group extends BaseObject implements java.io.Serializable {

    private static final long serialVersionUID = 7657568959406790313L;

    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    private String managedSysId;
    private String managedSysName;
    protected String grpId;
    protected String grpName;
    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    protected String createdBy;
    protected String companyId;
    /*
    protected String ownerId;
    protected String provisionMethod;
    protected String provisionObjName;
    */
    protected String description;

    protected String status;
    @XmlSchemaType(name = "dateTime")
    protected Date lastUpdate;
    protected String lastUpdatedBy;
    /*
    protected String metadataTypeId;
    protected String internalGroupId = null;
    */
    
    private Set<Group> parentGroups;
    private Set<Group> childGroups;

    private Set<Resource> resources;

    @XmlJavaTypeAdapter(RoleSetAdapter.class)
    protected Set<Role> roles = new HashSet<Role>(0);

    //@XmlJavaTypeAdapter(GroupAttributeMapAdapter.class)
    protected Set<GroupAttribute> attributes = new HashSet<GroupAttribute>();

    public Group() {
    }

    public Group(String grpId) {
        this.grpId = grpId;
    }

    public String getGrpId() {
        return this.grpId;
    }

    public void setGrpId(String grpId) {
        this.grpId = grpId;
    }

    public String getGrpName() {
        return this.grpName;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    /*
    public String getProvisionMethod() {
        return this.provisionMethod;
    }

    public void setProvisionMethod(String provisionMethod) {
        this.provisionMethod = provisionMethod;
    }

    public String getProvisionObjName() {
        return this.provisionObjName;
    }

    public void setProvisionObjName(String provisionObjName) {
        this.provisionObjName = provisionObjName;
    }
    */

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<GroupAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<GroupAttribute> attributes) {
		this.attributes = attributes;
	}

    public void removeAttributes(GroupAttribute attr) {
        attributes.remove(attr.getName());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /*
    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }
	*/

    /*
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
	*/

    public void setGroupStatus(GroupStatus status) {
        this.status = status.toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /*
    public String getInternalGroupId() {
        return internalGroupId;
    }

    public void setInternalGroupId(String internalGroupId) {
        this.internalGroupId = internalGroupId;
    }
    */

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public Set<Group> getParentGroups() {
		return parentGroups;
	}

    public void setParentGroups(Set<Group> parentGroups) {
		this.parentGroups = parentGroups;
	}
	
	public void addParentGroup(final Group group) {
		if(group != null) {
			if(parentGroups == null) {
				parentGroups = new LinkedHashSet<Group>();
			}
			parentGroups.add(group);
		}
	}

	public Set<Group> getChildGroups() {
		return childGroups;
	}
	
	public void setChildGroups(Set<Group> childGroups) {
		this.childGroups = childGroups;
	}
	
	public void addChildGroup(final Group group) {
		if(group != null) {
			if(childGroups == null) {
				childGroups = new LinkedHashSet<Group>();
			}
			childGroups.add(group);
		}
	}

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }

	public String getManagedSysId() {
		return managedSysId;
	}

	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}

	public String getManagedSysName() {
		return managedSysName;
	}

	public void setManagedSysName(String managedSysName) {
		this.managedSysName = managedSysName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((grpId == null) ? 0 : grpId.hashCode());
		result = prime * result + ((grpName == null) ? 0 : grpName.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result
				+ ((lastUpdatedBy == null) ? 0 : lastUpdatedBy.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result
				+ ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result
				+ ((managedSysName == null) ? 0 : managedSysName.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		Group other = (Group) obj;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (grpId == null) {
			if (other.grpId != null)
				return false;
		} else if (!grpId.equals(other.grpId))
			return false;
		if (grpName == null) {
			if (other.grpName != null)
				return false;
		} else if (!grpName.equals(other.grpName))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (lastUpdatedBy == null) {
			if (other.lastUpdatedBy != null)
				return false;
		} else if (!lastUpdatedBy.equals(other.lastUpdatedBy))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (managedSysName == null) {
			if (other.managedSysName != null)
				return false;
		} else if (!managedSysName.equals(other.managedSysName))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (operation != other.operation)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("Group [operation=%s, managedSysId=%s, managedSysName=%s, grpId=%s, grpName=%s, createDate=%s, createdBy=%s, description=%s, status=%s, lastUpdate=%s, lastUpdatedBy=%s]",
						operation, managedSysId, managedSysName, grpId,
						grpName, createDate, createdBy, description, status,
						lastUpdate, lastUpdatedBy);
	}

	
}

