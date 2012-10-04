package org.openiam.authmanager.common.model;

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class AuthorizationUser extends AbstractEntity implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	private Set<AuthorizationGroup> directParentGroups = null;
	private Set<AuthorizationRole> directParentRoles = null;
	private Set<AuthorizationResource> directResources = null;
	
	private BitSet linearGroupBitSet = new BitSet();
	private BitSet linearRoleBitSet = new BitSet();
	private BitSet linearResourceBitSet = new BitSet();
	
	public AuthorizationUser() {
	}
	
	public void addGroup(final AuthorizationGroup group) {
		if(group != null) {
			if(directParentGroups == null) {
				directParentGroups = new HashSet<AuthorizationGroup>();
			}
			directParentGroups.add(group);
		}
	}
	
	public void addRole(final AuthorizationRole role) {
		if(role != null) {
			if(directParentRoles == null) {
				directParentRoles = new HashSet<AuthorizationRole>();
			}
			directParentRoles.add(role);
		}
	}
	
	public void addResource(final AuthorizationResource resource) {
		if(resource != null) {
			if(directResources == null) {
				directResources = new HashSet<AuthorizationResource>();
			}
			directResources.add(resource);
		}
	}
	
	/**
	 * Compiles this Group against it's Role, Group, and Resource Membership
	 */
	public void compile() {
		final Set<AuthorizationGroup> compiledGroupSet = visitGroups();
		for(final AuthorizationGroup group : compiledGroupSet) {
			linearGroupBitSet.set(group.getBitSetIdx());
		}
		
		final Set<AuthorizationRole> compiledRoleSet = visitRoles();
		for(final AuthorizationRole role : compiledRoleSet) {
			linearRoleBitSet.set(role.getBitSetIdx());
		}
		
		final Set<AuthorizationResource> compiledResourceSet = visitResources();
		for(final AuthorizationResource resource : compiledResourceSet) {
			linearResourceBitSet.set(resource.getBitSetIdx());
		}
	}
	
	private Set<AuthorizationGroup> visitGroups() {
		final Set<AuthorizationGroup> compiledGroupSet = new HashSet<AuthorizationGroup>();
		if(directParentGroups != null) {
			for(final AuthorizationGroup group : directParentGroups) {
				compiledGroupSet.add(group);
				compiledGroupSet.addAll(group.visitGroups(new HashSet<AuthorizationGroup>()));
			}
		}
		return compiledGroupSet;
	}
	
	private Set<AuthorizationRole> visitRoles() {
		final Set<AuthorizationRole> compiledRoleSet = new HashSet<AuthorizationRole>();
		if(directParentGroups != null) {
			for(final AuthorizationGroup group : directParentGroups) {
				compiledRoleSet.addAll(group.visitRoles(new HashSet<AuthorizationGroup>()));
			}
		}
			
		if(directParentRoles != null) {
			for(final AuthorizationRole role : directParentRoles) {
				compiledRoleSet.add(role);
				compiledRoleSet.addAll(role.visitRoles(new HashSet<AuthorizationRole>()));
			}
		}
		return compiledRoleSet;
	}
	
	private Set<AuthorizationResource> visitResources() {
		final Set<AuthorizationResource> compiledResourceSet = new HashSet<AuthorizationResource>();
		if(directParentGroups != null) {
			for(final AuthorizationGroup group : directParentGroups) {
				compiledResourceSet.addAll(group.visitResources(new HashSet<AuthorizationGroup>()));
			}
		}
		
		if(directParentRoles != null) {
			for(final AuthorizationRole role : directParentRoles) {
				compiledResourceSet.addAll(role.visitResources(new HashSet<AuthorizationRole>()));
			}
		}
		
		if(directResources != null) {
			for(final AuthorizationResource resource : directResources) {
				compiledResourceSet.add(resource);
				compiledResourceSet.addAll(resource.visitResources(new HashSet<AuthorizationResource>()));
			}
		}
		return compiledResourceSet;
	}

	public boolean isEntitledTo(final AuthorizationResource resource) {
		return (resource != null) ? linearResourceBitSet.get(resource.getBitSetIdx()) : false;
	}
	
	public boolean isMemberOf(final AuthorizationGroup group) {
		return (group != null) ? linearGroupBitSet.get(group.getBitSetIdx()) : false;
	}
	
	public boolean isMemberOf(final AuthorizationRole role) {
		return (role != null) ? linearRoleBitSet.get(role.getBitSetIdx()) : false;
	}
	
	public Set<Integer> getLinearRoles() {
		final Set<Integer> linearBitSet = new HashSet<Integer>();
		for(int i = 0; i < linearRoleBitSet.size(); i++) {
			if(linearRoleBitSet.get(i)) {
				linearBitSet.add(new Integer(i));
			}
		}
		return linearBitSet;
	}
	
	public Set<Integer> getLinearGroups() {
		final Set<Integer> linearBitSet = new HashSet<Integer>();
		for(int i = 0; i < linearGroupBitSet.size(); i++) {
			if(linearGroupBitSet.get(i)) {
				linearBitSet.add(new Integer(i));
			}
		}
		return linearBitSet;
	}
	
	public Set<Integer> getLinearResources() {
		final Set<Integer> linearBitSet = new HashSet<Integer>();
		for(int i = 0; i < linearResourceBitSet.size(); i++) {
			if(linearResourceBitSet.get(i)) {
				linearBitSet.add(new Integer(i));
			}
		}
		return linearBitSet;
	}
}
