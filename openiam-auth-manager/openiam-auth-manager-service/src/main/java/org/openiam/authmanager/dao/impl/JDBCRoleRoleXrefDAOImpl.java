package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.xref.RoleRoleXref;
import org.openiam.authmanager.dao.RoleRoleXrefDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcRoleRoleXrefDAO")
public class JDBCRoleRoleXrefDAOImpl extends AbstractJDBCDao implements RoleRoleXrefDAO {

	private static final RowMapper<RoleRoleXref> rowMapper = new XrefMapper();
	
	private String GET_ALL = "SELECT ROLE_ID AS ROLE_ID, MEMBER_ROLE_ID AS MEMBER_ROLE_ID FROM %s.role_to_role_membership";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	@Override
	public List<RoleRoleXref> getList() {
		if(log.isDebugEnabled()) {
			log.info(String.format("Query: %s", GET_ALL));
		}
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	private static class XrefMapper implements RowMapper<RoleRoleXref> {

		@Override
		public RoleRoleXref mapRow(ResultSet rs, int rowNum) throws SQLException {
			final RoleRoleXref xref = new RoleRoleXref();
			xref.setMemberRoleId(rs.getString("MEMBER_ROLE_ID"));
			xref.setRoleId(rs.getString("ROLE_ID"));
			return xref;
		}
		
	}
}
