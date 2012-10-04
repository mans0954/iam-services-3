package org.openiam.authmanager.common.model;

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuthorizationResource extends AbstractEntity implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	private Set<AuthorizationResource> directParentResources;
	private String name;
	
	/*
	private BitSet linearBitSet = new BitSet();
	*/
	
	public AuthorizationResource() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addParentResoruce(final AuthorizationResource resource) {
		if(directParentResources == null) {
			directParentResources = new HashSet<AuthorizationResource>();
		}
		directParentResources.add(resource);
	}
	
	/**
	 * Compiles this Resource against it's Resource Membership
	 */
	@Override
	public void compile() {
		/*
		final Set<Resource> compiledSet = visitResources(new HashSet<Resource>());
		for(final Resource resource : compiledSet) {
			linearBitSet.set(new Integer(resource.getBitSetIdx()));
		}
		*/
	}
	
	public Set<AuthorizationResource> visitResources(final Set<AuthorizationResource> visitedSet) {
		final Set<AuthorizationResource> compiledResourceBitSet = new HashSet<AuthorizationResource>();
		if(!visitedSet.contains(this)) {
			visitedSet.add(this);
			if(directParentResources != null) {
				for(final AuthorizationResource parent : directParentResources) {
					compiledResourceBitSet.add(parent);
					compiledResourceBitSet.addAll(parent.visitResources(visitedSet));
				}
			}
		}
		return compiledResourceBitSet;
	}
}
