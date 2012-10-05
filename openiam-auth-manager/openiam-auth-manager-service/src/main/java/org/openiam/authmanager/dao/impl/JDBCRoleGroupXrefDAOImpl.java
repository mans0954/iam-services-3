package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.xref.RoleGroupXref;
import org.openiam.authmanager.dao.RoleGroupXrefDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository("jdbcRoleGroupXrefDAO")
public class JDBCRoleGroupXrefDAOImpl extends AbstractJDBCDao implements RoleGroupXrefDAO {

	private static final RowMapper<RoleGroupXref> rowMapper = new XrefMapper();
	
	private String GET_ALL = "SELECT GRP_ID AS GROUP_ID, ROLE_ID AS ROLE_ID FROM %s.GRP_ROLE";

	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	@Override
	public List<RoleGroupXref> getList() {
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	private static class XrefMapper implements RowMapper<RoleGroupXref> {

		@Override
		public RoleGroupXref mapRow(ResultSet rs, int rowNum) throws SQLException {
			final RoleGroupXref xref = new RoleGroupXref();
			xref.setGroupId(rs.getString("GROUP_ID"));
			xref.setRoleId(rs.getString("ROLE_ID"));
			return xref;
		}
		
	}
}
