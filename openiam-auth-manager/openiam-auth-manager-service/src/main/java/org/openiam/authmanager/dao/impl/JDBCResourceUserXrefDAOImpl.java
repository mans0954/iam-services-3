package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.xref.ResourceUserXref;
import org.openiam.authmanager.dao.ResourceUserXrefDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcResourceUserXrefDAO")
public class JDBCResourceUserXrefDAOImpl extends AbstractJDBCDao implements ResourceUserXrefDAO {

	private static final RowMapper<ResourceUserXref> rowMapper = new XrefMapper();
	
	private String GET_ALL = "SELECT RESOURCE_ID AS RESOURCE_ID, USER_ID AS USER_ID FROM %s.RESOURCE_USER";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	@Override
	public List<ResourceUserXref> getList() {
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	private static class XrefMapper implements RowMapper<ResourceUserXref> {

		@Override
		public ResourceUserXref mapRow(ResultSet rs, int rowNum) throws SQLException {
			final ResourceUserXref xref = new ResourceUserXref();
			xref.setResourceId(rs.getString("RESOURCE_ID"));
			xref.setUserId(rs.getString("USER_ID"));
			return xref;
		}
		
	}
}
