package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.xref.GroupGroupXref;
import org.openiam.authmanager.dao.GroupGroupXrefDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcGroupGroupXrefDao")
public class JDBCGroupGroupXrefDAOImpl extends AbstractJDBCDao implements GroupGroupXrefDAO {

	private static final RowMapper<GroupGroupXref> rowMapper = new XrefMapper();
	
	private String GET_ALL = "SELECT GROUP_ID AS GROUP_ID, MEMBER_GROUP_ID AS MEMBER_GROUP_ID FROM %s.grp_to_grp_membership";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	@Override
	public List<GroupGroupXref> getList() {
		if(log.isDebugEnabled()) {
			log.info(String.format("Query: %s", GET_ALL));
		}
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	private static class XrefMapper implements RowMapper<GroupGroupXref> {

		@Override
		public GroupGroupXref mapRow(ResultSet rs, int rowNum) throws SQLException {
			final GroupGroupXref xref = new GroupGroupXref();
			xref.setGroupId(rs.getString("GROUP_ID"));
			xref.setMemberGroupId(rs.getString("MEMBER_GROUP_ID"));
			return xref;
		}
		
	}
}
