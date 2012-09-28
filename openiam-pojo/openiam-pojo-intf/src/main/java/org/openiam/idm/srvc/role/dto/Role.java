package org.openiam.idm.srvc.role.dto;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseObject;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupSet;
import org.openiam.idm.srvc.grp.dto.GroupSetAdapter;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
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
 *         &lt;element name="provisionObjName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "provisionObjName",
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
        "parentRoles"
})
@XmlRootElement(name = "Role")
@XmlSeeAlso({
        Group.class,
        RoleAttribute.class,
        RolePolicy.class
})
@Entity
@Table(name="ROLE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    protected String provisionObjName;
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


    @XmlSchemaType(name = "dateTime")
    protected Date startDate;
    @XmlSchemaType(name = "dateTime")
    protected Date endDate;

    public Role() {
    }
    
    public Role(final String roleId) {
    	this.roleId = roleId;
    }

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="ROLE_ID", length=32)
    public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	@Column(name="SERVICE_ID",length=32)
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@Column(name="CREATE_DATE",length=19)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date value) {
        this.createDate = value;
    }

    @Column(name="CREATED_BY",length=20)
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String value) {
        this.createdBy = value;
    }

    @Column(name="DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

	@ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinTable(name="GRP_ROLE",
	    joinColumns={@JoinColumn(name="ROLE_ID")},
	    inverseJoinColumns={@JoinColumn(name="GRP_ID")})
	@Fetch(FetchMode.SELECT)
    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> value) {
        this.groups = value;
    }

    @Column(name="PROVISION_OBJ_NAME",length=80)
    public String getProvisionObjName() {
        return provisionObjName;
    }

    public void setProvisionObjName(String value) {
        this.provisionObjName = value;
    }

	@OneToMany(fetch=FetchType.EAGER,orphanRemoval=true,cascade={CascadeType.ALL})
	@JoinColumn(name="ROLE_ID")
    public Set<RoleAttribute> getRoleAttributes() {
        return roleAttributes;
    }

    public void setRoleAttributes(Set<RoleAttribute> value) {
        this.roleAttributes = value;
    }

    @Column(name="ROLE_NAME",length=80)
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String value) {
        this.roleName = value;
    }

    @Transient
    public int getUserAssociationMethod() {
        return userAssociationMethod;
    }

    public void setUserAssociationMethod(int value) {
        this.userAssociationMethod = value;
    }

    @Column(name="TYPE_ID",length=20)
    public String getMetadataTypeId() {
        return metadataTypeId;
    }


    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    @Column(name="OWNER_ID",length=32)
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
    
    @ManyToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY)
    @JoinTable(name="role_to_role_membership",
        joinColumns={@JoinColumn(name="MEMBER_ROLE_ID")},
        inverseJoinColumns={@JoinColumn(name="ROLE_ID")})
    @Fetch(FetchMode.SUBSELECT)
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

	@ManyToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY)
    @JoinTable(name="role_to_role_membership",
        joinColumns={@JoinColumn(name="ROLE_ID")},
        inverseJoinColumns={@JoinColumn(name="MEMBER_ROLE_ID")})
    @Fetch(FetchMode.SUBSELECT)
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

	@Column(name="STATUS",length=20)
    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoleStatus(RoleStatus status) {
        this.status = status.toString();
    }

    @Column(name="INTERNAL_ROLE_ID")
    public String getInternalRoleId() {
        return internalRoleId;
    }


    public void setInternalRoleId(String internalRoleId) {
        this.internalRoleId = internalRoleId;
    }

    @Transient
    public Boolean getSelected() {
        return selected;
    }


    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Transient
    public AttributeOperationEnum getOperation() {
        return operation;
    }


    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

	@OneToMany(fetch=FetchType.EAGER,orphanRemoval=true,cascade=CascadeType.ALL)
	@JoinColumn(name="ROLE_ID")
    public Set<RolePolicy> getRolePolicy() {
        return rolePolicy;
    }

    public void setRolePolicy(Set<RolePolicy> rolePolicy) {
        this.rolePolicy = rolePolicy;
    }

    @Transient
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Transient
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		return result;
	}


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Role)) {
            return false;
        }

        Role compareRole = (Role) obj;
        // check for nulls

        if ((this.createDate == null && compareRole.createDate != null) ||
                this.createDate != null && compareRole.createDate == null) {
            return false;
        }

        if ((this.endDate == null && compareRole.endDate != null) ||
                this.endDate != null && compareRole.endDate == null) {
            return false;
        }

        if ((this.description == null && compareRole.description != null) ||
                this.description != null && compareRole.description == null) {
            return false;
        }

        if ((this.internalRoleId == null && compareRole.internalRoleId != null) ||
                this.internalRoleId != null && compareRole.internalRoleId == null) {
            return false;
        }

        if ((this.metadataTypeId == null && compareRole.metadataTypeId != null) ||
                this.metadataTypeId != null && compareRole.metadataTypeId == null) {
            return false;
        }

        if ((this.ownerId == null && compareRole.ownerId != null) ||
                this.ownerId != null && compareRole.ownerId == null) {
            return false;
        }
        if ((this.status == null && compareRole.status != null) ||
                this.status != null && compareRole.status == null) {
            return false;
        }

        return (this.description == compareRole.description || this.description.equals(compareRole.description)) &&
                (this.roleId.equals(compareRole.roleId)) &&
                (this.internalRoleId == compareRole.internalRoleId || this.internalRoleId.equals(compareRole.internalRoleId)) &&
                (this.metadataTypeId == compareRole.metadataTypeId || this.metadataTypeId.equals(compareRole.metadataTypeId)) &&
                (this.ownerId == compareRole.ownerId || this.ownerId.equals(compareRole.ownerId)) &&
                (this.status == compareRole.status || this.status.equals(compareRole.status)) &&
                (this.startDate == compareRole.startDate || this.startDate.equals(compareRole.startDate)) &&
                (this.endDate == compareRole.endDate || this.endDate.equals(compareRole.endDate));
    }

}


