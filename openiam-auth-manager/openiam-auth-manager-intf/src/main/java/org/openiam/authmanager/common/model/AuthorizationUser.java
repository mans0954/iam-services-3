package org.openiam.authmanager.common.model;

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.xref.AbstractGroupXref;
import org.openiam.authmanager.common.xref.AbstractOrgXref;
import org.openiam.authmanager.common.xref.AbstractResourceXref;
import org.openiam.authmanager.common.xref.AbstractRoleXref;
import org.openiam.authmanager.common.xref.GroupUserXref;
import org.openiam.authmanager.common.xref.OrgUserXref;
import org.openiam.authmanager.common.xref.ResourceUserXref;
import org.openiam.authmanager.common.xref.RoleUserXref;

public class AuthorizationUser extends AbstractAuthorizationEntity implements Serializable  {

	private static final Log log = LogFactory.getLog(AuthorizationUser.class);
	
	private static final long serialVersionUID = 1L;
	
	private Set<GroupUserXref> directParentGroups = null;
	private Set<RoleUserXref> directParentRoles = null;
	private Set<ResourceUserXref> directResources = null;
	private Set<OrgUserXref> directOrganizations = null;
	
	private BitSet linearGroupBitSet = new BitSet();
	private BitSet linearRoleBitSet = new BitSet();
	private BitSet linearResourceBitSet = new BitSet();
	private BitSet linearOrganizationBitSet = new BitSet();
	
	public AuthorizationUser() {
		
	}
	
	public AuthorizationUser(final AuthorizationUser entity) {
		super(entity);
	}
	
	public AuthorizationUser(final InternalAuthroizationUser entity) {
		super.setId(entity.getUserId());
	}
	
	public void addOrganization(final OrgUserXref organization) {
		if(organization != null) {
			if(directOrganizations == null) {
				directOrganizations = new HashSet<OrgUserXref>();
			}
			directOrganizations.add(organization);
		}
	}
	
	public void addGroup(final GroupUserXref group) {
		if(group != null) {
			if(directParentGroups == null) {
				directParentGroups = new HashSet<GroupUserXref>();
			}
			directParentGroups.add(group);
		}
	}
	
	public void addRole(final RoleUserXref role) {
		if(role != null) {
			if(directParentRoles == null) {
				directParentRoles = new HashSet<RoleUserXref>();
			}
			directParentRoles.add(role);
		}
	}
	
	public void addResource(final ResourceUserXref resource) {
		if(resource != null) {
			if(directResources == null) {
				directResources = new HashSet<ResourceUserXref>();
			}
			directResources.add(resource);
		}
	}

	private int getBitIndex(final AuthorizationAccessRight right, final AbstractAuthorizationEntity entity, final int numOfRights) {
		final int rightBit = (right != null) ? (right.getBitIdx()) : 0;
		final int offset = (entity.getBitSetIdx() * numOfRights);
		return rightBit + offset;
	}
	
	/**
	 * Reverse engineers the algorithm for calculating a bitset, and return the bit for the 'right'
	 * @param bit - bit from the internal bitset of the Collection
	 * @param entity - Entity you're looking up
	 * @param numOfRights - number of Authorization Rights
	 * @return
	 */
	public static int getRightBit(final int bit, final AbstractAuthorizationEntity entity, final int numOfRights) {
		return bit - (entity.getBitSetIdx() * numOfRights);
	}
	
	/**
	 * * Reverse engineers the algorithm for calculating a bitset, and return the bit for the 'entity'
	 * @param bit - bit from the internal bitset of the Collection
	 * @param numOfRights - number of Authorization Rights
	 * @return
	 */
	public static Integer getEntityBit(final int bit, final int numOfRights) {
		/* 
		 * right bit is 0, since you're not looking at rights
		 * if mod is not 0, then it's a right big, return null in this case 
		 */
		return (bit % numOfRights == 0) ? (bit / numOfRights) : null;
	}
	
