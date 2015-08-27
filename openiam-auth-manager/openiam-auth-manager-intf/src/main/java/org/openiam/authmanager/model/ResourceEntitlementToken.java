package org.openiam.authmanager.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openiam.authmanager.common.model.AuthorizationAccessRight;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.idm.srvc.access.dto.AccessRight;

public class ResourceEntitlementToken {

	/* key is the resource id, value is a set of right ids */
	private Map<AuthorizationResource, Set<AuthorizationAccessRight>> directResources;
	private Map<AuthorizationResource, Set<AuthorizationAccessRight>> allResources;
	private Map<AuthorizationResource, Set<AuthorizationAccessRight>> indirectResources;
	public Map<AuthorizationResource, Set<AuthorizationAccessRight>> getDirectResources() {
		return directResources;
	}
	public void setDirectResources(
			Map<AuthorizationResource, Set<AuthorizationAccessRight>> directResources) {
		this.directResources = directResources;
	}
	public Map<AuthorizationResource, Set<AuthorizationAccessRight>> getAllResources() {
		return allResources;
	}
	public void setAllResources(
			Map<AuthorizationResource, Set<AuthorizationAccessRight>> allResources) {
		this.allResources = allResources;
	}
	
	public void addDirectResource(final AuthorizationResource resource, final AuthorizationAccessRight right) {
		if(resource != null) {
			if(directResources == null) {
				directResources = new HashMap<AuthorizationResource, Set<AuthorizationAccessRight>>();
			}
			if(!directResources.containsKey(resource)) {
				directResources.put(resource, new HashSet<AuthorizationAccessRight>());
			}
			if(right != null) {
				directResources.get(resource).add(right);
			}
		}
	}
	
	public void addResource(final AuthorizationResource resource, final AuthorizationAccessRight right) {
		if(resource != null) {
			if(allResources == null) {
				allResources = new HashMap<AuthorizationResource, Set<AuthorizationAccessRight>>();
			}
			if(!allResources.containsKey(resource)) {
				allResources.put(resource, new HashSet<AuthorizationAccessRight>());
			}
			if(right != null) {
				allResources.get(resource).add(right);
			}
		}
	}
	
	public void addIndirectResource(final AuthorizationResource resource, final AuthorizationAccessRight right) {
		if(resource != null) {
			if(indirectResources == null) {
				indirectResources = new HashMap<AuthorizationResource, Set<AuthorizationAccessRight>>();
			}
			if(!indirectResources.containsKey(resource)) {
				indirectResources.put(resource, new HashSet<AuthorizationAccessRight>());
			}
			if(right != null) {
				indirectResources.get(resource).add(right);
			}
		}
	}
	
	public boolean isResourceDirect(final String id) {
		return (directResources != null) ? directResources.keySet().stream().map(e -> e.getId()).filter(e -> e.equals(id)).findFirst().isPresent() : false;
	}
	
	public boolean isResourceIndirect(final String id) {
		return (indirectResources != null) ? indirectResources.keySet().stream().map(e -> e.getId()).filter(e -> e.equals(id)).findFirst().isPresent() : false;
	}
	public Map<AuthorizationResource, Set<AuthorizationAccessRight>> getIndirectResources() {
		return indirectResources;
	}
	public void setIndirectResources(
			Map<AuthorizationResource, Set<AuthorizationAccessRight>> indirectResources) {
		this.indirectResources = indirectResources;
	}
	
	
}
