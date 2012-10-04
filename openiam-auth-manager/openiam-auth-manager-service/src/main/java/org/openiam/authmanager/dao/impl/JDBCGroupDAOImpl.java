package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.dao.GroupDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcGroupDAO")
public class JDBCGroupDAOImpl extends AbstractJDBCDao implements GroupDAO {

	private static final RowMapper<AuthorizationGroup> rowMapper = new GroupMapper();
	
	private String GET_ALL_GROUPS = "SELECT GRP_ID AS GROUP_ID, GRP_NAME AS NAME FROM %s.GRP";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL_GROUPS = String.format(GET_ALL_GROUPS, getSchemaName());
	}
	
	@Override
	public List<AuthorizationGroup> getList() {
		return getJdbcTemplate().query(GET_ALL_GROUPS, rowMapper);
	}
	
	private static class GroupMapper implements RowMapper<AuthorizationGroup> {

		@Override
		public AuthorizationGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
			final AuthorizationGroup group = new AuthorizationGroup();
			group.setId(rs.getString("GROUP_ID"));
			group.setName(rs.getString("NAME"));
			return group;
		}
	}
}