	/**
	 * Compiles this Group against it's Role, Group, and Resource Membership
	 */
	public void compile(final int numOfRights) {
		final Set<AbstractOrgXref> compiledOrgSet = visitOrganization();
		for(final AbstractOrgXref xref : compiledOrgSet) {
			if(CollectionUtils.isNotEmpty(xref.getRights())) {
				xref.getRights().forEach(right -> {
					linearOrganizationBitSet.set(getBitIndex(right, xref.getOrganization(), numOfRights));
				});
			}
			linearOrganizationBitSet.set(getBitIndex(null, xref.getOrganization(), numOfRights));
		}
		
		final Set<AbstractRoleXref> compiledRoleSet = visitRoles(compiledOrgSet);
		for(final AbstractRoleXref xref : compiledRoleSet) {
			if(CollectionUtils.isNotEmpty(xref.getRights())) {
				xref.getRights().forEach(right -> {
					linearRoleBitSet.set(getBitIndex(right, xref.getRole(), numOfRights));
				});
			}
			linearRoleBitSet.set(getBitIndex(null, xref.getRole(), numOfRights));
		}

		final Set<AbstractGroupXref> compiledGroupSet = visitGroups(compiledOrgSet, compiledRoleSet);
		for(final AbstractGroupXref xref : compiledGroupSet) {
			if(CollectionUtils.isNotEmpty(xref.getRights())) {
				xref.getRights().forEach(right -> {
					linearGroupBitSet.set(getBitIndex(right, xref.getGroup(), numOfRights));
				});
			}
			linearGroupBitSet.set(getBitIndex(null, xref.getGroup(), numOfRights));
		}
		
		final Set<AbstractResourceXref> compiledResourceSet = visitResources(compiledOrgSet, compiledGroupSet, compiledRoleSet);
		for(final AbstractResourceXref xref : compiledResourceSet) {
			if(CollectionUtils.isNotEmpty(xref.getRights())) {
				xref.getRights().forEach(right -> {
					linearResourceBitSet.set(getBitIndex(right, xref.getResource(), numOfRights));
				});
			}
			linearResourceBitSet.set(getBitIndex(null, xref.getResource(), numOfRights));
		}
	}
	
	private Set<AbstractOrgXref> visitOrganization() {
		final Set<AbstractOrgXref> tempCompiledSet = new HashSet<AbstractOrgXref>();
		if(directOrganizations != null) {
			tempCompiledSet.addAll(directOrganizations);
		}
		
		final Set<AbstractOrgXref> compiledSet = new HashSet<AbstractOrgXref>();
		
		final Set<AuthorizationOrganization> visitedSet = new HashSet<AuthorizationOrganization>();
		for(final AbstractOrgXref xref : tempCompiledSet) {
			final Set<AbstractOrgXref> justVisited = xref.getOrganization().visitOrganizations(visitedSet);
			compiledSet.addAll(justVisited);
			visitedSet.addAll(justVisited.stream().map(e -> e.getOrganization()).collect(Collectors.toSet()));
		}
		compiledSet.addAll(tempCompiledSet);
		
		return compiledSet;
	}
	
	
	private Set<AbstractRoleXref> visitRoles(final Set<AbstractOrgXref> compiledOrgs) {
		final Set<AbstractRoleXref> tempCompiledSet = new HashSet<AbstractRoleXref>();
		if(directParentRoles != null) {
			tempCompiledSet.addAll(directParentRoles);
		}
		
		if(CollectionUtils.isNotEmpty(compiledOrgs)) {
			for(final AbstractOrgXref xref : compiledOrgs) {
				final Set<AbstractRoleXref> orgEntities = xref.getOrganization().getRoles();
				if(orgEntities != null) {
					tempCompiledSet.addAll(orgEntities);
				}
			}
		}
		
		final Set<AuthorizationRole> visitedSet = new HashSet<AuthorizationRole>();
		final Set<AbstractRoleXref> compiledSet = new HashSet<AbstractRoleXref>();
		for(final AbstractRoleXref xref : tempCompiledSet) {
			final Set<AbstractRoleXref> justVisited = xref.getRole().visitRoles(visitedSet);
			compiledSet.addAll(justVisited);
			visitedSet.addAll(justVisited.stream().map(e -> e.getRole()).collect(Collectors.toSet()));
		}
		compiledSet.addAll(tempCompiledSet);
		
		return compiledSet;
	}
	
