package org.openiam.authmanager.dao.impl;

import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.dao.GroupDAO;
import org.openiam.core.dao.AbstractJDBCDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("jdbcGroupDAO")
public class JDBCGroupDAOImpl extends AbstractJDBCDao implements GroupDAO {

	
	private static final RowMapper<AuthorizationGroup> rowMapper = new GroupMapper();
	
	private String GET_ALL_GROUPS = "SELECT GRP_ID AS GROUP_ID, GRP_NAME AS NAME, GROUP_DESC AS DESCRIPTION, STATUS AS STATUS, MANAGED_SYS_ID AS MANAGED_SYS_ID FROM %s.GRP";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL_GROUPS = String.format(GET_ALL_GROUPS, getSchemaName());
	}
	
	@Override
	public List<AuthorizationGroup> getList() {
		if(log.isDebugEnabled()) {
			log.info(String.format("Query: %s", GET_ALL_GROUPS));
		}
		return getJdbcTemplate().query(GET_ALL_GROUPS, rowMapper);
	}
	
	private static class GroupMapper implements RowMapper<AuthorizationGroup> {

		@Override
		public AuthorizationGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
			final AuthorizationGroup group = new AuthorizationGroup();
			group.setId(rs.getString("GROUP_ID"));
			group.setName(rs.getString("NAME"));
            group.setDescription(rs.getString("DESCRIPTION"));
            group.setStatus(rs.getString("STATUS"));
            group.setManagedSysId(rs.getString("MANAGED_SYS_ID"));
			return group;
		}
	}
}
