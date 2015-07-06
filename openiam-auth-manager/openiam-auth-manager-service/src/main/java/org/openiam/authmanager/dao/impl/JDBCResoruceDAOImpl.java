package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.dao.ResourceDAO;
import org.openiam.authmanager.util.AuthorizationConstants;
import org.openiam.core.dao.AbstractJDBCDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcResourceDAO")
public class JDBCResoruceDAOImpl extends AbstractJDBCDao implements ResourceDAO  {

	private static final RowMapper<AuthorizationMenu> menuMapper = new MenuMapper();
	
	private String GET_ALL = "SELECT RESOURCE_ID AS RESOURCE_ID, RESOURCE_TYPE_ID AS RESOURCE_TYPE_ID, NAME AS NAME, DESCRIPTION AS DESCRIPTION, IS_PUBLIC AS IS_PUBLIC, RISK AS RISK, ADMIN_RESOURCE_ID AS ADMIN_RESOURCE_ID, COORELATED_NAME AS COORELATED_NAME FROM %s.RES";
	private String GET_ALL_MENUS = "SELECT RESOURCE_ID AS RESOURCE_ID, URL AS MENU_URL, NAME AS MENU_NAME, DISPLAY_ORDER AS DISPLAY_ORDER, IS_PUBLIC AS IS_PUBLIC, RISK AS RISK FROM %s.RES WHERE RESOURCE_TYPE_ID = ?";
	private String GET_AUTH_MENU_BY_ID = "SELECT RESOURCE_ID AS RESOURCE_ID, URL AS MENU_URL, NAME AS MENU_NAME, DISPLAY_ORDER AS DISPLAY_ORDER, IS_PUBLIC AS IS_PUBLIC, RISK AS RISK FROM %s.RES WHERE RESOURCE_TYPE_ID = ? AND RESOURCE_ID = ?";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
		GET_ALL_MENUS = String.format(GET_ALL_MENUS, getSchemaName());
		GET_AUTH_MENU_BY_ID = String.format(GET_AUTH_MENU_BY_ID, getSchemaName());
	}
	
	@Override
	public List<AuthorizationMenu> getAuthorizationMenus() {
		if(log.isDebugEnabled()) {
			log.debug(String.format("Query: %s", GET_ALL_MENUS));
		}
		return getJdbcTemplate().query(GET_ALL_MENUS, menuMapper, new Object[] {AuthorizationConstants.MENU_ITEM_RESOURCE_TYPE});
	}
	
	private static class MenuMapper implements RowMapper<AuthorizationMenu> {

		@Override
		public AuthorizationMenu mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final AuthorizationMenu menu = new AuthorizationMenu();
			menu.setId(rs.getString("RESOURCE_ID"));
			menu.setUrl(rs.getString("MENU_URL"));
			menu.setName(rs.getString("MENU_NAME"));
			menu.setDisplayOrder(rs.getInt("DISPLAY_ORDER"));
            menu.setRisk(rs.getString("RISK"));
			menu.setIsPublic("Y".equals(rs.getString("IS_PUBLIC")));
			return menu;
		}
		
	}
}
