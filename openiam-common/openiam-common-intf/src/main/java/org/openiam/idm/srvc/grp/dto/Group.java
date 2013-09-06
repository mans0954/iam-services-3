package org.openiam.idm.srvc.grp.dto;


import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.res.dto.ResourceGroup;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleSetAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Group", propOrder = {
        "roles",
        "attributes",
        "companyId",
        "createDate",
        "createdBy",
        "description",
        "grpId",
        "grpName",
        "lastUpdate",
        "lastUpdatedBy",
        "provisionMethod",
        "provisionObjName",
        "status",
        "metadataTypeId",
        "ownerId",
        "internalGroupId",
        "operation",
        "parentGroups",
        "childGroups",
        "resourceGroups"
})
@XmlRootElement(name = "Group")
@XmlSeeAlso({
        Role.class,
        GroupAttribute.class
})
@DozerDTOCorrespondence(GroupEntity.class)
public class Group implements java.io.Serializable {

    private static final long serialVersionUID = 7657568959406790313L;

    protected AttributeOperationEnum operation;

    protected String grpId;
    protected String grpName;
    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    protected String createdBy;
    protected String companyId;
    protected String ownerId;
    protected String provisionMethod;
    protected String provisionObjName;
    protected String description;

    protected String status;
    @XmlSchemaType(name = "dateTime")
    protected Date lastUpdate;
    protected String lastUpdatedBy;
    protected String metadataTypeId;
    protected String internalGroupId = null;
    
    private Set<Group> parentGroups;
    private Set<Group> childGroups;
    
    private Set<ResourceGroup> resourceGroups;

    @XmlJavaTypeAdapter(RoleSetAdapter.class)
    protected Set<Role> roles = new HashSet<Role>(0);

    @XmlJavaTypeAdapter(GroupAttributeMapAdapter.class)
    protected Map<String, GroupAttribute> attributes = new HashMap<String, GroupAttribute>(0);

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

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Map<String, GroupAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, GroupAttribute> attributes) {
        this.attributes = attributes;
    }

    public void saveAttribute(GroupAttribute attr) {
        attributes.put(attr.getName(), attr);
    }

    public void removeAttributes(GroupAttribute attr) {
        attributes.remove(attr.getName());
    }

    public GroupAttribute getAttribute(String name) {
        return attributes.get(name);
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

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }


    public String toString() {
        String str = "grpId=" + grpId +
                " grpName=" + grpName +
                " status=" + status +
                " description=" + description +
                " attributes=" + attributes;

        return str;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


    public void setGroupStatus(GroupStatus status) {
        this.status = status.toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInternalGroupId() {
        return internalGroupId;
    }

    public void setInternalGroupId(String internalGroupId) {
        this.internalGroupId = internalGroupId;
    }

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
	
	public Set<ResourceGroup> getResourceGroups() {
		return resourceGroups;
	}

	public void setResourceGroups(Set<ResourceGroup> resourceGroups) {
		this.resourceGroups = resourceGroups;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (companyId != null ? !companyId.equals(group.companyId) : group.companyId != null) return false;
        if (createDate != null ? !createDate.equals(group.createDate) : group.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(group.createdBy) : group.createdBy != null) return false;
        if (grpId != null ? !grpId.equals(group.grpId) : group.grpId != null) return false;
        if (grpName != null ? !grpName.equals(group.grpName) : group.grpName != null) return false;
        if (ownerId != null ? !ownerId.equals(group.ownerId) : group.ownerId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = grpId != null ? grpId.hashCode() : 0;
        result = 31 * result + (grpName != null ? grpName.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (companyId != null ? companyId.hashCode() : 0);
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        return result;
    }
}

