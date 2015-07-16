package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;
import org.openiam.authmanager.dao.MembershipDAO;
import org.openiam.base.KeyDTO;
import org.openiam.base.domain.KeyEntity;
import org.openiam.core.dao.AbstractJDBCDao;
import org.openiam.idm.srvc.grp.domain.GroupToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.grp.domain.GroupToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.GroupToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrgToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.ResourceToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.RoleToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.res.domain.ResourceToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.domain.RoleToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.role.domain.RoleToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.role.domain.RoleToRoleMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserToOrganizationMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserToRoleMembershipXrefEntity;
import org.openiam.membership.MembershipDTO;
import org.openiam.membership.MembershipRightDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcMembershipDAO")
public class JdbcMembershipDAO extends AbstractJDBCDao implements MembershipDAO {
	
	private static final Log log = LogFactory.getLog(JdbcMembershipDAO.class);
	
	private static final RowMapper<MembershipDTO> memberMapper = new MembershipDTOMapper();
	private static final RowMapper<MembershipRightDTO> rightMapper = new MembershipRightDTOMapper();
	private static final UserRowMapper urm = new UserRowMapper();

	private static final String GET_MEMBERSHIP = "SELECT %s AS MEMBER_ENTITY_ID, %s AS ENTITY_ID, MEMBERSHIP_ID AS MEMBERSHIP_ID FROM %s.%s";
	private static final String GET_RIGHTS = "SELECT MEMBERSHIP_ID AS MEMBERSHIP_ID, ACCESS_RIGHT_ID AS ACCESS_RIGHT_ID FROM %s.%s";
	private static final String GET_ENTITY = "SELECT %S AS ID, %s AS NAME, %s AS DESCRIPTION, %s AS STATUS FROM %s.%s";
	
	private String GET_FULLY_POPULATED_USER_RS_LIST = "SELECT " + 
													  "	l.USER_ID AS ID, " + 
													  "	gm.GRP_ID AS GROUP_ID, " + 
												      " rm.ROLE_ID AS ROLE_ID, " + 
													  "	resm.RESOURCE_ID AS RESOURCE_ID, " + 
													  "	orgm.COMPANY_ID AS COMPANY_ID, " + 
													  "	gmr.ACCESS_RIGHT_ID AS GROUP_ID_RIGHT, " + 
													  "	rmr.ACCESS_RIGHT_ID AS ROLE_ID_RIGHT, " + 
													  "	resmr.ACCESS_RIGHT_ID AS RESOURCE_ID_RIGHT, " + 
													  "	orgmr.ACCESS_RIGHT_ID AS COMPANY_ID_RIGHT " + 
													  "		FROM " + 
													  "	openiam.LOGIN l " +  	
													  "	LEFT JOIN openiam.USER_GRP gm " +  		
													  "		ON l.USER_ID=gm.USER_ID " + 
													  "	LEFT JOIN openiam.USER_ROLE rm " +  		
													  "		ON l.USER_ID=rm.USER_ID " + 
													  "	LEFT JOIN openiam.RESOURCE_USER resm " +  		
													  "		ON l.USER_ID=resm.USER_ID " + 
													  "	LEFT JOIN openiam.USER_AFFILIATION orgm " + 
													  "		ON l.USER_ID=orgm.USER_ID " + 
													  "	LEFT JOIN openiam.USER_GRP_MEMBERSHIP_RIGHTS gmr " +  		
													  "		ON gm.MEMBERSHIP_ID=gmr.MEMBERSHIP_ID " + 
													  "	LEFT JOIN openiam.USER_ROLE_MEMBERSHIP_RIGHTS rmr " +  		
													  "		ON rm.MEMBERSHIP_ID=rmr.MEMBERSHIP_ID " + 
													  "	LEFT JOIN openiam.USER_RES_MEMBERSHIP_RIGHTS resmr " +  		
													  "		ON resm.MEMBERSHIP_ID=resmr.MEMBERSHIP_ID " + 
													  "	LEFT JOIN openiam.USER_AFFILIATION_RIGHTS orgmr " + 	
													  "		ON orgm.MEMBERSHIP_ID=orgmr.MEMBERSHIP_ID " + 
													  "	WHERE l.USER_ID=?;";

