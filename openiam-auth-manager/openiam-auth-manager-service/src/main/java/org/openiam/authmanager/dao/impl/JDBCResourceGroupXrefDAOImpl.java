package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.xref.ResourceGroupXref;
import org.openiam.authmanager.dao.ResourceGroupXrefDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcResourceGroupXrefDAO")
public class JDBCResourceGroupXrefDAOImpl extends AbstractJDBCDao implements ResourceGroupXrefDAO {

	private static final RowMapper<ResourceGroupXref> rowMapper = new XrefMapper();
	
	private String GET_ALL = "SELECT RESOURCE_ID AS RESOURCE_ID, GRP_ID AS GROUP_ID FROM %s.RESOURCE_GROUP";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	@Override
	public List<ResourceGroupXref> getList() {
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	private static class XrefMapper implements RowMapper<ResourceGroupXref> {

		@Override
		public ResourceGroupXref mapRow(ResultSet rs, int rowNum) throws SQLException {
			final ResourceGroupXref xref = new ResourceGroupXref();
			xref.setResourceId(rs.getString("RESOURCE_ID"));
			xref.setGroupId(rs.getString("GROUP_ID"));
			return xref;
		}
		
	}
}
