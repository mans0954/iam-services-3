package org.openiam.authmanager.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openiam.authmanager.common.model.AuthorizationAccessRight;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.membership.MembershipDTO;

public class AuthorizationManagerDataModel {
	
	final AtomicInteger tempGroupBitSet = new AtomicInteger(0);
	final AtomicInteger tempRoleBitSet = new AtomicInteger(0);
	final AtomicInteger tempResourceBitSet = new AtomicInteger(0);
	final AtomicInteger tempUserBitSet = new AtomicInteger(0);
	final AtomicInteger tempOrgBitSet = new AtomicInteger(0);
	final AtomicInteger tempAccessRightBitSet = new AtomicInteger(1);
	
	private List<AuthorizationResource> hbmResourceList;
	private Map<String, Set<MembershipDTO>> resource2ResourceMap;
	private Map<String, Set<String>> resource2ResourceRightMap;
	
	private Map<String, Set<MembershipDTO>> user2ResourceMap;
	private Map<String, Set<String>> user2ResourceRightMap;
	
	/* groups */
	private List<AuthorizationGroup> hbmGroupList;
	private Map<String, Set<MembershipDTO>> group2GroupMap;
	private Map<String, Set<String>> group2GroupRightMap;
	
	private Map<String, Set<MembershipDTO>> group2UserMap;
	private Map<String, Set<String>> group2UserRightMap;
	
	
	private Map<String, Set<MembershipDTO>> group2ResourceMap;
	private Map<String, Set<String>> group2ResourceRightMap;
	
	private List<AuthorizationRole> hbmRoleList;
	private Map<String, Set<MembershipDTO>> role2RoleMap;
	private Map<String, Set<String>> role2RoleRightMap;
	
	private Map<String, Set<MembershipDTO>> role2GroupMap;
	private Map<String, Set<String>> role2GroupRightMap;
	
	private Map<String, Set<MembershipDTO>> role2ResourceMap;
	private Map<String, Set<String>> role2ResourceRightMap;
	
	private Map<String, Set<MembershipDTO>> user2RoleMap;
	private Map<String, Set<String>> user2RoleRightMap;
	
	private List<AuthorizationOrganization> hbmOrganizationList;
	private Map<String, Set<MembershipDTO>> user2OrgMap;
	private Map<String, Set<String>> user2OrgRightMap;
	
	private Map<String, Set<MembershipDTO>> role2OrgMap;
	private Map<String, Set<String>> role2OrgRightMap;
	
	private Map<String, Set<MembershipDTO>> group2OrgMap;
	private Map<String, Set<String>> group2OrgRightMap;
	
	private Map<String, Set<MembershipDTO>> resource2OrgMap;
	private Map<String, Set<String>> resource2OrgRightMap;
	
	private Map<String, Set<MembershipDTO>> org2Org2Map;
	private Map<String, Set<String>> org2OrgRightMap;
	
	private Map<String, AuthorizationAccessRight> tempAccessRightMap;
	private Map<Integer, AuthorizationAccessRight> tempAccessRightBitMap;
	
	private Map<String, AuthorizationOrganization> tempOrganizationIdMap;
	
	private Map<String, AuthorizationRole> tempRoleIdMap;
	
	private Map<String, AuthorizationResource> tempResourceIdMap;
	
	private Map<String, AuthorizationGroup> tempGroupIdMap;
	
	private Map<Integer, AuthorizationResource> resourceBitSetMap;
	private Map<Integer, AuthorizationGroup> groupBitSetMap;
	private Map<Integer, AuthorizationRole> roleBitSetMap;
	private Map<Integer, AuthorizationOrganization> organizationBitSetMap;

	public AuthorizationManagerDataModel() {
		
	}

	public List<AuthorizationResource> getHbmResourceList() {
		return hbmResourceList;
	}

	public void setHbmResourceList(List<AuthorizationResource> hbmResourceList) {
		this.hbmResourceList = hbmResourceList;
	}

	public Map<String, Set<MembershipDTO>> getResource2ResourceMap() {
		return resource2ResourceMap;
	}

