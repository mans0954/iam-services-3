package org.openiam.authmanager.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openiam.authmanager.common.model.AuthorizationResource;

public class ResourceEntitlementToken {

	private Set<AuthorizationResource> directResources;
	private Set<AuthorizationResource> indirectResources;
	
	public boolean isResourceDirect(final String resourceId) {
		return containsResource(directResources, resourceId);
	}
	
	public boolean isResourceIndirect(final String resourceId) {
		return containsResource(indirectResources, resourceId);
	}
	
	private boolean containsResource(final Set<AuthorizationResource> resourceSet, final String resourceId) {
		boolean retVal = false;
		if(resourceSet != null && resourceId != null) {
			for(final Iterator<AuthorizationResource> it = resourceSet.iterator(); it.hasNext();) {
				final AuthorizationResource resource = it.next();
				if(resource != null) {
					retVal = StringUtils.equals(resource.getId(), resourceId);
					if(retVal) {
						break;
					}
				}
			}
		}
		return retVal;
	}
	
	public void addDirectResource(final AuthorizationResource resource) {
		if(resource != null) {
			if(directResources == null) {
				directResources = new HashSet<AuthorizationResource>();
			}
			directResources.add(resource);
		}
	}
	
	public void addDirectResources(final Collection<AuthorizationResource> resources) {
		if(resources != null) {
			if(directResources == null) {
				directResources = new HashSet<AuthorizationResource>();
			}
			directResources.addAll(resources);
		}
	}
	
	public void addIndirectResource(final AuthorizationResource resource) {
		if(resource != null) {
			if(indirectResources == null) {
				indirectResources = new HashSet<AuthorizationResource>();
			}
			indirectResources.add(resource);
		}
	}
	
	public void addIndirectResource(final Collection<AuthorizationResource> resources) {
		if(resources != null) {
			if(indirectResources == null) {
				indirectResources = new HashSet<AuthorizationResource>();
			}
			indirectResources.addAll(resources);
		}
	}
}
