package org.openiam.idm.srvc.role.dto;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseObject;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupSetAdapter;
import org.openiam.idm.srvc.res.dto.ResourceRole;
import org.openiam.idm.srvc.role.domain.RoleEntity;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;


/**
 * <p>Java class for role complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="role">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="createDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groups" type="{urn:idm.openiam.org/srvc/grp/dto}groupSet" minOccurs="0"/>
 *         &lt;element name="id" type="{urn:idm.openiam.org/srvc/role/dto}roleId" minOccurs="0"/>
 *         &lt;element name="roleAttributes" type="{urn:idm.openiam.org/srvc/role/dto}roleAttributeSet" minOccurs="0"/>
 *         &lt;element name="roleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userAssociationMethod" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="users" type="{urn:idm.openiam.org/srvc/user/dto}userSet" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "role", propOrder = {
        "roleId",
        "createDate",
        "serviceId",
        "createdBy",
        "description",
        "groups",
        "roleAttributes",
        "roleName",
        "userAssociationMethod",
        "metadataTypeId",
        "ownerId",
        "status",
        "childRoles",
        "selected",
        "internalRoleId",
        "operation",
        "startDate",
        "endDate",
        "rolePolicy",
        "parentRoles",
        "resourceRoles"
})
@XmlRootElement(name = "Role")
@XmlSeeAlso({
        Group.class,
        RoleAttribute.class,
        RolePolicy.class
})
@DozerDTOCorrespondence(RoleEntity.class)
public class Role extends BaseObject implements Comparable<Role> {

    /**
     *
     */
    private static final long serialVersionUID = -3903402630611423082L;

    protected AttributeOperationEnum operation;

    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    protected String createdBy;
    protected String description;
    @XmlJavaTypeAdapter(GroupSetAdapter.class)
    protected Set<Group> groups = new HashSet<Group>(0);
    protected String roleId;
    @XmlJavaTypeAdapter(RoleAttributeSetAdapter.class)
    protected Set<RoleAttribute> roleAttributes = new HashSet<RoleAttribute>(0);

    protected Set<RolePolicy> rolePolicy = new HashSet<RolePolicy>();

    protected String roleName;
    protected int userAssociationMethod = RoleConstant.UN_ASSIGNED;

    protected String status;
    protected Boolean selected = new Boolean(false);

    protected String metadataTypeId;

    protected String ownerId;
    protected String internalRoleId;
    private String serviceId;

    private Set<Role> parentRoles;
    private Set<Role> childRoles;
    
    private Set<ResourceRole> resourceRoles;


    @XmlSchemaType(name = "dateTime")
    protected Date startDate;
    @XmlSchemaType(name = "dateTime")
    protected Date endDate;

    public Role() {
    }
    
    public Role(final String roleId) {
    	this.roleId = roleId;
    }

    public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date value) {
        this.createDate = value;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String value) {
        this.createdBy = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> value) {
        this.groups = value;
    }

    public Set<RoleAttribute> getRoleAttributes() {
        return roleAttributes;
    }

    public void setRoleAttributes(Set<RoleAttribute> value) {
        this.roleAttributes = value;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String value) {
        this.roleName = value;
    }

    public int getUserAssociationMethod() {
        return userAssociationMethod;
    }

    public void setUserAssociationMethod(int value) {
        this.userAssociationMethod = value;
    }

    public String getMetadataTypeId() {
        return metadataTypeId;
    }


    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public String getOwnerId() {
        return ownerId;
    }


    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    
    public void addParentRole(final Role role) {
    	if(role != null) {
    		if(parentRoles == null) {
    			parentRoles = new LinkedHashSet<Role>();
    		}
    		parentRoles.add(role);
    	}
    }
    
    public Set<Role> getParentRoles() {
		return parentRoles;
	}

	public void setParentRoles(Set<Role> parentRoles) {
		this.parentRoles = parentRoles;
	}

	public void addChildRole(final Role role) {
    	if(role != null) {
    		if(childRoles == null) {
    			childRoles = new LinkedHashSet<Role>();
    		}
    		childRoles.add(role);
    	}
    }

	public Set<Role> getChildRoles() {
		return childRoles;
	}


	public void setChildRoles(Set<Role> childRoles) {
		this.childRoles = childRoles;
	}


	public String toString() {
        String str = "id=" + roleId +
                " name=" + roleName +
                " metadataTypeId=" + metadataTypeId +
                " ownerId=" + ownerId +
                " startDate=" + startDate +
                " endDate=" + endDate;
        return str;

    }

    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoleStatus(RoleStatus status) {
        this.status = status.toString();
    }

    public String getInternalRoleId() {
        return internalRoleId;
    }


    public void setInternalRoleId(String internalRoleId) {
        this.internalRoleId = internalRoleId;
    }

    public Boolean getSelected() {
        return selected;
    }


    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }


    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public Set<RolePolicy> getRolePolicy() {
        return rolePolicy;
    }

    public void setRolePolicy(Set<RolePolicy> rolePolicy) {
        this.rolePolicy = rolePolicy;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public int compareTo(Role o) {
        if (getRoleName() == null || o == null) {
            return Integer.MIN_VALUE;
        }
        return getRoleName().compareTo(o.getRoleName());
    }

	public Set<ResourceRole> getResourceRoles() {
		return resourceRoles;
	}

	public void setResourceRoles(Set<ResourceRole> resourceRoles) {
		this.resourceRoles = resourceRoles;
	}

	 @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        if (createDate != null ? !createDate.equals(role.createDate) : role.createDate != null) return false;
        if (description != null ? !description.equals(role.description) : role.description != null) return false;
        if (ownerId != null ? !ownerId.equals(role.ownerId) : role.ownerId != null) return false;
        if (roleId != null ? !roleId.equals(role.roleId) : role.roleId != null) return false;
        if (roleName != null ? !roleName.equals(role.roleName) : role.roleName != null) return false;
        if (selected != null ? !selected.equals(role.selected) : role.selected != null) return false;
        if (serviceId != null ? !serviceId.equals(role.serviceId) : role.serviceId != null) return false;
        if (status != null ? !status.equals(role.status) : role.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = createDate != null ? createDate.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (roleId != null ? roleId.hashCode() : 0);
        result = 31 * result + (roleName != null ? roleName.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (selected != null ? selected.hashCode() : 0);
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        return result;
    }
}


