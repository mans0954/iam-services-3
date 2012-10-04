package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.dao.RoleDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcRoleDAO")
public class JDBCRoleDAOImpl extends AbstractJDBCDao implements RoleDAO {

	private static final RowMapper<AuthorizationRole> rowMapper = new RoleMapper();
	
	private String GET_ALL_ROLES = "SELECT ROLE_ID AS ROLE_ID, ROLE_NAME AS NAME FROM %s.ROLE";
	
	@Override
	public void initSqlStatements() {
		GET_ALL_ROLES = String.format(GET_ALL_ROLES, getSchemaName());
	}
	
	@Override
	public List<AuthorizationRole> getList() {
		return getJdbcTemplate().query(GET_ALL_ROLES, rowMapper);
	}

	private static class RoleMapper implements RowMapper<AuthorizationRole> {

		@Override
		public AuthorizationRole mapRow(ResultSet rs, int rowNum) throws SQLException {
			final AuthorizationRole role = new AuthorizationRole();
			role.setId(rs.getString("ROLE_ID"));
			role.setName(rs.getString("NAME"));
			return role;
		}
	}
}