	public void setResource2ResourceMap(
			Map<String, Set<MembershipDTO>> resource2ResourceMap) {
		this.resource2ResourceMap = resource2ResourceMap;
	}

	public Map<String, Set<String>> getResource2ResourceRightMap() {
		return resource2ResourceRightMap;
	}

	public void setResource2ResourceRightMap(
			Map<String, Set<String>> resource2ResourceRightMap) {
		this.resource2ResourceRightMap = resource2ResourceRightMap;
	}

	public Map<String, Set<MembershipDTO>> getUser2ResourceMap() {
		return user2ResourceMap;
	}

	public void setUser2ResourceMap(Map<String, Set<MembershipDTO>> user2ResourceMap) {
		this.user2ResourceMap = user2ResourceMap;
	}

	public Map<String, Set<String>> getUser2ResourceRightMap() {
		return user2ResourceRightMap;
	}

	public void setUser2ResourceRightMap(
			Map<String, Set<String>> user2ResourceRightMap) {
		this.user2ResourceRightMap = user2ResourceRightMap;
	}

	public List<AuthorizationGroup> getHbmGroupList() {
		return hbmGroupList;
	}

	public void setHbmGroupList(List<AuthorizationGroup> hbmGroupList) {
		this.hbmGroupList = hbmGroupList;
	}

	public Map<String, Set<MembershipDTO>> getGroup2GroupMap() {
		return group2GroupMap;
	}

	public void setGroup2GroupMap(Map<String, Set<MembershipDTO>> group2GroupMap) {
		this.group2GroupMap = group2GroupMap;
	}

	public Map<String, Set<String>> getGroup2GroupRightMap() {
		return group2GroupRightMap;
	}

	public void setGroup2GroupRightMap(Map<String, Set<String>> group2GroupRightMap) {
		this.group2GroupRightMap = group2GroupRightMap;
	}

	public Map<String, Set<MembershipDTO>> getGroup2UserMap() {
		return group2UserMap;
	}

	public void setGroup2UserMap(Map<String, Set<MembershipDTO>> group2UserMap) {
		this.group2UserMap = group2UserMap;
	}

	public Map<String, Set<String>> getGroup2UserRightMap() {
		return group2UserRightMap;
	}

	public void setGroup2UserRightMap(Map<String, Set<String>> group2UserRightMap) {
		this.group2UserRightMap = group2UserRightMap;
	}

	public Map<String, Set<MembershipDTO>> getGroup2ResourceMap() {
		return group2ResourceMap;
	}

	public void setGroup2ResourceMap(
			Map<String, Set<MembershipDTO>> group2ResourceMap) {
		this.group2ResourceMap = group2ResourceMap;
	}

	public Map<String, Set<String>> getGroup2ResourceRightMap() {
		return group2ResourceRightMap;
	}

	public void setGroup2ResourceRightMap(
			Map<String, Set<String>> group2ResourceRightMap) {
		this.group2ResourceRightMap = group2ResourceRightMap;
	}

	public List<AuthorizationRole> getHbmRoleList() {
		return hbmRoleList;
	}

	public void setHbmRoleList(List<AuthorizationRole> hbmRoleList) {
		this.hbmRoleList = hbmRoleList;
	}

	public Map<String, Set<MembershipDTO>> getRole2RoleMap() {
		return role2RoleMap;
	}

	public void setRole2RoleMap(Map<String, Set<MembershipDTO>> role2RoleMap) {
		this.role2RoleMap = role2RoleMap;
	}

	public Map<String, Set<String>> getRole2RoleRightMap() {
		return role2RoleRightMap;
	}

	public void setRole2RoleRightMap(Map<String, Set<String>> role2RoleRightMap) {
		this.role2RoleRightMap = role2RoleRightMap;
	}

	public Map<String, Set<MembershipDTO>> getRole2GroupMap() {
		return role2GroupMap;
	}

	public void setRole2GroupMap(Map<String, Set<MembershipDTO>> role2GroupMap) {
		this.role2GroupMap = role2GroupMap;
	}

