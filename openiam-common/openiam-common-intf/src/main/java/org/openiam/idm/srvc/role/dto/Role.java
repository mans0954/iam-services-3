package org.openiam.idm.srvc.role.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.entitlements.AbstractEntitlementsDTO;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.domain.RoleEntity;


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
 *         &lt;element name="id" type="{urn:idm.openiam.org/srvc/role/dto}id" minOccurs="0"/>
 *         &lt;element name="roleAttributes" type="{urn:idm.openiam.org/srvc/role/dto}roleAttributeSet" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userAssociationMethod" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "role", propOrder = {
        "createDate",
        "createdBy",
        "description",
        "groups",
        "roleAttributes",
        "userAssociationMethod",
        "status",
        "childRoles",
        "selected",
        "operation",
        "startDate",
        "endDate",
        "parentRoles",
        "resources",
        "managedSysId",
        "managedSysName"
})
@XmlRootElement(name = "Role")
@XmlSeeAlso({
        Group.class,
        RoleAttribute.class,
        Resource.class
})
@DozerDTOCorrespondence(RoleEntity.class)
public class Role extends AbstractEntitlementsDTO implements Comparable<Role> {

    /**
     *
     */
    private static final long serialVersionUID = -3903402630611423082L;

    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    protected String createdBy;
    protected String description;
    //@XmlJavaTypeAdapter(GroupSetAdapter.class)
    @Deprecated
    protected Set<Group> groups = new HashSet<Group>(0);
    @XmlJavaTypeAdapter(RoleAttributeSetAdapter.class)
    protected Set<RoleAttribute> roleAttributes = new HashSet<RoleAttribute>(0);

    protected int userAssociationMethod = RoleConstant.UN_ASSIGNED;

    protected String status;
    protected Boolean selected = new Boolean(false);

    private Set<Role> parentRoles;
    private Set<Role> childRoles;
    
    private String managedSysId;
    private String managedSysName;
    
    @Deprecated
    private Set<Resource> resources;


    @XmlSchemaType(name = "dateTime")
    protected Date startDate;
    @XmlSchemaType(name = "dateTime")
    protected Date endDate;

    public Role() {
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

    @Deprecated
    public Set<Group> getGroups() {
        return groups;
    }

    @Deprecated
    public void setGroups(Set<Group> value) {
        this.groups = value;
    }

    public Set<RoleAttribute> getRoleAttributes() {
        return roleAttributes;
    }

    public void setRoleAttributes(Set<RoleAttribute> value) {
        this.roleAttributes = value;
    }

    public int getUserAssociationMethod() {
        return userAssociationMethod;
    }

    public void setUserAssociationMethod(int value) {
        this.userAssociationMethod = value;
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

    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoleStatus(RoleStatus status) {
        this.status = status.toString();
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

    @Override
    public int compareTo(Role o) {
        if (getName() == null || o == null) {
            return Integer.MIN_VALUE;
        }
        return getName().compareTo(o.getName());
    }

    @Deprecated
    public Set<Resource> getResources() {
        return resources;
    }

    @Deprecated
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        if (createDate != null ? !createDate.equals(role.createDate) : role.createDate != null) return false;
        if (description != null ? !description.equals(role.description) : role.description != null) return false;
        if (id != null ? !id.equals(role.id) : role.id != null) return false;
        if (this.getName() != null ? !this.getName().equals(role.getName()) : role.getName() != null) return false;
        if (selected != null ? !selected.equals(role.selected) : role.selected != null) return false;
        if (status != null ? !status.equals(role.status) : role.status != null) return false;
        if (managedSysId != null ? !managedSysId.equals(role.managedSysId) : role.managedSysId != null) return false;
        return !(managedSysName != null ? !managedSysName.equals(role.managedSysName) : role.managedSysName != null);
    }

    @Override
    public int hashCode() {
        int result = createDate != null ? createDate.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (this.getName() != null ? this.getName().hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (selected != null ? selected.hashCode() : 0);
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (managedSysName != null ? managedSysName.hashCode() : 0);
        return result;
    }
}