	private String GET_USER_IDS_FOR_RESOURCE_WITH_RIGHT = "SELECT r.USER_ID FROM %s.RESOURCE_USER r JOIN %s.USER_RES_MEMBERSHIP_RIGHTS rm ON r.MEMBERSHIP_ID=rm.MEMBERSHIP_ID WHERE r.RESOURCE_ID=? AND rm.ACCESS_RIGHT_ID=?;";
	private String GET_USER_IDS_FOR_RESOURCE = "SELECT USER_ID FROM %s.RESOURCE_USER WHERE RESOURCE_ID=?";
	
	private String GET_USER_IDS_FOR_GROUP_WITH_RIGHT = "SELECT r.USER_ID FROM %s.USER_GRP r JOIN %s.USER_GRP_MEMBERSHIP_RIGHTS rm ON r.MEMBERSHIP_ID=rm.MEMBERSHIP_ID WHERE r.GRP_ID=? AND rm.ACCESS_RIGHT_ID=?;";
	private String GET_USER_IDS_FOR_GROUP = "SELECT USER_ID FROM %s.USER_GRP WHERE GRP_ID=?";
	
	private String GET_USERS = "SELECT USER_ID AS ID FROM %s.LOGIN WHERE LAST_LOGIN >= ?";
	private String GET_RESOURCES = "SELECT RESOURCE_ID AS ID, NAME AS NAME, DESCRIPTION AS DESCRIPTION, RESOURCE_TYPE_ID AS RESOURCE_TYPE_ID FROM %s.RES";
	private String GET_GROUPS;
	private String GET_ROLES;
	private String GET_ORGS;
	
	private String USER_ROLE_XREFS;
	private String USER_GRP_XREFS;
	private String USER_ORG_XREFS;
	private String USER_RES_XREFS;
	
	private String ORG_ORG_XREFS;
	private String ORG_ROLE_XREFS;
	private String ORG_GRP_XREFS;
	private String ORG_RES_XREFS;
	
	private String ROLE_ROLE_XREFS;
	private String ROLE_GRP_XREFS;
	private String ROLE_RES_XREFS;
	
	private String GRP_GRP_XREFS;
	private String GRP_RES_XREFS;
	
	private String RES_RES_XREFS;
	
	private String USER_ROLE_XREFS_RIGHTS;
	private String USER_GRP_XREFS_RIGHTS;
	private String USER_ORG_XREFS_RIGHTS;
	private String USER_RES_XREFS_RIGHTS;
	
	private String ORG_ORG_XREFS_RIGHTS;
	private String ORG_ROLE_XREFS_RIGHTS;
	private String ORG_GRP_XREFS_RIGHTS;
	private String ORG_RES_XREFS_RIGHTS;
	
	private String ROLE_ROLE_XREFS_RIGHTS;
	private String ROLE_GRP_XREFS_RIGHTS;
	private String ROLE_RES_XREFS_RIGHTS;
	
	private String GRP_GRP_XREFS_RIGHTS;
	private String GRP_RES_XREFS_RIGHTS;
	
	private String RES_RES_XREFS_RIGHTS;
	
	private String getMembershipSQL( final String memberName, final String entity, final String tableName) {
		return String.format(GET_MEMBERSHIP, memberName, entity, getSchemaName(), tableName);
	}
	
	private String getRightSQL(final String tableName) {
		return String.format(GET_RIGHTS, getSchemaName(), tableName);
	}
	
	private String getEntitySQL(final String idName, final String name, final String description, final String status, final String tableName) {
		return String.format(GET_ENTITY, idName, name, description, status, getSchemaName(), tableName);
	}
	