	public Map<String, Set<String>> getRole2GroupRightMap() {
		return role2GroupRightMap;
	}

	public void setRole2GroupRightMap(Map<String, Set<String>> role2GroupRightMap) {
		this.role2GroupRightMap = role2GroupRightMap;
	}

	public Map<String, Set<MembershipDTO>> getRole2ResourceMap() {
		return role2ResourceMap;
	}

	public void setRole2ResourceMap(Map<String, Set<MembershipDTO>> role2ResourceMap) {
		this.role2ResourceMap = role2ResourceMap;
	}

	public Map<String, Set<String>> getRole2ResourceRightMap() {
		return role2ResourceRightMap;
	}

	public void setRole2ResourceRightMap(
			Map<String, Set<String>> role2ResourceRightMap) {
		this.role2ResourceRightMap = role2ResourceRightMap;
	}

	public Map<String, Set<MembershipDTO>> getUser2RoleMap() {
		return user2RoleMap;
	}

	public void setUser2RoleMap(Map<String, Set<MembershipDTO>> user2RoleMap) {
		this.user2RoleMap = user2RoleMap;
	}

	public Map<String, Set<String>> getUser2RoleRightMap() {
		return user2RoleRightMap;
	}

	public void setUser2RoleRightMap(Map<String, Set<String>> user2RoleRightMap) {
		this.user2RoleRightMap = user2RoleRightMap;
	}

	public List<AuthorizationOrganization> getHbmOrganizationList() {
		return hbmOrganizationList;
	}

	public void setHbmOrganizationList(
			List<AuthorizationOrganization> hbmOrganizationList) {
		this.hbmOrganizationList = hbmOrganizationList;
	}

	public Map<String, Set<MembershipDTO>> getUser2OrgMap() {
		return user2OrgMap;
	}

	public void setUser2OrgMap(Map<String, Set<MembershipDTO>> user2OrgMap) {
		this.user2OrgMap = user2OrgMap;
	}

	public Map<String, Set<String>> getUser2OrgRightMap() {
		return user2OrgRightMap;
	}

	public void setUser2OrgRightMap(Map<String, Set<String>> user2OrgRightMap) {
		this.user2OrgRightMap = user2OrgRightMap;
	}

	public Map<String, Set<MembershipDTO>> getRole2OrgMap() {
		return role2OrgMap;
	}

	public void setRole2OrgMap(Map<String, Set<MembershipDTO>> role2OrgMap) {
		this.role2OrgMap = role2OrgMap;
	}

	public Map<String, Set<String>> getRole2OrgRightMap() {
		return role2OrgRightMap;
	}

	public void setRole2OrgRightMap(Map<String, Set<String>> role2OrgRightMap) {
		this.role2OrgRightMap = role2OrgRightMap;
	}

	public Map<String, Set<MembershipDTO>> getGroup2OrgMap() {
		return group2OrgMap;
	}

	public void setGroup2OrgMap(Map<String, Set<MembershipDTO>> group2OrgMap) {
		this.group2OrgMap = group2OrgMap;
	}

	public Map<String, Set<String>> getGroup2OrgRightMap() {
		return group2OrgRightMap;
	}

	public void setGroup2OrgRightMap(Map<String, Set<String>> group2OrgRightMap) {
		this.group2OrgRightMap = group2OrgRightMap;
	}

	public Map<String, Set<MembershipDTO>> getResource2OrgMap() {
		return resource2OrgMap;
	}

	public void setResource2OrgMap(Map<String, Set<MembershipDTO>> resource2OrgMap) {
		this.resource2OrgMap = resource2OrgMap;
	}

	public Map<String, Set<String>> getResource2OrgRightMap() {
		return resource2OrgRightMap;
	}

	public void setResource2OrgRightMap(
			Map<String, Set<String>> resource2OrgRightMap) {
		this.resource2OrgRightMap = resource2OrgRightMap;
	}

	public Map<String, Set<MembershipDTO>> getOrg2Org2Map() {
		return org2Org2Map;
	}

