package org.openiam.authmanager.common.model;

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StopWatch;

public class AuthorizationUser extends AbstractAuthorizationEntity implements Serializable  {

	private static final Log log = LogFactory.getLog(AuthorizationUser.class);
	
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
		//final StopWatch sw1 = new StopWatch();
		//sw1.start();
		final Set<AuthorizationGroup> compiledGroupSet = visitGroups();
		for(final AuthorizationGroup group : compiledGroupSet) {
			linearGroupBitSet.set(group.getBitSetIdx());
		}
		//sw1.stop();
		//log.info(String.format("Group compilation: %s", sw1.getTotalTimeMillis()));
		
		//final StopWatch sw2 = new StopWatch();
		//sw2.start();
		final Set<AuthorizationRole> compiledRoleSet = visitRoles(compiledGroupSet);
		for(final AuthorizationRole role : compiledRoleSet) {
			linearRoleBitSet.set(role.getBitSetIdx());
		}
		//sw2.stop();
		//log.info(String.format("Role compilation: %s", sw2.getTotalTimeMillis()));
		
		//final StopWatch sw3 = new StopWatch();
		//sw3.start();
		final Set<AuthorizationResource> compiledResourceSet = visitResources(compiledGroupSet, compiledRoleSet);
		for(final AuthorizationResource resource : compiledResourceSet) {
			linearResourceBitSet.set(resource.getBitSetIdx());
		}
		//sw3.stop();
		//log.info(String.format("Resource compilation: %s", sw3.getTotalTimeMillis()));
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
	
	private Set<AuthorizationRole> visitRoles(final Set<AuthorizationGroup> compiledGroups) {
		final Set<AuthorizationRole> tempCompiledRoleSet = new HashSet<AuthorizationRole>();
		if(directParentRoles != null) {
			tempCompiledRoleSet.addAll(directParentRoles);
		}
		
		for(final AuthorizationGroup group : compiledGroups) {
			final Set<AuthorizationRole> roles = group.getRoles();
			if(roles != null) {
				tempCompiledRoleSet.addAll(roles);
			}
		}
		
		final Set<AuthorizationRole> visitedSet = new HashSet<AuthorizationRole>();
		final Set<AuthorizationRole> compiledRoleSet = new HashSet<AuthorizationRole>();
		for(final AuthorizationRole role : tempCompiledRoleSet) {
			compiledRoleSet.addAll(role.visitRoles(visitedSet));
			visitedSet.addAll(compiledRoleSet);
		}
		compiledRoleSet.addAll(tempCompiledRoleSet);
		
		return compiledRoleSet;
	}
	
	private Set<AuthorizationResource> visitResources(final Set<AuthorizationGroup> compiledGroups, final Set<AuthorizationRole> compiledRoles) {
		final Set<AuthorizationResource> tempCompiledResourceSet = new HashSet<AuthorizationResource>();
		//final StopWatch sw1 = new StopWatch();
		//sw1.start();
		if(directResources != null) {
			tempCompiledResourceSet.addAll(directResources);
		}
		//sw1.stop();
		//log.debug(String.format("Got direct Resources.  Size: %s.  Time: %s", tempCompiledResourceSet.size(), sw1.getTotalTimeMillis()));
		
		//final StopWatch sw2 = new StopWatch();
		//sw2.start();
		if(CollectionUtils.isNotEmpty(compiledGroups)) {
			for(final AuthorizationGroup group : compiledGroups) {
				final Set<AuthorizationResource> resources = group.getResources();
				if(resources != null) {
					tempCompiledResourceSet.addAll(resources);
				}
			}
		}
		//sw2.stop();
		//log.debug(String.format("Got Group Resources.  Size: %s.  Time: %s", tempCompiledResourceSet.size(), sw2.getTotalTimeMillis()));
		
		
		//final StopWatch sw3 = new StopWatch();
		//sw3.start();
		if(CollectionUtils.isNotEmpty(compiledRoles)) {
			for(final AuthorizationRole role : compiledRoles) {
				final Set<AuthorizationResource> resources = role.getResources();
				if(resources != null) {
					tempCompiledResourceSet.addAll(resources);
				}
			}
		}
		//sw3.stop();
		//log.debug(String.format("Got Role Resources.  Size: %s.  Time: %s", tempCompiledResourceSet.size(), sw3.getTotalTimeMillis()));

		//final StopWatch sw4 = new StopWatch();
		//sw4.start();
		final Set<AuthorizationResource> compiledResourceSet = new HashSet<AuthorizationResource>();
		final Set<AuthorizationResource> visitedSet = new HashSet<AuthorizationResource>();
		for(final AuthorizationResource resource : tempCompiledResourceSet) {
			compiledResourceSet.addAll(resource.visitResources(visitedSet));
			visitedSet.addAll(compiledResourceSet);
		}
		compiledResourceSet.addAll(tempCompiledResourceSet);
		//sw4.stop();
		//log.debug(String.format("Got Resource Resources.  Size: %s.  Time: %s", tempCompiledResourceSet.size(), sw4.getTotalTimeMillis()));
		return compiledResourceSet;
	}
	
	/*
	private Set<AuthorizationResource> visitResources() {
		final StopWatch sw1 = new StopWatch();
		sw1.start();
		final Set<AuthorizationResource> compiledResourceSet = new HashSet<AuthorizationResource>();
		if(directParentGroups != null) {
			for(final AuthorizationGroup group : directParentGroups) {
				compiledResourceSet.addAll(group.visitResources(new HashSet<AuthorizationGroup>()));
			}
		}
		sw1.stop();
		log.info(String.format("Time to compile resources from groups: %s", sw1.getTotalTimeMillis()));
		
		final StopWatch sw2 = new StopWatch();
		sw2.start();
		if(directParentRoles != null) {
			for(final AuthorizationRole role : directParentRoles) {
				compiledResourceSet.addAll(role.visitResources(new HashSet<AuthorizationRole>()));
			}
		}
		sw2.stop();
		log.info(String.format("Time to compile resources from roles: %s", sw2.getTotalTimeMillis()));
		
		final StopWatch sw3 = new StopWatch();
		sw3.start();
		if(directResources != null) {
			for(final AuthorizationResource resource : directResources) {
				compiledResourceSet.add(resource);
				compiledResourceSet.addAll(resource.visitResources(new HashSet<AuthorizationResource>()));
			}
		}
		sw3.stop();
		log.info(String.format("Time to compile resources from resources: %s", sw3.getTotalTimeMillis()));
		
		return compiledResourceSet;
	}
	*/

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
