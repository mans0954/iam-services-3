package org.openiam.am.srvc.dto.jdbc;

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dto.jdbc.xref.AbstractGroupXref;
import org.openiam.am.srvc.dto.jdbc.xref.AbstractOrgXref;
import org.openiam.am.srvc.dto.jdbc.xref.AbstractResourceXref;
import org.openiam.am.srvc.dto.jdbc.xref.AbstractRoleXref;
import org.openiam.am.srvc.dto.jdbc.xref.OrgGroupXref;
import org.openiam.am.srvc.dto.jdbc.xref.OrgOrgXref;
import org.openiam.am.srvc.dto.jdbc.xref.OrgResourceXref;
import org.openiam.am.srvc.dto.jdbc.xref.OrgRoleXref;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationOrganization", propOrder = {
})
public class AuthorizationOrganization extends AbstractAuthorizationEntity implements Serializable  {
	
	private static final Log log = LogFactory.getLog(AuthorizationOrganization.class);

	
	@XmlTransient
	private Set<OrgOrgXref> parentOrganizations = null;
	
	@XmlTransient
	private Set<OrgRoleXref> roles = null;
	
	@XmlTransient
	private Set<OrgResourceXref> resources = null;
	
	@XmlTransient
	private Set<OrgGroupXref> groups = null;
	
	private BitSet linearGroupBitSet = null;
	private BitSet linearResourceBitSet = null;
	private BitSet linearRoleBitSet = null;
	private BitSet linearOrgBitSet = null;
	
	public AuthorizationOrganization() {}
	
	public AuthorizationOrganization(final AuthorizationOrganization entity, final int bitIdx) {
		super(entity);
		super.setBitSetIdx(bitIdx);
	}
	
	public Set<AbstractResourceXref> getResources() {
		Set<AbstractResourceXref> retval = null;
		if(resources != null) {
			retval = new HashSet<AbstractResourceXref>(resources);
		}
		return retval;
	}
	
	public Set<AbstractGroupXref> getGroups() {
		Set<AbstractGroupXref> retval = null;
		if(groups != null) {
			retval = new HashSet<AbstractGroupXref>(groups);
		}
		return retval;
	}
	
	public Set<AbstractRoleXref> getRoles() {
		Set<AbstractRoleXref> retval = null;
		if(roles != null) {
			retval = new HashSet<AbstractRoleXref>(roles);
		}
		return retval;
	}
	
	public void addParentOrganization(final OrgOrgXref organization) {
		if(parentOrganizations == null) {
			parentOrganizations = new HashSet<OrgOrgXref>();
		}
		parentOrganizations.add(organization);
	}
	
	public void addRole(final OrgRoleXref role) {
		if(roles == null) {
			roles = new HashSet<OrgRoleXref>();
		}
		roles.add(role);
	}
	
	public void addGroup(final OrgGroupXref group) {
		if(groups == null) {
			groups = new HashSet<OrgGroupXref>();
		}
		groups.add(group);
	}
	
	public void addResource(final OrgResourceXref resource) {
		if(resources == null) {
			resources = new HashSet<OrgResourceXref>();
		}
		resources.add(resource);
	}

	public AuthorizationOrganization shallowCopy() {
		final AuthorizationOrganization copy = new AuthorizationOrganization();
		super.makeCopy(copy);
		return copy;
	}
	
