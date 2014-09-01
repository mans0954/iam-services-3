package org.openiam.authmanager.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;
import org.openiam.authmanager.dao.UserDAO;
import org.openiam.core.dao.AbstractJDBCDao;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("jdbcUserDao")
public class JDBCUserDAOImpl extends AbstractJDBCDao implements UserDAO {

	private static final Log log = LogFactory.getLog(JDBCUserDAOImpl.class);

    private String COUNT_ALL_USERS = "SELECT COUNT(*) AS USER_CNT FROM %s.USERS";
    private String GET_ALL_USERS_IDS = "SELECT USER_ID AS USER_ID FROM %s.USERS";
	private String GET_ALL_USERS_LOGGED_IN_AFTER = "SELECT USER_ID AS USER_ID FROM %s.LOGIN WHERE LAST_LOGIN >= ?";
	private String GET_ALL_LOGINS_WITH_LAST_LOGIN_AFTER = "SELECT LOGIN AS LOGIN, MANAGED_SYS_ID AS MANAGED_SYS_ID, USER_ID AS USER_ID FROM %s.LOGIN WHERE USER_ID IN( SELECT USER_ID FROM %s.LOGIN WHERE LAST_LOGIN >= ? )";
	private static final String GET_FULLY_POPULATED_USER_RS_LIST = "SELECT " +
												    			   "	l.USER_ID AS L_USER_ID, " +
												    			   "	l.LOGIN AS L_LOGIN, l.MANAGED_SYS_ID AS L_MANAGED_SYS_ID, " +
												    			   "	gm.GRP_ID AS GM_GROUP_ID, " +
												    			   "	rm.ROLE_ID AS RM_ROLE_ID, " +
												    			   "	resm.RESOURCE_ID AS RESM_RESOURCE_ID " +
												    			   "FROM " +
												    			   "	%s.LOGIN l " +
												    			   "	LEFT JOIN %s.USER_GRP gm " +
												    			   "		ON l.USER_ID=gm.USER_ID " +
												    			   "	LEFT JOIN %s.USER_ROLE rm " +
												    			   "		ON l.USER_ID=rm.USER_ID " +
												    			   "	LEFT JOIN %s.RESOURCE_USER resm " +
												    			   "		ON l.USER_ID=resm.USER_ID ";


    private String GET_ALL_USERS_IDS_FOR_ROLE = "SELECT USER_ID AS USER_ID FROM %s.USER_ROLE WHERE ROLE_ID IN (:ids)";
    private String GET_ALL_USERS_IDS_FOR_GROUP = "SELECT USER_ID AS USER_ID FROM %s.USER_GRP WHERE GRP_ID IN (:ids)";
    private String GET_ALL_USERS_IDS_FOR_RESOURCE = "SELECT USER_ID AS USER_ID FROM %s.RESOURCE_USER WHERE RESOURCE_ID IN (:ids)";
	
	private String GET_FULLY_POPULATED_USER_BY_ID = GET_FULLY_POPULATED_USER_RS_LIST + "WHERE l.USER_ID=?";
	private String GET_FULLY_POPULATED_USER_BY_LOGIN_ID = GET_FULLY_POPULATED_USER_RS_LIST + "WHERE l.USER_ID IN (" +
				"SELECT USER_ID FROM %s.LOGIN WHERE lower(LOGIN)=? AND lower(MANAGED_SYS_ID)=?)";
	
