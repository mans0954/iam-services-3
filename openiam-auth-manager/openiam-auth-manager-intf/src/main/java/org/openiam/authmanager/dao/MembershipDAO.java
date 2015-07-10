package org.openiam.authmanager.dao;

import java.util.Date;
import java.util.List;

import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;
import org.openiam.base.KeyDTO;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.membership.MembershipDTO;
import org.openiam.membership.MembershipRightDTO;

public interface MembershipDAO {

	public List<MembershipRightDTO> getResource2ResourceRights();
	public List<MembershipRightDTO> getUser2ResourceRights();
	
	public List<MembershipRightDTO> getGroup2ResourceRights();
	public List<MembershipRightDTO> getGroup2GroupRights();
	public List<MembershipRightDTO> getUser2GroupRights();
	
	public List<MembershipRightDTO> getRole2ResourceRights();
	public List<MembershipRightDTO> getRole2GroupRights();
	public List<MembershipRightDTO> getRole2RoleRights();
	public List<MembershipRightDTO> getUser2RoleRights();
	
	public List<MembershipRightDTO> getOrg2ResourceRights();
	public List<MembershipRightDTO> getOrg2GroupRights();
	public List<MembershipRightDTO> getOrg2RoleRights();
	public List<MembershipRightDTO> getOrg2OrgRights();
	public List<MembershipRightDTO> getUser2OrgRights();
	
	public List<MembershipDTO> getResource2ResourceMembership();
	public List<MembershipDTO> getUser2ResourceMembership();
	
	public List<MembershipDTO> getGroup2ResourceMembership();
	public List<MembershipDTO> getGroup2GroupMembership();
	public List<MembershipDTO> getUser2GroupMembership();
	
	public List<MembershipDTO> getRole2ResourceMembership();
	public List<MembershipDTO> getRole2GroupMembership();
	public List<MembershipDTO> getRole2RoleMembership();
	public List<MembershipDTO> getUser2RoleMembership();
	
	public List<MembershipDTO> getOrg2ResourceMembership();
	public List<MembershipDTO> getOrg2GroupMembership();
	public List<MembershipDTO> getOrg2RoleMembership();
	public List<MembershipDTO> getOrg2OrgMembership();
	public List<MembershipDTO> getUser2OrgMembership();
	
	public List<AuthorizationOrganization> getOrganizations();
	public List<AuthorizationGroup> getGroups();
	public List<AuthorizationRole> getRoles();
	public List<AuthorizationResource> getResources();
	public List<AuthorizationUser> getUsers(final Date date);
	public InternalAuthroizationUser getUser(final String id);
}