	public void setOrg2Org2Map(Map<String, Set<MembershipDTO>> org2Org2Map) {
		this.org2Org2Map = org2Org2Map;
	}

	public Map<String, Set<String>> getOrg2OrgRightMap() {
		return org2OrgRightMap;
	}

	public void setOrg2OrgRightMap(Map<String, Set<String>> org2OrgRightMap) {
		this.org2OrgRightMap = org2OrgRightMap;
	}

	public AtomicInteger getTempGroupBitSet() {
		return tempGroupBitSet;
	}

	public AtomicInteger getTempRoleBitSet() {
		return tempRoleBitSet;
	}

	public AtomicInteger getTempResourceBitSet() {
		return tempResourceBitSet;
	}

	public AtomicInteger getTempUserBitSet() {
		return tempUserBitSet;
	}

	public AtomicInteger getTempOrgBitSet() {
		return tempOrgBitSet;
	}

	public AtomicInteger getTempAccessRightBitSet() {
		return tempAccessRightBitSet;
	}

	public Map<String, AuthorizationAccessRight> getTempAccessRightMap() {
		return tempAccessRightMap;
	}

	public void setTempAccessRightMap(
			Map<String, AuthorizationAccessRight> tempAccessRightMap) {
		this.tempAccessRightMap = tempAccessRightMap;
	}

	public Map<Integer, AuthorizationAccessRight> getTempAccessRightBitMap() {
		return tempAccessRightBitMap;
	}

	public void setTempAccessRightBitMap(
			Map<Integer, AuthorizationAccessRight> tempAccessRightBitMap) {
		this.tempAccessRightBitMap = tempAccessRightBitMap;
	}

	public Map<String, AuthorizationOrganization> getTempOrganizationIdMap() {
		return tempOrganizationIdMap;
	}

	public void setTempOrganizationIdMap(
			Map<String, AuthorizationOrganization> tempOrganizationIdMap) {
		this.tempOrganizationIdMap = tempOrganizationIdMap;
	}

	public Map<String, AuthorizationRole> getTempRoleIdMap() {
		return tempRoleIdMap;
	}

	public void setTempRoleIdMap(Map<String, AuthorizationRole> tempRoleIdMap) {
		this.tempRoleIdMap = tempRoleIdMap;
	}

	public Map<String, AuthorizationResource> getTempResourceIdMap() {
		return tempResourceIdMap;
	}

	public void setTempResourceIdMap(
			Map<String, AuthorizationResource> tempResourceIdMap) {
		this.tempResourceIdMap = tempResourceIdMap;
	}

	public Map<String, AuthorizationGroup> getTempGroupIdMap() {
		return tempGroupIdMap;
	}

	public void setTempGroupIdMap(Map<String, AuthorizationGroup> tempGroupIdMap) {
		this.tempGroupIdMap = tempGroupIdMap;
	}

	public int getNumOfRights() {
		return getTempAccessRightMap().size();
	}

	public Map<Integer, AuthorizationResource> getResourceBitSetMap() {
		return resourceBitSetMap;
	}

	public void setResourceBitSetMap(
			Map<Integer, AuthorizationResource> resourceBitSetMap) {
		this.resourceBitSetMap = resourceBitSetMap;
	}

	public Map<Integer, AuthorizationGroup> getGroupBitSetMap() {
		return groupBitSetMap;
	}

	public void setGroupBitSetMap(Map<Integer, AuthorizationGroup> groupBitSetMap) {
		this.groupBitSetMap = groupBitSetMap;
	}

	public Map<Integer, AuthorizationRole> getRoleBitSetMap() {
		return roleBitSetMap;
	}

	public void setRoleBitSetMap(Map<Integer, AuthorizationRole> roleBitSetMap) {
		this.roleBitSetMap = roleBitSetMap;
	}

	public Map<Integer, AuthorizationOrganization> getOrganizationBitSetMap() {
		return organizationBitSetMap;
	}

	public void setOrganizationBitSetMap(
			Map<Integer, AuthorizationOrganization> organizationBitSetMap) {
		this.organizationBitSetMap = organizationBitSetMap;
	}
	
}