	private Set<AbstractGroupXref> visitGroups(final Set<AbstractOrgXref> compiledOrgs, final Set<AbstractRoleXref> compiledRoles) {
		final Set<AbstractGroupXref> tempCompiledSet = new HashSet<AbstractGroupXref>();
		if(directParentGroups != null) {
			tempCompiledSet.addAll(directParentGroups);
		}
		
		if(CollectionUtils.isNotEmpty(compiledOrgs)) {
			for(final AbstractOrgXref xref : compiledOrgs) {
				final Set<AbstractGroupXref> orgEntities = xref.getOrganization().getGroups();
				if(orgEntities != null) {
					tempCompiledSet.addAll(orgEntities);
				}
			}
		}
		
		for(final AbstractRoleXref xref : compiledRoles) {
			final Set<AbstractGroupXref> groups = xref.getRole().getGroups();
			if(groups != null) {
				tempCompiledSet.addAll(groups);
			}
		}
		
		final Set<AuthorizationGroup> visitedSet = new HashSet<AuthorizationGroup>();
		final Set<AbstractGroupXref> compiledSet = new HashSet<AbstractGroupXref>();
		for(final AbstractGroupXref xref : tempCompiledSet) {
			final Set<AbstractGroupXref> justVisited = xref.getGroup().visitGroups(visitedSet);
			compiledSet.addAll(justVisited);
			visitedSet.addAll(justVisited.stream().map(e -> e.getGroup()).collect(Collectors.toSet()));
		}
		compiledSet.addAll(tempCompiledSet);
		
		return compiledSet;
	}
	
	private Set<AbstractResourceXref> visitResources(final Set<AbstractOrgXref> compiledOrgs, final Set<AbstractGroupXref> compiledGroups, final Set<AbstractRoleXref> compiledRoles) {
		final Set<AbstractResourceXref> tempCompiledSet = new HashSet<AbstractResourceXref>();
		if(directResources != null) {
			tempCompiledSet.addAll(directResources);
		}

		if(CollectionUtils.isNotEmpty(compiledGroups)) {
			for(final AbstractGroupXref xref : compiledGroups) {
				final Set<AbstractResourceXref> resources = xref.getGroup().getResources();
				if(resources != null) {
					tempCompiledSet.addAll(resources);
				}
			}
		}

		if(CollectionUtils.isNotEmpty(compiledRoles)) {
			for(final AbstractRoleXref xref : compiledRoles) {
				final Set<AbstractResourceXref> resources = xref.getRole().getResources();
				if(resources != null) {
					tempCompiledSet.addAll(resources);
				}
			}
		}
		
		if(CollectionUtils.isNotEmpty(compiledOrgs)) {
			for(final AbstractOrgXref xref : compiledOrgs) {
				final Set<AbstractResourceXref> orgEntities = xref.getOrganization().getResources();
				if(orgEntities != null) {
					tempCompiledSet.addAll(orgEntities);
				}
			}
		}

		final Set<AbstractResourceXref> compiledSet = new HashSet<AbstractResourceXref>();
		final Set<AuthorizationResource> visitedSet = new HashSet<AuthorizationResource>();
		for(final AbstractResourceXref xref : tempCompiledSet) {
			final Set<AbstractResourceXref> justVisited = xref.getResource().visitResources(visitedSet);
			compiledSet.addAll(justVisited);
			visitedSet.addAll(justVisited.stream().map(e -> e.getResource()).collect(Collectors.toSet()));
		}
		compiledSet.addAll(tempCompiledSet);
		return compiledSet;
	}