	public Set<AbstractOrgXref> visitOrganizations(final Set<AuthorizationOrganization> visitedSet) {
		final Set<AbstractOrgXref> compiledList = new HashSet<AbstractOrgXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(parentOrganizations != null) {
				for(final AbstractOrgXref xref : parentOrganizations) {
					compiledList.add(xref);
					compiledList.addAll(xref.getOrganization().visitOrganizations(visitedSet));
				}
			}
		}
		return compiledList;
	}
	
	public Set<AbstractRoleXref> visitRoles(final Set<AuthorizationOrganization> visitedSet) {
		final Set<AbstractRoleXref> compiledList = new HashSet<AbstractRoleXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			
			if(parentOrganizations != null) {
				parentOrganizations.forEach(xref -> {
					compiledList.addAll(xref.getOrganization().visitRoles(visitedSet));
				});
			}
			
			if(roles != null) {
				roles.forEach(xref -> {
					compiledList.add(xref);
					compiledList.addAll(xref.getRole().visitRoles(new HashSet<AuthorizationRole>()));
				});
			}
		}
		return compiledList;
	}
	
	public Set<AbstractGroupXref> visitGroups(final Set<AuthorizationOrganization> visitedSet) {
		final Set<AbstractGroupXref> compiledList = new HashSet<AbstractGroupXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			
			if(parentOrganizations != null) {
				parentOrganizations.forEach(xref -> {
					compiledList.addAll(xref.getOrganization().visitGroups(visitedSet));
				});
			}
			
			if(roles != null) {
				roles.forEach(xref -> {
					compiledList.addAll(xref.getRole().visitGroups(new HashSet<AuthorizationRole>()));
				});
			}
			
			if(groups != null) {
				groups.forEach(xref -> {
					compiledList.add(xref);
					compiledList.addAll(xref.getGroup().visitGroups(new HashSet<AuthorizationGroup>()));
				});
			}
		}
		return compiledList;
	}
	
	public Set<AbstractResourceXref> visitResources(final Set<AuthorizationOrganization> visitedSet) {
		final Set<AbstractResourceXref> compiledList = new HashSet<AbstractResourceXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			
			if(parentOrganizations != null) {
				parentOrganizations.forEach(xref -> {
					compiledList.addAll(xref.getOrganization().visitResources(visitedSet));
				});
			}
			
			if(roles != null) {
				roles.forEach(xref -> {
					compiledList.addAll(xref.getRole().visitResources(new HashSet<AuthorizationRole>()));
				});
			}
			
			if(groups != null) {
				groups.forEach(xref -> {
					compiledList.addAll(xref.getGroup().visitResources(new HashSet<AuthorizationGroup>()));
				});
			}
			
			if(resources != null) {
				resources.forEach(xref -> {
					compiledList.add(xref);
					compiledList.addAll(xref.getResource().visitResources(new HashSet<AuthorizationResource>()));
				});
			}
		}
		return compiledList;
	}
	
	public boolean hasResource(final String id) {
		return (resources != null) ? resources.stream().map(e -> e.getResource().getId()).filter(e -> e.equals(id)).findFirst().isPresent() : false;
	}

	private Set<AbstractOrgXref> visitOrganization(final Set<AuthorizationOrganization> visitedSet) {
		final Set<AbstractOrgXref> compiledRoles = new HashSet<AbstractOrgXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(parentOrganizations != null) {
				for(final AbstractOrgXref xref : parentOrganizations) {
					compiledRoles.add(xref);
					compiledRoles.addAll(xref.getOrganization().visitOrganization(visitedSet));
				}
			}
		}
		return compiledRoles;
	}
	
	private Set<AbstractRoleXref> visitRolesInternal(final Set<AbstractOrgXref> compiledOrgs) {
		final Set<AbstractRoleXref> tempCompiledSet = new HashSet<AbstractRoleXref>();
		if(roles != null) {
			tempCompiledSet.addAll(roles);
		}
		
		if(CollectionUtils.isNotEmpty(compiledOrgs)) {
			compiledOrgs.forEach(xref -> {
			//for(final AbstractOrgXref xref : compiledOrgs) {
				final Set<AbstractRoleXref> orgEntities = xref.getOrganization().getRoles();
				if(orgEntities != null) {
					tempCompiledSet.addAll(orgEntities);
				}
			});
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
		if(groups != null) {
			tempCompiledSet.addAll(groups);
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
		if(resources != null) {
			tempCompiledSet.addAll(resources);
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
	
	/**
	 * Compiles this User against it's Role, Group, Organization and Resource Membership
	 */
	public void compile(final int numOfRights) {
		
		linearGroupBitSet = new BitSet();
		linearResourceBitSet = new BitSet();
		linearRoleBitSet = new BitSet();
		linearOrgBitSet = new BitSet();
		final StopWatch sw = new StopWatch();
		sw.start();
		final StringBuilder sb = (log.isDebugEnabled()) ? new StringBuilder(String.format("User ID: %s", getId())) : null;
		
		final StopWatch innerSW = new StopWatch();
		innerSW.start();
		
		final Set<AbstractOrgXref> compiledOrgSet = visitOrganization(new HashSet<AuthorizationOrganization>());
		for(final AbstractOrgXref xref : compiledOrgSet) {
			if(CollectionUtils.isNotEmpty(xref.getRights())) {
				xref.getRights().forEach(right -> {
					linearOrgBitSet.set(getBitIndex(right, xref.getOrganization(), numOfRights));
				});
			}
			linearOrgBitSet.set(getBitIndex(null, xref.getOrganization(), numOfRights));
		}
		innerSW.stop();
		if(log.isDebugEnabled()) {
			sb.append(String.format("Compiled Orgs: %s.  ", innerSW.getTime()));
		}
		innerSW.reset();
		innerSW.start();
		
		final Set<AbstractRoleXref> compiledRoleSet = visitRolesInternal(compiledOrgSet);
		for(final AbstractRoleXref xref : compiledRoleSet) {
			if(CollectionUtils.isNotEmpty(xref.getRights())) {
				xref.getRights().forEach(right -> {
					linearRoleBitSet.set(getBitIndex(right, xref.getRole(), numOfRights));
				});
			}
			linearRoleBitSet.set(getBitIndex(null, xref.getRole(), numOfRights));
		}
		innerSW.stop();
		if(log.isDebugEnabled()) {
			sb.append(String.format("Compiled Roles: %s.  ", innerSW.getTime()));
		}
		innerSW.reset();
		innerSW.start();

		final Set<AbstractGroupXref> compiledGroupSet = visitGroups(compiledOrgSet, compiledRoleSet);
		for(final AbstractGroupXref xref : compiledGroupSet) {
			if(CollectionUtils.isNotEmpty(xref.getRights())) {
				xref.getRights().forEach(right -> {
					linearGroupBitSet.set(getBitIndex(right, xref.getGroup(), numOfRights));
				});
			}
			linearGroupBitSet.set(getBitIndex(null, xref.getGroup(), numOfRights));
		}
		innerSW.stop();
		if(log.isDebugEnabled()) {
			sb.append(String.format("Compiled Groups: %s.  ", innerSW.getTime()));
		}
		innerSW.reset();
		innerSW.start();
		
		final Set<AbstractResourceXref> compiledResourceSet = visitResources(compiledOrgSet, compiledGroupSet, compiledRoleSet);
		for(final AbstractResourceXref xref : compiledResourceSet) {
			if(CollectionUtils.isNotEmpty(xref.getRights())) {
				xref.getRights().forEach(right -> {
					linearResourceBitSet.set(getBitIndex(right, xref.getResource(), numOfRights));
				});
			}
			linearResourceBitSet.set(getBitIndex(null, xref.getResource(), numOfRights));
		}
		innerSW.stop();
		if(log.isDebugEnabled()) {
			sb.append(String.format("Compiled Resources: %s.  ", innerSW.getTime()));
		}
		sw.stop();
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
}
