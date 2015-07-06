package org.openiam.authmanager.common.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import javax.management.relation.RoleResult;

import org.apache.commons.lang.RandomStringUtils;
import org.openiam.authmanager.common.xref.GroupGroupXref;
import org.openiam.authmanager.common.xref.GroupUserXref;
import org.openiam.authmanager.common.xref.OrgGroupXref;
import org.openiam.authmanager.common.xref.OrgOrgXref;
import org.openiam.authmanager.common.xref.OrgResourceXref;
import org.openiam.authmanager.common.xref.OrgRoleXref;
import org.openiam.authmanager.common.xref.OrgUserXref;
import org.openiam.authmanager.common.xref.ResourceGroupXref;
import org.openiam.authmanager.common.xref.ResourceResourceXref;
import org.openiam.authmanager.common.xref.ResourceRoleXref;
import org.openiam.authmanager.common.xref.ResourceUserXref;
import org.openiam.authmanager.common.xref.RoleGroupXref;
import org.openiam.authmanager.common.xref.RoleRoleXref;
import org.openiam.authmanager.common.xref.RoleUserXref;
import org.springframework.util.StopWatch;
import org.testng.annotations.Test;

public class TestAuthorizationUser {

	private static final int LOAD_COEFFICIENT = 1;
	private static final int NUM_OF_ORGS = 1000;
	private static final int NUM_OF_ROLES = 1000;
	private static final int NUM_OF_GROUPS = 1000;
	private static final int NUM_OF_RESOURCES = 1000;
	