	public boolean isEntitledTo(final AuthorizationResource entity, final int numOfRights) {
		return (entity != null) ? linearResourceBitSet.get(getBitIndex(null, entity, numOfRights)) : false;
	}
	
	public boolean isEntitledTo(final AuthorizationResource entity, final AuthorizationAccessRight right, final int numOfRights) {
		if(right == null) {
			return isEntitledTo(entity, numOfRights);
		} else {
			return linearResourceBitSet.get(getBitIndex(right, entity, numOfRights));
		}
	}
	
	public boolean isMemberOf(final AuthorizationGroup entity, final int numOfRights) {
		return (entity != null) ? linearGroupBitSet.get(getBitIndex(null, entity, numOfRights)) : false;
	}
	
	public boolean isMemberOf(final AuthorizationGroup entity, final AuthorizationAccessRight right, final int numOfRights) {
		if(right == null) {
			return isMemberOf(entity, numOfRights);
		} else {
			return linearGroupBitSet.get(getBitIndex(right, entity, numOfRights));
		}
	}
	
	public boolean isMemberOf(final AuthorizationRole entity, final int numOfRights) {
		return (entity != null) ? linearRoleBitSet.get(getBitIndex(null, entity, numOfRights)) : false;
	}
	
	public boolean isMemberOf(final AuthorizationRole entity, final AuthorizationAccessRight right, final int numOfRights) {
		if(right == null) {
			return isMemberOf(entity, numOfRights);
		} else {
			return linearRoleBitSet.get(getBitIndex(right, entity, numOfRights));
		}
	}
	
	public boolean isMemberOf(final AuthorizationOrganization entity, final int numOfRights) {
		return (entity != null) ? linearOrganizationBitSet.get(getBitIndex(null, entity, numOfRights)) : false;
	}
	
	public boolean isMemberOf(final AuthorizationOrganization entity, final AuthorizationAccessRight right, final int numOfRights) {
		if(right == null) {
			return isMemberOf(entity, numOfRights);
		} else {
			return linearOrganizationBitSet.get(getBitIndex(right, entity, numOfRights));
		}
	}
	
	public Set<Integer> getLinearOrganizationSet() {
		final Set<Integer> linearBitSet = new HashSet<Integer>();
		for(int i = 0; i < linearOrganizationBitSet.size(); i++) {
			if(linearOrganizationBitSet.get(i)) {
				linearBitSet.add(new Integer(i));
			}
		}
		return linearBitSet;
	}
	
	public List<Integer> getLinearRoles() {
		final List<Integer> linearBitSet = new LinkedList<Integer>();
		for(int i = 0; i < linearRoleBitSet.size(); i++) {
			if(linearRoleBitSet.get(i)) {
				linearBitSet.add(new Integer(i));
			}
		}
		return linearBitSet;
	}
	
	public List<Integer> getLinearGroups() {
		final List<Integer> linearBitSet = new LinkedList<Integer>();
		for(int i = 0; i < linearGroupBitSet.size(); i++) {
			if(linearGroupBitSet.get(i)) {
				linearBitSet.add(new Integer(i));
			}
		}
		return linearBitSet;
	}
	
	public List<Integer> getLinearResources() {
		final List<Integer> linearBitSet = new LinkedList<Integer>();
		for(int i = 0; i < linearResourceBitSet.size(); i++) {
			if(linearResourceBitSet.get(i)) {
				linearBitSet.add(new Integer(i));
			}
		}
		return linearBitSet;
	}
	
	public List<Integer> getLinearOrganizations() {
		final List<Integer> linearBitSet = new LinkedList<Integer>();
		for(int i = 0; i < linearOrganizationBitSet.size(); i++) {
			if(linearOrganizationBitSet.get(i)) {
				linearBitSet.add(new Integer(i));
			}
		}
		return linearBitSet;
	}
}