	private static final ResultSetExtractor<InternalAuthroizationUser> internalAuthorizationuserMapper = new InternalAuthroizationUserMapper();
	private static final RowMapper<AuthorizationUser> userMapper = new UserMapper();
	private static final RowMapper<AuthorizationManagerLoginId> loginMapper = new LoginIdMapper();
    private static final RowMapper<String> userIdMapper = new UserIdMapper();
	@Override
	public void initSqlStatements() {
		final String schemaName = getSchemaName();
        COUNT_ALL_USERS=String.format(COUNT_ALL_USERS, schemaName);
        GET_ALL_USERS_IDS=String.format(GET_ALL_USERS_IDS, schemaName);
		GET_ALL_USERS_LOGGED_IN_AFTER = String.format(GET_ALL_USERS_LOGGED_IN_AFTER, schemaName);
		GET_ALL_LOGINS_WITH_LAST_LOGIN_AFTER = String.format(GET_ALL_LOGINS_WITH_LAST_LOGIN_AFTER, schemaName, schemaName);
		GET_FULLY_POPULATED_USER_BY_ID = String.format(GET_FULLY_POPULATED_USER_BY_ID, schemaName, schemaName, schemaName, schemaName);
		GET_FULLY_POPULATED_USER_BY_LOGIN_ID = String.format(GET_FULLY_POPULATED_USER_BY_LOGIN_ID,  schemaName, schemaName, schemaName, schemaName, schemaName);

        GET_ALL_USERS_IDS_FOR_ROLE = String.format(GET_ALL_USERS_IDS_FOR_ROLE, schemaName);
        GET_ALL_USERS_IDS_FOR_GROUP = String.format(GET_ALL_USERS_IDS_FOR_GROUP, schemaName);
        GET_ALL_USERS_IDS_FOR_RESOURCE = String.format(GET_ALL_USERS_IDS_FOR_RESOURCE, schemaName);
	}
	
	@Override
	public List<AuthorizationUser> getAllUsersLoggedInAfter(final Date date) {
		if(log.isDebugEnabled()) {
			log.debug(String.format("Query: %s", GET_ALL_USERS_LOGGED_IN_AFTER));
			log.debug(String.format("Params: %s", date));
		}
		return getJdbcTemplate().query(GET_ALL_USERS_LOGGED_IN_AFTER, new Object[] {date}, userMapper);
	}
	
	@Override
	public List<AuthorizationManagerLoginId> getLoginIdsForUsersLoggedInAfter(Date date) {
		if(log.isDebugEnabled()) {
			log.debug(String.format("Query: %s", GET_ALL_LOGINS_WITH_LAST_LOGIN_AFTER));
			log.debug(String.format("Params: %s", date));
		}
		return getJdbcTemplate().query(GET_ALL_LOGINS_WITH_LAST_LOGIN_AFTER, new Object[] {date}, loginMapper);
	}
	
	@Override
	public InternalAuthroizationUser getFullUser(String userId) {
		if(log.isDebugEnabled()) {
			log.debug(String.format("Query: %s", GET_FULLY_POPULATED_USER_BY_ID));
			log.debug(String.format("Params: %s", userId));
		}
		return getJdbcTemplate().query(GET_FULLY_POPULATED_USER_BY_ID, new Object[] {userId}, internalAuthorizationuserMapper);
	}

	@Override
	public InternalAuthroizationUser getFullUser(AuthorizationManagerLoginId loginId) {
		final Object[] params = new Object[] {
			StringUtils.lowerCase(loginId.getLogin()),
			StringUtils.lowerCase(loginId.getManagedSysId())
		};
		
		if(log.isDebugEnabled()) {
			log.debug(String.format("Query: %s", GET_FULLY_POPULATED_USER_BY_LOGIN_ID));
			log.debug(String.format("Params: %s", params));
		}
		return getJdbcTemplate().query(GET_FULLY_POPULATED_USER_BY_LOGIN_ID, params, internalAuthorizationuserMapper);
	}

    @Override
    public List<String> getUserIdsList(){
        if(log.isDebugEnabled()) {
            log.debug(String.format("Query: %s", GET_ALL_USERS_IDS));
        }
        return getJdbcTemplate().query(GET_ALL_USERS_IDS, userIdMapper);
    }

    @Override
    public List<String> getUserIdsForRoles(Set<String> roleIds){
        if(log.isDebugEnabled()) {
            log.debug(String.format("Query: %s", GET_ALL_USERS_IDS_FOR_ROLE));
        }
        Map<String, Set<String>> param = Collections.singletonMap("ids",roleIds);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new
                NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());


