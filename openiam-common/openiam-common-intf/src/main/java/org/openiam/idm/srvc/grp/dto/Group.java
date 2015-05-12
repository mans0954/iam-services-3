package org.openiam.idm.srvc.grp.dto;


import org.apache.commons.lang.StringUtils;
import org.openiam.base.AdminResourceDTO;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseObject;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleSetAdapter;
import org.openiam.idm.srvc.role.dto.UserSetAdapter;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.Serializable;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Group", propOrder = {
        "roles",
        "attributes",
        "managedSysId",
        "managedSysName",
        "createDate",
        "createdBy",
        "description",
        "lastUpdate",
        "lastUpdatedBy",
        "status",
        "operation",
        "parentGroups",
        "childGroups",
        "resources",
		"classificationId",
		"classificationName",
		"adGroupTypeId",
		"adGroupTypeName",
		"adGroupScopeId",
		"adGroupScopeName",
		"riskId",
		"riskName",
		"maxUserNumber",
		"membershipDuration",
		"organizations",
		"owner"
})
@XmlSeeAlso({
        Role.class,
        GroupAttribute.class,
        Resource.class,
        User.class
})
@DozerDTOCorrespondence(GroupEntity.class)
public class Group extends AdminResourceDTO implements Serializable {

    private static final long serialVersionUID = 7657568959406790313L;

    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    protected String managedSysId;
    protected String managedSysName;
    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    protected String createdBy;
    protected String description;

    protected String status;
    @XmlSchemaType(name = "dateTime")
    protected Date lastUpdate;
    protected String lastUpdatedBy;

    protected Set<Group> parentGroups;
    protected Set<Group> childGroups;

    protected Set<Resource> resources;

    @XmlJavaTypeAdapter(RoleSetAdapter.class)
    protected Set<Role> roles = new HashSet<Role>(0);

    //@XmlJavaTypeAdapter(GroupAttributeMapAdapter.class)
    protected Set<GroupAttribute> attributes = new HashSet<GroupAttribute>();

    protected String classificationId;
    protected String classificationName;

    protected String adGroupTypeId;
    protected String adGroupTypeName;

    protected String adGroupScopeId;
    protected String adGroupScopeName;

    protected String riskId;
    protected String riskName;

    protected Integer maxUserNumber;
    protected Long membershipDuration;

	protected Set<Organization> organizations = new HashSet<Organization>(0);

 	protected GroupOwner owner;

    public Group() {
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

    public void setGroupStatus(GroupStatus status) {
        this.status = status.toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public void addResource(final Resource resource) {
        if(resource != null) {
            if(resources == null) {
                resources = new HashSet<Resource>();
            }
            resources.add(resource);
        }
    }

    public void addRole(final Role role) {
        if(role != null) {
            if(roles == null) {
                roles = new HashSet<Role>();
            }
            roles.add(role);
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

	public String getClassificationId() {
		return classificationId;
	}

	public void setClassificationId(String classificationId) {
		this.classificationId = classificationId;
	}

	public String getClassificationName() {
		return classificationName;
	}

	public void setClassificationName(String classificationName) {
		this.classificationName = classificationName;
	}

	public String getAdGroupTypeId() {
		return adGroupTypeId;
	}

	public void setAdGroupTypeId(String adGroupTypeId) {
		this.adGroupTypeId = adGroupTypeId;
	}

	public String getAdGroupTypeName() {
		return adGroupTypeName;
	}

	public void setAdGroupTypeName(String adGroupTypeName) {
		this.adGroupTypeName = adGroupTypeName;
	}

	public String getAdGroupScopeId() {
		return adGroupScopeId;
	}

	public void setAdGroupScopeId(String adGroupScopeId) {
		this.adGroupScopeId = adGroupScopeId;
	}

	public String getAdGroupScopeName() {
		return adGroupScopeName;
	}

	public void setAdGroupScopeName(String adGroupScopeName) {
		this.adGroupScopeName = adGroupScopeName;
	}

	public String getRiskId() {
		return riskId;
	}

	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}

	public String getRiskName() {
		return riskName;
	}

	public void setRiskName(String riskName) {
		this.riskName = riskName;
	}

	public Integer getMaxUserNumber() {
		return maxUserNumber;
	}

	public void setMaxUserNumber(Integer maxUserNumber) {
		this.maxUserNumber = maxUserNumber;
	}

	public Long getMembershipDuration() {
		return membershipDuration;
	}

	public void setMembershipDuration(Long membershipDuration) {
		this.membershipDuration = membershipDuration;
	}

	public Set<Organization> getOrganizations() {
		return organizations;
	}

	public void addOrganization(final Organization org) {
		if (org != null) {
			if (organizations == null) {
				organizations = new HashSet<Organization>();
			}
			org.setOperation(AttributeOperationEnum.ADD);
			organizations.add(org);
		}
	}

	public void markOrganizationAsDeleted(final String id) {
		if (id != null) {
			if (organizations != null) {
				for (final Organization organization : organizations) {
					if (StringUtils.equals(organization.getId(), id)) {
						organization.setOperation(AttributeOperationEnum.DELETE);
						break;
					}
				}
			}
		}
	}

	public void setOrganizations(Set<Organization> organizations) {
		this.organizations = organizations;
	}

	public GroupOwner getOwner() {
		return owner;
	}

	public void setOwner(GroupOwner owner) {
		this.owner = owner;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Group group = (Group) o;
        if (name != null ? !name.equals(group.name) : group.name != null) return false;
        if (id != null ? !id.equals(group.id) : group.id != null) return false;

        if (createDate != null ? !createDate.equals(group.createDate) : group.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(group.createdBy) : group.createdBy != null) return false;
        if (managedSysId != null ? !managedSysId.equals(group.managedSysId) : group.managedSysId != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return String
				.format("Group [operation=%s, managedSysId=%s, managedSysName=%s, id=%s, name=%s, createDate=%s, createdBy=%s, description=%s, status=%s, lastUpdate=%s, lastUpdatedBy=%s" +
								", classificationId=%s, classificationName=%s, adGroupTypeId=%s, adGroupTypeName=%s, adGroupScopeId=%s, adGroupScopeName=%s, riskId=%s, riskName=%s, maxUserNumber=%s, membershipDuration=%s]",
						operation, managedSysId, managedSysName, id,
						name, createDate, createdBy, description, status,
						lastUpdate, lastUpdatedBy,
						classificationId, classificationName, adGroupTypeId, adGroupTypeName,
						adGroupScopeId, adGroupScopeName, riskId, riskName, maxUserNumber, membershipDuration);
	}

	
}

