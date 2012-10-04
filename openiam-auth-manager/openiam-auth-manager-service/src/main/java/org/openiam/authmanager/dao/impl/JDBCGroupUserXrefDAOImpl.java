package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.xref.GroupUserXref;
import org.openiam.authmanager.dao.GroupUserXrefDAO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcGroupUserXrefDao")
public class JDBCGroupUserXrefDAOImpl extends AbstractJDBCDao implements GroupUserXrefDAO {

	private static final RowMapper<GroupUserXref> rowMapper = new XrefMapper();
	
	private String GET_ALL = "SELECT GRP_ID AS GROUP_ID, USER_ID AS USER_ID FROM %s.USER_GRP";

	@Override
	public List<GroupUserXref> getList() {
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}

	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
	}
	
	private static class XrefMapper implements RowMapper<GroupUserXref> {

		@Override
		public GroupUserXref mapRow(ResultSet rs, int rowNum) throws SQLException {
			final GroupUserXref xref = new GroupUserXref();
			xref.setGroupId(rs.getString("GROUP_ID"));
			xref.setUserId(rs.getString("USER_ID"));
			return xref;
		}
		
	}
}
