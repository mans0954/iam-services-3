package org.openiam.idm.srvc.grp.dto;


import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleSetAdapter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

/**
 * <code>Group</code> is used to represent groups in the IAM system. Groups are frequently modeled
 * after an organizations structure and represent a way to associate users together so that we don't
 * have to assign policies to individual users.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Group", propOrder = {
        "roles",
        "attributes",
        "companyId",
        "createDate",
        "createdBy",
        "description",
        "groupClass",
        "grpId",
        "grpName",
        "inheritFromParent",
        "lastUpdate",
        "lastUpdatedBy",
        /*"parentGrpId",*/
        "provisionMethod",
        "provisionObjName",
        "status",
        "metadataTypeId",
        "selected",
        "ownerId",
        "internalGroupId",
        "operation",
        "parentGroups",
        "childGroups"
})
@XmlRootElement(name = "Group")
@XmlSeeAlso({
        Role.class,
        GroupAttribute.class
})
@Entity
@Table(name="GRP")
public class Group implements java.io.Serializable {

    // Fields

    /**
     *
     */
    private static final long serialVersionUID = 7657568959406790313L;

    protected AttributeOperationEnum operation;

    protected String grpId;
    protected String grpName;
    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    protected String createdBy;
    protected String companyId;
    protected String ownerId;
    /*protected String parentGrpId;*/
    protected Boolean inheritFromParent;
    protected String provisionMethod;
    protected String provisionObjName;
    protected String groupClass;
    protected String description;

    protected String status;
    @XmlSchemaType(name = "dateTime")
    protected Date lastUpdate;
    protected String lastUpdatedBy;
    protected String metadataTypeId;
    protected String internalGroupId = null;
    private Boolean selected = new Boolean(false);
    
    private Set<Group> parentGroups;
    private Set<Group> childGroups;


    @XmlJavaTypeAdapter(RoleSetAdapter.class)
    protected Set<Role> roles = new HashSet<Role>(0);

    @XmlJavaTypeAdapter(GroupAttributeMapAdapter.class)
    protected Map<String, GroupAttribute> attributes = new HashMap<String, GroupAttribute>(0);

    public Group() {
    }

    public Group(String grpId) {
        this.grpId = grpId;
    }

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="GRP_ID", length=20)
    public String getGrpId() {
        return this.grpId;
    }

    public void setGrpId(String grpId) {
        this.grpId = grpId;
    }

    @Column(name="GRP_NAME",length=80)
    public String getGrpName() {
        return this.grpName;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    @Column(name="CREATE_DATE",length=19)
    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name="CREATED_BY",length=20)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name="COMPANY_ID",length=20)
    public String getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    @Column(name="INHERIT_FROM_PARENT")
    @Type(type="boolean")
    public Boolean getInheritFromParent() {
        return this.inheritFromParent;
    }

    public void setInheritFromParent(Boolean inheritFromParent) {
        this.inheritFromParent = inheritFromParent;
    }

    @Column(name="PROVISION_METHOD",length=20)
    public String getProvisionMethod() {
        return this.provisionMethod;
    }

    public void setProvisionMethod(String provisionMethod) {
        this.provisionMethod = provisionMethod;
    }

    @Column(name="PROVISION_OBJ_NAME",length=80)
    public String getProvisionObjName() {
        return this.provisionObjName;
    }

    public void setProvisionObjName(String provisionObjName) {
        this.provisionObjName = provisionObjName;
    }

    @Transient
    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="GRP_ID", referencedColumnName="GRP_ID")
    @MapKeyColumn(name="name")
    public Map<String, GroupAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, GroupAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Updates the underlying collection with the GroupAttribute object that is being passed in.
     * The attribute is added if its does not exist and updated if its does exist.
     *
     * @param attr
     */

    public void saveAttribute(GroupAttribute attr) {
        attributes.put(attr.getName(), attr);
    }

    /**
     * Removes the attribute object from the underlying collection.
     *
     * @param attr
     */
    public void removeAttributes(GroupAttribute attr) {
        attributes.remove(attr.getName());
    }

    /**
     * Returns the attribute object that is specified by the NAME parameter.
     *
     * @param name - The attribute map is keyed on the NAME property.
     * @return
     */
    @Transient
    public GroupAttribute getAttribute(String name) {

        return attributes.get(name);

    }

    @Column(name="GROUP_CLASS",length=40)
    public String getGroupClass() {
        return groupClass;
    }

    public void setGroupClass(String groupClass) {
        this.groupClass = groupClass;
    }

    @Column(name="GROUP_DESC",length=80)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="LAST_UPDATE",length=19)
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column(name="LAST_UPDATED_BY",length=20)
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Column(name="TYPE_ID",length=20)
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

    @Column(name="OWNER_ID",length=20)
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


    public void setGroupStatus(GroupStatus status) {
        this.status = status.toString();
    }

    @Column(name="STATUS",length=20)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name="INTERNAL_GROUP_ID",length=32)
    public String getInternalGroupId() {
        return internalGroupId;
    }

    public void setInternalGroupId(String internalGroupId) {
        this.internalGroupId = internalGroupId;
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

    @ManyToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY)
    @JoinTable(name="grp_to_grp_membership",
        joinColumns={@JoinColumn(name="MEMBER_GROUP_ID")},
        inverseJoinColumns={@JoinColumn(name="GROUP_ID")})
    @Fetch(FetchMode.SUBSELECT)
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

	@ManyToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY)
    @JoinTable(name="grp_to_grp_membership",
        joinColumns={@JoinColumn(name="GROUP_ID")},
        inverseJoinColumns={@JoinColumn(name="MEMBER_GROUP_ID")})
    @Fetch(FetchMode.SUBSELECT)
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grpId == null) ? 0 : grpId.hashCode());
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
        if (!(obj instanceof Group)) {
            return false;
        }

        Group compareGroup = (Group) obj;

        return this.companyId.equals(compareGroup.companyId) &&
                this.description.equals(compareGroup.description) &&
                this.groupClass.equals(compareGroup.groupClass) &&
                this.grpId.equals(compareGroup.grpId) &&
                this.grpName.equals(compareGroup.grpName) &&
                this.internalGroupId.equals(compareGroup.internalGroupId) &&
                this.metadataTypeId.equals(compareGroup.metadataTypeId);

    }
}