        return namedParameterJdbcTemplate.query(GET_ALL_USERS_IDS_FOR_ROLE, new MapSqlParameterSource(param), userIdMapper);
//        return getJdbcTemplate().query(GET_ALL_USERS_IDS_FOR_ROLE, userIdMapper, Collections
//                .singletonMap("ids", roleIds));
    }
    @Override
    public List<String> getUserIdsForGroups(Set<String> groupIds){
        if(log.isDebugEnabled()) {
            log.debug(String.format("Query: %s", GET_ALL_USERS_IDS_FOR_GROUP));
        }

        Map<String, Set<String>> param = Collections.singletonMap("ids",groupIds);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new
                NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());


        return namedParameterJdbcTemplate.query(GET_ALL_USERS_IDS_FOR_GROUP, new MapSqlParameterSource(param), userIdMapper);
    }
    @Override
    public List<String> getUserIdsForResources(Set<String> resourceIds){
        if(log.isDebugEnabled()) {
            log.debug(String.format("Query: %s", GET_ALL_USERS_IDS_FOR_RESOURCE));
        }

        Map<String, Set<String>> param = Collections.singletonMap("ids",resourceIds);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new
                NamedParameterJdbcTemplate(getJdbcTemplate().getDataSource());


        return namedParameterJdbcTemplate.query(GET_ALL_USERS_IDS_FOR_RESOURCE, new MapSqlParameterSource(param), userIdMapper);
    }

	private static class UserMapper implements RowMapper<AuthorizationUser> {

		@Override
		public AuthorizationUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			final AuthorizationUser user = new AuthorizationUser();
			user.setId(rs.getString("USER_ID"));
			return user;
		}
	}
	
	private static class InternalAuthroizationUserMapper implements ResultSetExtractor<InternalAuthroizationUser> {

		@Override
		public InternalAuthroizationUser extractData(final ResultSet rs) throws SQLException, DataAccessException {
			final InternalAuthroizationUser user = new InternalAuthroizationUser();;
			while(rs.next()) {
				final String userId = rs.getString("L_USER_ID");
				final String login = rs.getString("L_LOGIN");
				final String managedSysId = rs.getString("L_MANAGED_SYS_ID");
				final String groupId = rs.getString("GM_GROUP_ID");
				final String roleId = rs.getString("RM_ROLE_ID");
				final String resourceId = rs.getString("RESM_RESOURCE_ID");
				
				user.setUserId(userId);
				user.addLoginId(new AuthorizationManagerLoginId(login, managedSysId));
				user.addGroupId(groupId);
				user.addRoleId(roleId);
				user.addResourceId(resourceId);
			}
			return (user.getUserId() != null) ? user : null;
		}
		
	}
	
	
	
	private static class LoginIdMapper implements RowMapper<AuthorizationManagerLoginId> {
		@Override
		public AuthorizationManagerLoginId mapRow(ResultSet rs, int rowNum) throws SQLException {
//			final String serviceId = rs.getString("SERVICE_ID");
			final String login = rs.getString("LOGIN");
			final String managedSysId = rs.getString("MANAGED_SYS_ID");
			final String userId = rs.getString("USER_ID");
			
			final AuthorizationManagerLoginId loginId = new AuthorizationManagerLoginId();
			loginId.setLogin(login);
			loginId.setManagedSysId(managedSysId);
			loginId.setUserId(userId);
			return loginId;
		}
	}

    private static class UserIdMapper implements RowMapper<String> {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
//			final String serviceId = rs.getString("SERVICE_ID");
//            final String login = rs.getString("LOGIN");
//            final String managedSysId = rs.getString("MANAGED_SYS_ID");
            final String userId = rs.getString("USER_ID");

//            final AuthorizationManagerLoginId loginId = new AuthorizationManagerLoginId();
//            loginId.setLogin(login);
//            loginId.setManagedSysId(managedSysId);
//            loginId.setUserId(userId);
            return userId;
        }
    }

	@Override
	public List<AuthorizationUser> getList() {
		throw new UnsupportedOperationException("userDao.getAll() not supported as per our partial-cache");
	}
}
