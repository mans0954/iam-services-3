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
        "name"
})
public class AuthorizationRole extends AbstractAuthorizationEntity implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@XmlTransient
	private Set<AuthorizationRole> directParentRoles;
	
	@XmlTransient
	private Set<AuthorizationResource> directResources;
	
	
	private String name;

	/*
	private BitSet linearRoleBitSet = new BitSet();
	private BitSet linearResourceBitSet = new BitSet();
	*/
	
	public AuthorizationRole() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addParentRole(final AuthorizationRole role) {
		if(directParentRoles == null) {
			directParentRoles = new HashSet<AuthorizationRole>();
		}
		directParentRoles.add(role);
	}
	
	public void addResource(final AuthorizationResource resource) {
		if(directResources == null) {
			directResources = new HashSet<AuthorizationResource>();
		}
		directResources.add(resource);
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
			if(directParentRoles != null) {
				for(final AuthorizationRole parent : directParentRoles) {
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
			
			if(directParentRoles != null) {
				for(final AuthorizationRole role : directParentRoles) {
					compiledResources.addAll(role.visitResources(visitedRoles));
				}
			}
			
			if(directResources != null) {
				for(final AuthorizationResource resource : directResources) {
					compiledResources.add(resource);
					compiledResources.addAll(resource.visitResources(new HashSet<AuthorizationResource>()));
				}
			}
		}
		return compiledResources;
	}
}
