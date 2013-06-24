package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.xref.ResourceRoleXref;
import org.openiam.authmanager.dao.ResourceRoleXrefDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcResourceRoleXrefDAO")
public class JDBCResourceRoleXrefDAOImpl extends AbstractJDBCDao implements ResourceRoleXrefDAO {

	private static final RowMapper<ResourceRoleXref> rowMapper = new XrefMapper();
	
	private String GET_ALL = "SELECT RESOURCE_ID AS RESOURCE_ID, ROLE_ID AS ROLE_ID FROM %s.RESOURCE_ROLE";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	@Override
	public List<ResourceRoleXref> getList() {
		if(log.isDebugEnabled()) {
			log.info(String.format("Query: %s", GET_ALL));
		}
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	private static class XrefMapper implements RowMapper<ResourceRoleXref> {

		@Override
		public ResourceRoleXref mapRow(ResultSet rs, int rowNum) throws SQLException {
			final ResourceRoleXref xref = new ResourceRoleXref();
			xref.setResourceId(rs.getString("RESOURCE_ID"));
			xref.setRoleId(rs.getString("ROLE_ID"));
			return xref;
		}
		
	}
}
