package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.dao.ResourcePropDAO;
import org.openiam.core.dao.AbstractJDBCDao;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcResourcePropDAO")
public class JDBCResourcePropertyDAOImpl extends AbstractJDBCDao implements ResourcePropDAO {

	private static final RowMapper<ResourceProp> mapper = new Mapper();
	
	private static String GET_ALL = "SELECT RESOURCE_PROP_ID AS ID, RESOURCE_ID AS RESOURCE_ID, METADATA_ID AS METADATA_ID, NAME AS NAME, VALUE AS VALUE FROM %s.RESOURCE_PROP";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	@Override
	public List<ResourceProp> getList() {
		return getJdbcTemplate().query(GET_ALL, mapper);
	}
	
	private static final class Mapper implements RowMapper<ResourceProp> {

		@Override
		public ResourceProp mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final ResourceProp prop = new ResourceProp();
			prop.setId(rs.getString("ID"));
			prop.setResourceId(rs.getString("RESOURCE_ID"));
			prop.setMetadataId(rs.getString("METADATA_ID"));
			prop.setName(rs.getString("NAME"));
			prop.setValue(rs.getString("VALUE"));
			return prop;
		}
	}
}