	@Override
	protected void initSqlStatements() {
		final String schemaName = getSchemaName();
		GET_FULLY_POPULATED_USER_RS_LIST = String.format(GET_FULLY_POPULATED_USER_RS_LIST, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName);
		GET_USERS = String.format(GET_USERS, schemaName);
		GET_USER_IDS_FOR_RESOURCE_WITH_RIGHT = String.format(GET_USER_IDS_FOR_RESOURCE_WITH_RIGHT, schemaName, schemaName);
		GET_USER_IDS_FOR_RESOURCE = String.format(GET_USER_IDS_FOR_RESOURCE, schemaName);
		GET_USER_IDS_FOR_GROUP_WITH_RIGHT = String.format(GET_USER_IDS_FOR_GROUP_WITH_RIGHT, schemaName, schemaName);
		GET_USER_IDS_FOR_GROUP = String.format(GET_USER_IDS_FOR_GROUP, schemaName);
		GET_RESOURCES = String.format(GET_RESOURCES, schemaName);
		GET_ORGS = getEntitySQL("COMPANY_ID", "COMPANY_NAME", "DESCRIPTION", "STATUS", "COMPANY");
		GET_ROLES = getEntitySQL("ROLE_ID", "ROLE_NAME", "DESCRIPTION", "STATUS", "ROLE");
		GET_GROUPS = getEntitySQL("GRP_ID", "GRP_NAME", "GROUP_DESC", "STATUS", "GRP");
		
		USER_ROLE_XREFS = getMembershipSQL("USER_ID", "ROLE_ID", "USER_ROLE");
		USER_GRP_XREFS = getMembershipSQL("USER_ID", "GRP_ID", "USER_GRP");
		USER_ORG_XREFS = getMembershipSQL("USER_ID", "COMPANY_ID", "USER_AFFILIATION");
		USER_RES_XREFS = getMembershipSQL("USER_ID", "RESOURCE_ID", "RESOURCE_USER");
		
		ORG_ORG_XREFS = getMembershipSQL("MEMBER_COMPANY_ID", "COMPANY_ID", "COMPANY_TO_COMPANY_MEMBERSHIP");
		ORG_ROLE_XREFS = getMembershipSQL("ROLE_ID", "COMPANY_ID", "ROLE_ORG_MEMBERSHIP");
		ORG_GRP_XREFS = getMembershipSQL("GRP_ID", "COMPANY_ID", "GROUP_ORGANIZATION");
		ORG_RES_XREFS = getMembershipSQL("RESOURCE_ID", "COMPANY_ID", "RES_ORG_MEMBERSHIP");
		
		ROLE_ROLE_XREFS = getMembershipSQL("MEMBER_ROLE_ID", "ROLE_ID", "role_to_role_membership");
		ROLE_GRP_XREFS = getMembershipSQL("GRP_ID", "ROLE_ID", "GRP_ROLE");
		ROLE_RES_XREFS = getMembershipSQL("RESOURCE_ID", "ROLE_ID", "RESOURCE_ROLE");
		
		GRP_GRP_XREFS = getMembershipSQL("MEMBER_GROUP_ID", "GROUP_ID", "grp_to_grp_membership");
		GRP_RES_XREFS = getMembershipSQL("RESOURCE_ID", "GRP_ID", "RESOURCE_GROUP");
		
		RES_RES_XREFS = getMembershipSQL("MEMBER_RESOURCE_ID", "RESOURCE_ID", "res_to_res_membership");
		
		USER_ROLE_XREFS_RIGHTS = getRightSQL("USER_ROLE_MEMBERSHIP_RIGHTS");
		USER_GRP_XREFS_RIGHTS = getRightSQL("USER_GRP_MEMBERSHIP_RIGHTS");
		USER_ORG_XREFS_RIGHTS = getRightSQL("USER_AFFILIATION_RIGHTS");
		USER_RES_XREFS_RIGHTS = getRightSQL("USER_RES_MEMBERSHIP_RIGHTS");
		
		ORG_ORG_XREFS_RIGHTS = getRightSQL("ORG_TO_ORG_MEMBERSHIP_RIGHTS");
		ORG_ROLE_XREFS_RIGHTS = getRightSQL("ROLE_ORG_MEMBERSHIP_RIGHTS");
		ORG_GRP_XREFS_RIGHTS = getRightSQL("GRP_ORG_MEMBERSHIP_RIGHTS");
		ORG_RES_XREFS_RIGHTS = getRightSQL("RES_ORG_MEMBERSHIP_RIGHTS");
		
		ROLE_ROLE_XREFS_RIGHTS = getRightSQL("ROLE_ROLE_MEMBERSHIP_RIGHTS");
		ROLE_GRP_XREFS_RIGHTS = getRightSQL("GRP_ROLE_MEMBERSHIP_RIGHTS");
		ROLE_RES_XREFS_RIGHTS = getRightSQL("RES_ROLE_MEMBERSHIP_RIGHTS");
		
		GRP_GRP_XREFS_RIGHTS = getRightSQL("GRP_GRP_MEMBERSHIP_RIGHTS");
		GRP_RES_XREFS_RIGHTS = getRightSQL("RES_GRP_MEMBERSHIP_RIGHTS");
		
		RES_RES_XREFS_RIGHTS = getRightSQL("RES_RES_MEMBERSHIP_RIGHTS");
	}
	
