package org.openiam.am.srvc.dto.jdbc;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InternalAuthroizationUser {

	private String userId;
	private Map<String, Set<InternalAuthorizationToken>> resources = new HashMap<String, Set<InternalAuthorizationToken>>();
	private Map<String, Set<InternalAuthorizationToken>> roles = new HashMap<String, Set<InternalAuthorizationToken>>();
	private Map<String, Set<InternalAuthorizationToken>> groups = new HashMap<String, Set<InternalAuthorizationToken>>();
	private Map<String, Set<InternalAuthorizationToken>> organizations = new HashMap<String, Set<InternalAuthorizationToken>>();
	
	public InternalAuthroizationUser() {
		
	}
	
	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public Map<String, Set<InternalAuthorizationToken>> getResources() {
		return resources;
	}

	public void setResources(Map<String, Set<InternalAuthorizationToken>> resources) {
		this.resources = resources;
	}

	public Map<String, Set<InternalAuthorizationToken>> getRoles() {
		return roles;
	}

	public void setRoles(Map<String, Set<InternalAuthorizationToken>> roles) {
		this.roles = roles;
	}

	public Map<String, Set<InternalAuthorizationToken>> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, Set<InternalAuthorizationToken>> groups) {
		this.groups = groups;
	}

	public Map<String, Set<InternalAuthorizationToken>> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Map<String, Set<InternalAuthorizationToken>> organizations) {
		this.organizations = organizations;
	}

	public void addResourceRight(final String entityId, final String rightId, final Date startDate, final Date endDate) {
		add(resources, entityId, rightId, startDate, endDate);
	}
	
	public void addGroupRight(final String entityId, final String rightId, final Date startDate, final Date endDate) {
		add(groups, entityId, rightId, startDate, endDate);
	}
	
	public void addRoleRight(final String entityId, final String rightId, final Date startDate, final Date endDate) {
		add(roles, entityId, rightId, startDate, endDate);
	}
	
	public void addOrganizationRight(final String entityId, final String rightId, final Date startDate, final Date endDate) {
		add(organizations, entityId, rightId, startDate, endDate);
	}
	
	private void add(final Map<String, Set<InternalAuthorizationToken>> map, final String entityId, final String rightId, final Date startDate, final Date endDate) {
		if(entityId != null) {
			if(!map.containsKey(entityId)) {
				map.put(entityId, new HashSet<InternalAuthorizationToken>());
			}
			if(rightId != null) {
				map.get(entityId).add(new InternalAuthorizationToken(rightId, startDate, endDate));
			}
		}
	}
}
