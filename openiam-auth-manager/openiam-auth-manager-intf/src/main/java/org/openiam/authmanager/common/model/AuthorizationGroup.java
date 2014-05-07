package org.openiam.authmanager.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationGroup", propOrder = {
})
public class AuthorizationGroup extends AbstractAuthorizationEntity implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	@XmlTransient
	private Set<AuthorizationGroup> parentGroups = null;
	
	@XmlTransient
	private Set<AuthorizationRole> roles = null;
	
	@XmlTransient
	private Set<AuthorizationResource> resources = null;
//

	/*
	private BitSet linearGroupBitSet = new BitSet();
	private BitSet linearRoleBitSet = new BitSet();
	private BitSet linearResourceBitSet = new BitSet();
	*/
	
	public AuthorizationGroup() {
		
	}
	
	public void addParentGroup(final AuthorizationGroup group) {
		if(parentGroups == null) {
			parentGroups = new HashSet<AuthorizationGroup>();
		}
		parentGroups.add(group);
	}
	
	public void addRole(final AuthorizationRole role) {
		if(roles == null) {
			roles = new HashSet<AuthorizationRole>();
		}
		roles.add(role);
	}
	
	public void addResource(final AuthorizationResource resource) {
		if(resources == null) {
			resources = new HashSet<AuthorizationResource>();
		}
		resources.add(resource);
	}
	
	public Set<AuthorizationResource> getResources() {
		Set<AuthorizationResource> retVal = null;
		if(resources != null) {
			retVal = new HashSet<AuthorizationResource>(resources);
		}
		return retVal;
	}
	
	public Set<AuthorizationRole> getRoles() {
		Set<AuthorizationRole> retVal = null;
		if(roles != null) {
			retVal = new HashSet<AuthorizationRole>(roles);
		}
		return retVal;
	}
	
	public Set<AuthorizationGroup> getParentGroups() {
		Set<AuthorizationGroup> retVal = null;
		if(parentGroups != null) {
			retVal = new HashSet<AuthorizationGroup>(parentGroups);
		}
		return retVal;
	}


    /**
	 * Compiles this Group against it's Role, Group, and Resource Membership
	 */
	public void compile() {
		/*
		final Set<Group> compiledGroupSet = visitGroups(new HashSet<Group>());
		for(final Group group : compiledGroupSet) {
			linearGroupBitSet.set(group.getBitSetIdx());
		}
		
		final Set<Role> compiledRoleSet = visitRoles(new HashSet<Group>());
		for(final Role role : compiledRoleSet) {
			linearRoleBitSet.set(role.getBitSetIdx());
		}
		
		final Set<Resource> compiledResourceSet = visitResources(new HashSet<Group>());
		for(final Resource resource : compiledResourceSet) {
			linearResourceBitSet.set(resource.getBitSetIdx());
		}
		*/
	}
	
	public Set<AuthorizationGroup> visitGroups(final Set<AuthorizationGroup> visitedSet) {
		final Set<AuthorizationGroup> compiledGroupSet = new HashSet<AuthorizationGroup>();
		if(!visitedSet.contains(this)) {
			if(parentGroups != null) {
				visitedSet.add(this);
				for(final AuthorizationGroup group : parentGroups) {
					compiledGroupSet.add(group);
					compiledGroupSet.addAll(group.visitGroups(visitedSet));
				}
			}
		}
		return compiledGroupSet;
	}
	
	public Set<AuthorizationRole> visitRoles(final Set<AuthorizationGroup> visitedSet) {
		final Set<AuthorizationRole> compiledRoleSet = new HashSet<AuthorizationRole>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(parentGroups != null) {
				for(final AuthorizationGroup group : parentGroups) {
					compiledRoleSet.addAll(group.visitRoles(visitedSet));
				}
			}
			
			if(roles != null) {
				for(final AuthorizationRole role : roles) {
					compiledRoleSet.add(role);
					compiledRoleSet.addAll(role.visitRoles(new HashSet<AuthorizationRole>()));
				}
			}
		}
		return compiledRoleSet;
	}
	
	public Set<AuthorizationResource> visitResources(final Set<AuthorizationGroup> visitedSet) {
		final Set<AuthorizationResource> compiledResourceSet = new HashSet<AuthorizationResource>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(parentGroups != null) {
				for(final AuthorizationGroup group : parentGroups) {
					compiledResourceSet.addAll(group.visitResources(visitedSet));
				}
			}
			
			if(roles != null) {
				for(final AuthorizationRole role : roles) {
					compiledResourceSet.addAll(role.visitResources(new HashSet<AuthorizationRole>()));
				}
			}
			
			if(resources != null) {
				for(final AuthorizationResource resource : resources) {
					compiledResourceSet.add(resource);
					compiledResourceSet.addAll(resource.visitResources(new HashSet<AuthorizationResource>()));
				}
			}
		}
		return compiledResourceSet;
	}
	
	public AuthorizationGroup shallowCopy() {
		final AuthorizationGroup copy = new AuthorizationGroup();
		super.makeCopy(copy);
		return copy;
	}
}
