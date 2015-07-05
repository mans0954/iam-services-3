package org.openiam.authmanager.common.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InternalAuthroizationUser {

	private String userId;
	private Map<String, Set<String>> resources = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> roles = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> groups = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> organizations = new HashMap<String, Set<String>>();
	
	public InternalAuthroizationUser() {
		
	}
	
	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public Map<String, Set<String>> getResources() {
		return resources;
	}

	public void setResources(Map<String, Set<String>> resources) {
		this.resources = resources;
	}

	public Map<String, Set<String>> getRoles() {
		return roles;
	}

	public void setRoles(Map<String, Set<String>> roles) {
		this.roles = roles;
	}

	public Map<String, Set<String>> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, Set<String>> groups) {
		this.groups = groups;
	}

	public Map<String, Set<String>> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Map<String, Set<String>> organizations) {
		this.organizations = organizations;
	}

	public void addResourceRight(final String entityId, final String rightId) {
		add(resources, entityId, rightId);
	}
	
	public void addGroupRight(final String entityId, final String rightId) {
		add(groups, entityId, rightId);
	}
	
	public void addRoleRight(final String entityId, final String rightId) {
		add(roles, entityId, rightId);
	}
	
	public void addOrganizationRight(final String entityId, final String rightId) {
		add(organizations, entityId, rightId);
	}
	
	private void add(final Map<String, Set<String>> map, final String entityId, final String rightId) {
		if(entityId != null) {
			if(!map.containsKey(entityId)) {
				map.put(entityId, new HashSet<String>());
			}
			if(rightId != null) {
				map.get(entityId).add(rightId);
			}
		}
	}
}
