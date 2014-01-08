package org.openiam.authmanager.dao.impl;

import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.dao.RoleDAO;
import org.openiam.core.dao.AbstractJDBCDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("jdbcRoleDAO")
public class JDBCRoleDAOImpl extends AbstractJDBCDao implements RoleDAO {

	private static final RowMapper<AuthorizationRole> rowMapper = new RoleMapper();
	
	private String GET_ALL_ROLES = "SELECT ROLE_ID AS ROLE_ID, ROLE_NAME AS NAME, DESCRIPTION AS DESCRIPTION, STATUS AS STATUS, MANAGED_SYS_ID AS MANAGED_SYS_ID FROM %s.ROLE";
	
	@Override
	public void initSqlStatements() {
		GET_ALL_ROLES = String.format(GET_ALL_ROLES, getSchemaName());
	}
	
	@Override
	public List<AuthorizationRole> getList() {
		if(log.isDebugEnabled()) {
			log.info(String.format("Query: %s", GET_ALL_ROLES));
		}
		return getJdbcTemplate().query(GET_ALL_ROLES, rowMapper);
	}

	private static class RoleMapper implements RowMapper<AuthorizationRole> {

		@Override
		public AuthorizationRole mapRow(ResultSet rs, int rowNum) throws SQLException {
			final AuthorizationRole role = new AuthorizationRole();
			role.setId(rs.getString("ROLE_ID"));
			role.setName(rs.getString("NAME"));
            role.setDescription(rs.getString("DESCRIPTION"));
            role.setStatus(rs.getString("STATUS"));
            role.setManagedSysId(rs.getString("MANAGED_SYS_ID"));
			return role;
		}
	}
}