	/**
	 * tests that comilation doesn't take too long
	 */
	@Test
	public void testCompileTime() {
		final AuthorizationUser user = new AuthorizationUser();
		
		final Map<String, AuthorizationOrganization> orgMap = new HashMap<String, AuthorizationOrganization>();
		final Map<String, AuthorizationRole> roleMap = new HashMap<String, AuthorizationRole>();
		final Map<String, AuthorizationGroup> groupMap = new HashMap<String, AuthorizationGroup>();
		final Map<String, AuthorizationResource> resourceMap = new HashMap<String, AuthorizationResource>();
		
		for(int i = 0; i < NUM_OF_ORGS; i++) {
			final AuthorizationOrganization e = new AuthorizationOrganization();
			e.setId(i + "");
			e.setBitSetIdx(i);
			orgMap.put(e.getId(), e);
		}
		
		for(int i = 0; i < NUM_OF_ROLES; i++) {
			final AuthorizationRole e = new AuthorizationRole();
			e.setId(i + "");
			e.setBitSetIdx(i);
			roleMap.put(e.getId(), e);
		}
		
		for(int i = 0; i < NUM_OF_GROUPS; i++) {
			final AuthorizationGroup e = new AuthorizationGroup();
			e.setId(i + "");
			e.setBitSetIdx(i);
			groupMap.put(e.getId(), e);
		}
		
		for(int i = 0; i < NUM_OF_RESOURCES; i++) {
			final AuthorizationResource e = new AuthorizationResource();
			e.setId(i + "");
			e.setBitSetIdx(i);
			resourceMap.put(e.getId(), e);
		}
		
		for(int i = 0; i < NUM_OF_ORGS; i++) {
			final AuthorizationOrganization e = orgMap.get("" + i);
			final OrgUserXref xref = new OrgUserXref();
			xref.setUser(user);
			xref.setOrganization(e);
			user.addOrganization(xref);
			i *= LOAD_COEFFICIENT;
		}
		
		for(int i = 0; i < NUM_OF_ROLES; i++) {
			final AuthorizationRole e = roleMap.get("" + i);
			final RoleUserXref xref = new RoleUserXref();
			xref.setUser(user);
			xref.setRole(e);
			user.addRole(xref);
			i *= LOAD_COEFFICIENT;
		}
		
		for(int i = 0; i < NUM_OF_GROUPS; i++) {
			final AuthorizationGroup e = groupMap.get("" + i);
			final GroupUserXref xref = new GroupUserXref();
			xref.setUser(user);
			xref.setGroup(e);
			user.addGroup(xref);
			i *= LOAD_COEFFICIENT;
		}
		
		for(int i = 0; i < NUM_OF_RESOURCES; i++) {
			final AuthorizationResource e = resourceMap.get("" + i);
			final ResourceUserXref xref = new ResourceUserXref();
			xref.setUser(user);
			xref.setResource(e);
			user.addResource(xref);
			i *= LOAD_COEFFICIENT;
		}
		
		for(int i = 0; i < NUM_OF_ORGS; i++) {
			final AuthorizationOrganization org = orgMap.get("" + i);
			for(int j = 0; j < NUM_OF_ORGS; j++) {
				final OrgOrgXref xref = new OrgOrgXref();
				xref.setOrganization(orgMap.get("" + j));
				xref.setMemberOrganization(org);
				org.addParentOrganization(xref);
				j *= 20;
			}
			
			for(int j = 0; j < NUM_OF_ROLES; j++) {
				final OrgRoleXref xref = new OrgRoleXref();
				xref.setOrganization(org);
				xref.setRole(roleMap.get("" + j));
				org.addRole(xref);
				j *= 20;
			}
			
			for(int j = 0; j < NUM_OF_GROUPS; j++) {
				final OrgGroupXref xref = new OrgGroupXref();
				xref.setOrganization(org);
				xref.setGroup(groupMap.get("" + j));
				org.addGroup(xref);
				j *= 20;
			}
			
			for(int j = 0; j < NUM_OF_RESOURCES; j++) {
				final OrgResourceXref xref = new OrgResourceXref();
				xref.setOrganization(org);
				xref.setResource(resourceMap.get("" + j));
				org.addResource(xref);
				j *= 20;
			}
			
			i *= LOAD_COEFFICIENT;
		}
		
		for(int i = 0; i < NUM_OF_ROLES; i++) {
			final AuthorizationRole role = roleMap.get("" + i);
			
			for(int j = 0; j < NUM_OF_ROLES; j++) {
				final RoleRoleXref xref = new RoleRoleXref();
				xref.setMemberRole(role);
				xref.setRole(roleMap.get("" + j));
				role.addParentRole(xref);
				j *= 20;
			}
			
			for(int j = 0; j < NUM_OF_GROUPS; j++) {
				final RoleGroupXref xref = new RoleGroupXref();
				xref.setGroup(groupMap.get("" + j));
				xref.setRole(role);
				role.addGroup(xref);
				j *= 20;
			}
			
			for(int j = 0; j < NUM_OF_RESOURCES; j++) {
				final ResourceRoleXref xref = new ResourceRoleXref();
				xref.setResource(resourceMap.get("" + j));
				xref.setRole(role);
				role.addResource(xref);
				j *= 20;
			}
			
			i *= LOAD_COEFFICIENT;
		}
		
		for(int i = 0; i < NUM_OF_GROUPS; i++) {
			final AuthorizationGroup group = groupMap.get("" + i);
			for(int j = 0; j < NUM_OF_GROUPS; j++) {
				final GroupGroupXref xref = new GroupGroupXref();
				xref.setMemberGroup(group);
				xref.setGroup(groupMap.get("" + j));
				group.addParentGroup(xref);
				j *= 20;
			}
			
			for(int j = 0; j < NUM_OF_RESOURCES; j++) {
				final ResourceGroupXref xref = new ResourceGroupXref();
				xref.setGroup(group);
				xref.setResource(resourceMap.get("" + j));
				group.addResource(xref);
				j *= 20;
			}
			
			i *= LOAD_COEFFICIENT;
		}
		for(int i = 0; i < NUM_OF_RESOURCES; i++) {
			final AuthorizationResource resource = resourceMap.get("" + i);
			for(int j = 0; j < NUM_OF_RESOURCES; j++) {
				final ResourceResourceXref xref = new ResourceResourceXref();
				xref.setMemberResource(resource);
				xref.setResource(resourceMap.get("" + j));
				resource.addParentResoruce(xref);
				j *= 20;
			}
			
			i *= LOAD_COEFFICIENT;
		}
		
		final StopWatch sw = new StopWatch();
		sw.start();
		user.compile();
		sw.stop();
		System.out.println(sw.getTotalTimeMillis());
	}
}