	@Override
	public List<MembershipRightDTO> getResource2ResourceRights() {
		return getJdbcTemplate().query(RES_RES_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getUser2ResourceRights() {
		return getJdbcTemplate().query(USER_RES_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getGroup2ResourceRights() {
		return getJdbcTemplate().query(GRP_RES_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getGroup2GroupRights() {
		return getJdbcTemplate().query(GRP_GRP_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getUser2GroupRights() {
		return getJdbcTemplate().query(USER_GRP_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getRole2ResourceRights() {
		return getJdbcTemplate().query(ROLE_RES_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getRole2GroupRights() {
		return getJdbcTemplate().query(ROLE_GRP_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getRole2RoleRights() {
		return getJdbcTemplate().query(ROLE_ROLE_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getUser2RoleRights() {
		return getJdbcTemplate().query(USER_ROLE_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getOrg2ResourceRights() {
		return getJdbcTemplate().query(ORG_RES_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getOrg2GroupRights() {
		return getJdbcTemplate().query(ORG_GRP_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getOrg2RoleRights() {
		return getJdbcTemplate().query(ORG_ROLE_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getOrg2OrgRights() {
		return getJdbcTemplate().query(ORG_ORG_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipRightDTO> getUser2OrgRights() {
		return getJdbcTemplate().query(USER_ORG_XREFS_RIGHTS, rightMapper);
	}

	@Override
	public List<MembershipDTO> getResource2ResourceMembership() {
		return getJdbcTemplate().query(RES_RES_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getUser2ResourceMembership() {
		return getJdbcTemplate().query(USER_RES_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getGroup2ResourceMembership() {
		return getJdbcTemplate().query(GRP_RES_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getGroup2GroupMembership() {
		return getJdbcTemplate().query(GRP_GRP_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getUser2GroupMembership() {
		return getJdbcTemplate().query(USER_GRP_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getRole2ResourceMembership() {
		return getJdbcTemplate().query(ROLE_RES_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getRole2GroupMembership() {
		return getJdbcTemplate().query(ROLE_GRP_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getRole2RoleMembership() {
		return getJdbcTemplate().query(ROLE_ROLE_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getUser2RoleMembership() {
		return getJdbcTemplate().query(USER_ROLE_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getOrg2ResourceMembership() {
		return getJdbcTemplate().query(ORG_RES_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getOrg2GroupMembership() {
		return getJdbcTemplate().query(ORG_GRP_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getOrg2RoleMembership() {
		return getJdbcTemplate().query(ORG_ROLE_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getOrg2OrgMembership() {
		return getJdbcTemplate().query(ORG_ORG_XREFS, memberMapper);
	}

	@Override
	public List<MembershipDTO> getUser2OrgMembership() {
		return getJdbcTemplate().query(USER_ORG_XREFS, memberMapper);
	}
	
	@Override
	public List<AuthorizationOrganization> getOrganizations() {
		return getJdbcTemplate().query(GET_ORGS, new OrgMapper());
	}
	
	@Override
	public List<AuthorizationGroup> getGroups() {
		return getJdbcTemplate().query(GET_GROUPS, new GroupMapper());
	}

	@Override
	public List<AuthorizationRole> getRoles() {
		return getJdbcTemplate().query(GET_ROLES, new RoleMapper());
	}
	
	@Override
	public List<AuthorizationResource> getResources() {
		return getJdbcTemplate().query(GET_RESOURCES, new ResourceMapper());
	}
	

	@Override
	public List<AuthorizationUser> getUsers(final Date date) {
		return getJdbcTemplate().query(GET_USERS, new Object[] {date}, new UserMapper());
	}
	
	private static final class UserMapper implements RowMapper<AuthorizationUser> {

		@Override
		public AuthorizationUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final AuthorizationUser dto = new AuthorizationUser();
			dto.setId(rs.getString("ID"));
			return dto;
		}
		
	}
	
	private static final class GroupMapper implements RowMapper<AuthorizationGroup> {

		@Override
		public AuthorizationGroup mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final AuthorizationGroup dto = new AuthorizationGroup();
			dto.setDescription(rs.getString("DESCRIPTION"));
			dto.setId(rs.getString("ID"));
			dto.setName(rs.getString("NAME"));
			dto.setStatus(rs.getString("STATUS"));
			return dto;
		}
	}
	
	@Override
	public InternalAuthroizationUser getUser(String id) {
		if(log.isDebugEnabled()) {
			log.debug(String.format("Query: " + GET_FULLY_POPULATED_USER_RS_LIST));
			log.debug(String.format("Params: " + id));
		}
		return getJdbcTemplate().query(GET_FULLY_POPULATED_USER_RS_LIST, new Object[] {id}, urm);
	}
	
	private static final class UserRowMapper implements ResultSetExtractor<InternalAuthroizationUser> {

		@Override
		public InternalAuthroizationUser extractData(ResultSet rs)
				throws SQLException, DataAccessException {
			final InternalAuthroizationUser user = new InternalAuthroizationUser();;
			while(rs.next()) {
				final String userId = rs.getString("ID");
				final String groupId = rs.getString("GROUP_ID");
				final String roleId = rs.getString("ROLE_ID");
				final String organizationId = rs.getString("COMPANY_ID");
				final String resourceId = rs.getString("RESOURCE_ID");
				
				final String groupIdRight = rs.getString("GROUP_ID_RIGHT");
				final String roleIdRight = rs.getString("ROLE_ID_RIGHT");
				final String organizationIdRight = rs.getString("COMPANY_ID_RIGHT");
				final String resourceIdRight = rs.getString("RESOURCE_ID_RIGHT");
				
				user.setUserId(userId);
				user.addGroupRight(groupId, groupIdRight);
				user.addRoleRight(roleId, roleIdRight);
				user.addOrganizationRight(organizationId, organizationIdRight);
				user.addResourceRight(resourceId, resourceIdRight);
			}
			return (user.getUserId() != null) ? user : null;
		}
		
	}
	
	private static final class RoleMapper implements RowMapper<AuthorizationRole> {

		@Override
		public AuthorizationRole mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final AuthorizationRole dto = new AuthorizationRole();
			dto.setDescription(rs.getString("DESCRIPTION"));
			dto.setId(rs.getString("ID"));
			dto.setName(rs.getString("NAME"));
			dto.setStatus(rs.getString("STATUS"));
			return dto;
		}
		
	}
	
	private static final class OrgMapper implements RowMapper<AuthorizationOrganization> {

		@Override
		public AuthorizationOrganization mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final AuthorizationOrganization dto = new AuthorizationOrganization();
			dto.setDescription(rs.getString("DESCRIPTION"));
			dto.setId(rs.getString("ID"));
			dto.setName(rs.getString("NAME"));
			dto.setStatus(rs.getString("STATUS"));
			return dto;
		}
		
	}
	
	private static final class ResourceMapper implements RowMapper<AuthorizationResource> {

		@Override
		public AuthorizationResource mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final AuthorizationResource dto = new AuthorizationResource();
			dto.setDescription(rs.getString("DESCRIPTION"));
			dto.setId(rs.getString("ID"));
			dto.setName(rs.getString("NAME"));
			dto.setResourceTypeId(rs.getString("RESOURCE_TYPE_ID"));
			return dto;
		}
		
	}

	private static final class MembershipRightDTOMapper implements RowMapper<MembershipRightDTO> {

		@Override
		public MembershipRightDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final MembershipRightDTO dto = new MembershipRightDTO();
			dto.setId(rs.getString("MEMBERSHIP_ID"));
			dto.setRightId(rs.getString("ACCESS_RIGHT_ID"));
			return dto;
		}
	}
	
	private static final class MembershipDTOMapper implements RowMapper<MembershipDTO> {

		@Override
		public MembershipDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final MembershipDTO dto = new MembershipDTO();
			dto.setEntityId(rs.getString("ENTITY_ID"));
			dto.setId(rs.getString("MEMBERSHIP_ID"));
			dto.setMemberEntityId(rs.getString("MEMBER_ENTITY_ID"));
			return dto;
		}
	}

	@Override
	public List<String> getUsersForResource(String resourceId) {
		return getJdbcTemplate().queryForList(GET_USER_IDS_FOR_RESOURCE, new Object[] {resourceId}, String.class);
	}

	@Override
	public List<String> getUsersForResource(String resourceId, String rightId) {
		return getJdbcTemplate().queryForList(GET_USER_IDS_FOR_RESOURCE_WITH_RIGHT, new Object[] {resourceId, rightId}, String.class);
	}
	
	@Override
	public List<String> getUsersForGroup(String groupId) {
		return getJdbcTemplate().queryForList(GET_USER_IDS_FOR_GROUP, new Object[] {groupId}, String.class);
	}

	@Override
	public List<String> getUsersForGroup(String groupId, String rightId) {
		return getJdbcTemplate().queryForList(GET_USER_IDS_FOR_GROUP_WITH_RIGHT, new Object[] {groupId, rightId}, String.class);
	}
}
