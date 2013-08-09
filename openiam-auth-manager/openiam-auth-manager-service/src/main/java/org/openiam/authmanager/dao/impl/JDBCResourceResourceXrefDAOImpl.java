package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.xref.ResourceResourceXref;
import org.openiam.authmanager.dao.ResourceResourceXrefDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcResourceResourceXrefDAO")
public class JDBCResourceResourceXrefDAOImpl extends AbstractJDBCDao implements ResourceResourceXrefDAO {

	private static final RowMapper<ResourceResourceXref> rowMapper = new XrefMapper();
	
	private static String GET_ALL = "SELECT RESOURCE_ID AS RESOURCE_ID, MEMBER_RESOURCE_ID AS MEMBER_RESOURCE_ID FROM %s.res_to_res_membership";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	@Override
	public List<ResourceResourceXref> getList() {
		if(log.isDebugEnabled()) {
			log.info(String.format("Query: %s", GET_ALL));
		}
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	private static class XrefMapper implements RowMapper<ResourceResourceXref> {

		@Override
		public ResourceResourceXref mapRow(ResultSet rs, int rowNum) throws SQLException {
			final ResourceResourceXref xref = new ResourceResourceXref();
			xref.setResourceId(rs.getString("RESOURCE_ID"));
			xref.setMemberResourceId(rs.getString("MEMBER_RESOURCE_ID"));
			return xref;
		}
		
	}
}
