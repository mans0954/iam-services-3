package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.xref.RoleUserXref;
import org.openiam.authmanager.dao.RoleUserXrefDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcRoleUserXrefDAO")
public class JDBCRoleUserXrefDAOImpl extends AbstractJDBCDao implements RoleUserXrefDAO {

	private static final RowMapper<RoleUserXref> rowMapper = new XrefMapper();
	
	private String GET_ALL = "SELECT USER_ID AS USER_ID, ROLE_ID AS ROLE_ID FROM %s.USER_ROLE";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	@Override
	public List<RoleUserXref> getList() {
		if(log.isDebugEnabled()) {
			log.info(String.format("Query: %s", GET_ALL));
		}
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	private static class XrefMapper implements RowMapper<RoleUserXref> {

		@Override
		public RoleUserXref mapRow(ResultSet rs, int rowNum) throws SQLException {
			final RoleUserXref xref = new RoleUserXref();
			xref.setRoleId(rs.getString("ROLE_ID"));
			xref.setUserId(rs.getString("USER_ID"));
			return xref;
		}
		
	}
}
