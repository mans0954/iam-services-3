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
import org.openiam.base.KeyDTO;
import org.openiam.idm.srvc.grp.domain.GroupEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationGroup", propOrder = {
})
public class AuthorizationGroup extends AbstractAuthorizationEntity implements Serializable  {

	private static final long serialVersionUID = 1L;
	
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
	
	public AuthorizationGroup() {
		
	}
	
	public AuthorizationGroup(final AuthorizationGroup dto, final int bitIdx) {
		super(dto);
		super.setBitSetIdx(bitIdx);
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
