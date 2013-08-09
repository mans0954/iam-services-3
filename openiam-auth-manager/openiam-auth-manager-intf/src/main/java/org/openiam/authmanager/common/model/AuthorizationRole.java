package org.openiam.authmanager.common.model;

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationRole", propOrder = {
})
public class AuthorizationRole extends AbstractAuthorizationEntity implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@XmlTransient
	private Set<AuthorizationRole> parentRoles;
	
	@XmlTransient
	private Set<AuthorizationResource> resources;

	/*
	private BitSet linearRoleBitSet = new BitSet();
	private BitSet linearResourceBitSet = new BitSet();
	*/
	
	public AuthorizationRole() {
		
	}
	
	public void addParentRole(final AuthorizationRole role) {
		if(parentRoles == null) {
			parentRoles = new HashSet<AuthorizationRole>();
		}
		parentRoles.add(role);
	}
	
	public Set<AuthorizationRole> getParentRoles() {
		Set<AuthorizationRole> retVal = null;
		if(parentRoles != null) {
			retVal = new HashSet<AuthorizationRole>(parentRoles);
		}
		return retVal;
	}
	
	public Set<AuthorizationResource> getResources() {
		Set<AuthorizationResource> retVal = null;
		if(resources != null) {
			retVal = new HashSet<AuthorizationResource>(resources);
		}
		return retVal;
	}
	
	public void addResource(final AuthorizationResource resource) {
		if(resources == null) {
			resources = new HashSet<AuthorizationResource>();
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
	
	public Set<AuthorizationRole> visitRoles(final Set<AuthorizationRole> visitedSet) {
		final Set<AuthorizationRole> compiledRoles = new HashSet<AuthorizationRole>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(parentRoles != null) {
				for(final AuthorizationRole parent : parentRoles) {
					compiledRoles.add(parent);
					compiledRoles.addAll(parent.visitRoles(visitedSet));
				}
			}
		}
		return compiledRoles;
	}
	
	public Set<AuthorizationResource> visitResources(final Set<AuthorizationRole> visitedRoles) {		
		final Set<AuthorizationResource> compiledResources = new HashSet<AuthorizationResource>();
		if(!visitedRoles.contains(this)) {
			visitedRoles.add(this);
			
			if(parentRoles != null) {
				for(final AuthorizationRole role : parentRoles) {
					compiledResources.addAll(role.visitResources(visitedRoles));
				}
			}
			
			if(resources != null) {
				for(final AuthorizationResource resource : resources) {
					compiledResources.add(resource);
					compiledResources.addAll(resource.visitResources(new HashSet<AuthorizationResource>()));
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
