package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.dao.ResourceDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcResourceDAO")
public class JDBCResoruceDAOImpl extends AbstractJDBCDao implements ResourceDAO  {

	private static final RowMapper<AuthorizationResource> rowMapper = new ResourceMapper();
	
	private String GET_ALL = "SELECT RESOURCE_ID AS RESOURCE_ID, NAME AS NAME FROM %s.RES";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	@Override
	public List<AuthorizationResource> getList() {
		if(log.isDebugEnabled()) {
			log.info(String.format("Query: %s", GET_ALL));
		}
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	private static class ResourceMapper implements RowMapper<AuthorizationResource> {

		@Override
		public AuthorizationResource mapRow(ResultSet rs, int rowNum) throws SQLException {
			final AuthorizationResource resource = new AuthorizationResource();
			resource.setId(rs.getString("RESOURCE_ID"));
			resource.setName(rs.getString("NAME"));
			return resource;
		}
		
	}
}
