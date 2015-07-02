package org.openiam.authmanager.common.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.xref.AbstractGroupXref;
import org.openiam.authmanager.common.xref.AbstractResourceXref;
import org.openiam.authmanager.common.xref.AbstractRoleXref;
import org.openiam.authmanager.common.xref.ResourceRoleXref;
import org.openiam.authmanager.common.xref.RoleGroupXref;
import org.openiam.authmanager.common.xref.RoleRoleXref;
import org.openiam.idm.srvc.role.domain.RoleEntity;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationRole", propOrder = {
})
public class AuthorizationRole extends AbstractAuthorizationEntity implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@XmlTransient
	private Set<RoleRoleXref> parentRoles;
	
	@XmlTransient
	private Set<ResourceRoleXref> resources;
	
	@XmlTransient
	private Set<RoleGroupXref> groups = null;

	private AuthorizationRole() {
		
	}
	
	public AuthorizationRole(final RoleEntity entity, final int bitIdx) {
		super.setBitSetIdx(bitIdx);
		super.setDescription(entity.getDescription());
		super.setId(entity.getId());
		super.setName(entity.getName());
		super.setStatus(entity.getStatus());
	}
	
	public void addParentRole(final RoleRoleXref role) {
		if(parentRoles == null) {
			parentRoles = new HashSet<RoleRoleXref>();
		}
		parentRoles.add(role);
	}
	
	public Set<AbstractResourceXref> getResources() {
		Set<AbstractResourceXref> retVal = null;
		if(resources != null) {
			retVal = new HashSet<AbstractResourceXref>(resources);
		}
		return retVal;
	}
	
	public Set<AbstractGroupXref> getGroups() {
		Set<AbstractGroupXref> retVal = null;
		if(groups != null) {
			retVal = new HashSet<AbstractGroupXref>(groups);
		}
		return retVal;
	}
	
	public void addGroup(final RoleGroupXref group) {
		if(groups == null) {
			groups = new HashSet<RoleGroupXref>();
		}
		groups.add(group);
	}
	
	
	public void addResource(final ResourceRoleXref resource) {
		if(resources == null) {
			resources = new HashSet<ResourceRoleXref>();
		}
		resources.add(resource);
	}
	
	/**
	 * Compiles this Role against it's Role and Resource Membership
	 */
	@Override
	public void compile() {
		/*
		final Set<Role> compiledRoles = visitRoles(new HashSet<Role>());
		for(final Role role : compiledRoles) {
			linearRoleBitSet.set(new Integer(role.getBitSetIdx()));
		}
		
		final Set<Resource> compiledResources = visitResources(new HashSet<Role>());
		for(final Resource resource : compiledResources) {
			linearResourceBitSet.set(resource.getBitSetIdx());
		}
		*/
	}
	
	public Set<AbstractRoleXref> visitRoles(final Set<AuthorizationRole> visitedSet) {
		final Set<AbstractRoleXref> compiledRoles = new HashSet<AbstractRoleXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(parentRoles != null) {
				for(final AbstractRoleXref xref : parentRoles) {
					compiledRoles.add(xref);
					compiledRoles.addAll(xref.getRole().visitRoles(visitedSet));
				}
			}
		}
		return compiledRoles;
	}
	

	public Set<AbstractGroupXref> visitGroups(final Set<AuthorizationRole> visitedSet) {
		final Set<AbstractGroupXref> compiledGroupSet = new HashSet<AbstractGroupXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(parentRoles != null) {
				for(final AbstractRoleXref xref : parentRoles) {
					compiledGroupSet.addAll(xref.getRole().visitGroups(visitedSet));
				}
			}
			
			if(groups != null) {
				for(final AbstractGroupXref xref : groups) {
					compiledGroupSet.add(xref);
					compiledGroupSet.addAll(xref.getGroup().visitGroups(new HashSet<AuthorizationGroup>()));
				}
			}
		}
		return compiledGroupSet;
	}
	
	
	public Set<AbstractResourceXref> visitResources(final Set<AuthorizationRole> visitedRoles) {		
		final Set<AbstractResourceXref> compiledResources = new HashSet<AbstractResourceXref>();
		if(!visitedRoles.contains(this)) {
			visitedRoles.add(this);
			
			if(parentRoles != null) {
				for(final AbstractRoleXref xref : parentRoles) {
					compiledResources.addAll(xref.getRole().visitResources(visitedRoles));
				}
			}
			
			if(groups != null) {
				for(final AbstractGroupXref xref : groups) {
					compiledResources.addAll(xref.getGroup().visitResources(new HashSet<AuthorizationGroup>()));
				}
			}
			
			if(resources != null) {
				for(final AbstractResourceXref xref : resources) {
					compiledResources.add(xref);
					compiledResources.addAll(xref.getResource().visitResources(new HashSet<AuthorizationResource>()));
				}
			}
		}
		return compiledResources;
	}
	
	public AuthorizationRole shallowCopy() {
		final AuthorizationRole copy = new AuthorizationRole();
		super.makeCopy(copy);
		return copy;
	}

}
