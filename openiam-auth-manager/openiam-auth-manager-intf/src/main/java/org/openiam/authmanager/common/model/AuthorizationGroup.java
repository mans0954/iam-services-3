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
import org.openiam.authmanager.common.xref.GroupGroupXref;
import org.openiam.authmanager.common.xref.ResourceGroupXref;
import org.openiam.idm.srvc.grp.domain.GroupEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationGroup", propOrder = {
})
public class AuthorizationGroup extends AbstractAuthorizationEntity implements Serializable  {

	private static final long serialVersionUID = 1L;

	private String adminResourceId;
	
	@XmlTransient
	private Set<GroupGroupXref> parentGroups = null;
	
	@XmlTransient
	private Set<ResourceGroupXref> resources = null;
//

	/*
	private BitSet linearGroupBitSet = new BitSet();
	private BitSet linearRoleBitSet = new BitSet();
	private BitSet linearResourceBitSet = new BitSet();
	*/
	
	private AuthorizationGroup() {
		
	}
	
	public AuthorizationGroup(final GroupEntity entity, final int bitIdx) {
		super.setBitSetIdx(bitIdx);
		super.setDescription(entity.getDescription());
		super.setId(entity.getId());
		super.setName(entity.getName());
		super.setStatus(entity.getStatus());
		this.adminResourceId = (entity.getAdminResource() != null) ? entity.getAdminResource().getId() : null;
	}

	public String getAdminResourceId() {
		return adminResourceId;
	}

	public void setAdminResourceId(String adminResourceId) {
		this.adminResourceId = adminResourceId;
	}

	public void addParentGroup(final GroupGroupXref group) {
		if(parentGroups == null) {
			parentGroups = new HashSet<GroupGroupXref>();
		}
		parentGroups.add(group);
	}
	
	public void addResource(final ResourceGroupXref resource) {
		if(resources == null) {
			resources = new HashSet<ResourceGroupXref>();
		}
		resources.add(resource);
	}
	
	public Set<AbstractResourceXref> getResources() {
		Set<AbstractResourceXref> retVal = null;
		if(resources != null) {
			retVal = new HashSet<AbstractResourceXref>(resources);
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
	
	public Set<AbstractGroupXref> visitGroups(final Set<AuthorizationGroup> visitedSet) {
		final Set<AbstractGroupXref> compiledGroupSet = new HashSet<AbstractGroupXref>();
		if(!visitedSet.contains(this)) {
			if(parentGroups != null) {
				visitedSet.add(this);
				for(final AbstractGroupXref xref : parentGroups) {
					compiledGroupSet.add(xref);
					compiledGroupSet.addAll(xref.getGroup().visitGroups(visitedSet));
				}
			}
		}
		return compiledGroupSet;
	}
	
	public Set<AbstractResourceXref> visitResources(final Set<AuthorizationGroup> visitedSet) {
		final Set<AbstractResourceXref> compiledResourceSet = new HashSet<AbstractResourceXref>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(parentGroups != null) {
				for(final AbstractGroupXref xref : parentGroups) {
					compiledResourceSet.addAll(xref.getGroup().visitResources(visitedSet));
				}
			}
			
			if(resources != null) {
				for(final AbstractResourceXref xref : resources) {
					compiledResourceSet.add(xref);
					compiledResourceSet.addAll(xref.getResource().visitResources(new HashSet<AuthorizationResource>()));
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
