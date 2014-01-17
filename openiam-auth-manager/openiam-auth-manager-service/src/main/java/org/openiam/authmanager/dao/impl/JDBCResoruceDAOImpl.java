package org.openiam.authmanager.dao.impl;

import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.dao.ResourceDAO;
import org.openiam.authmanager.util.AuthorizationConstants;
import org.openiam.core.dao.AbstractJDBCDao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("jdbcResourceDAO")
public class JDBCResoruceDAOImpl extends AbstractJDBCDao implements ResourceDAO  {

	private static final RowMapper<AuthorizationResource> rowMapper = new ResourceMapper();
	private static final RowMapper<AuthorizationMenu> menuMapper = new MenuMapper();
	
	private String GET_ALL = "SELECT RESOURCE_ID AS RESOURCE_ID, RESOURCE_TYPE_ID AS RESOURCE_TYPE_ID, NAME AS NAME, DESCRIPTION AS DESCRIPTION, IS_PUBLIC AS IS_PUBLIC, RISK AS RISK FROM %s.RES";
	private String GET_ALL_MENUS = "SELECT RESOURCE_ID AS RESOURCE_ID, URL AS MENU_URL, NAME AS MENU_NAME, DISPLAY_ORDER AS DISPLAY_ORDER, IS_PUBLIC AS IS_PUBLIC, RISK AS RISK FROM %s.RES WHERE RESOURCE_TYPE_ID = ?";
	private String GET_AUTH_MENU_BY_ID = "SELECT RESOURCE_ID AS RESOURCE_ID, URL AS MENU_URL, NAME AS MENU_NAME, DISPLAY_ORDER AS DISPLAY_ORDER, IS_PUBLIC AS IS_PUBLIC, RISK AS RISK FROM %s.RES WHERE RESOURCE_TYPE_ID = ? AND RESOURCE_ID = ?";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
		GET_ALL_MENUS = String.format(GET_ALL_MENUS, getSchemaName());
		GET_AUTH_MENU_BY_ID = String.format(GET_AUTH_MENU_BY_ID, getSchemaName());
	}
	
	@Override
	public List<AuthorizationResource> getList() {
		if(log.isDebugEnabled()) {
			log.debug(String.format("Query: %s", GET_ALL));
		}
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	@Override
	public List<AuthorizationMenu> getAuthorizationMenus() {
		if(log.isDebugEnabled()) {
			log.debug(String.format("Query: %s", GET_ALL_MENUS));
		}
		return getJdbcTemplate().query(GET_ALL_MENUS, menuMapper, new Object[] {AuthorizationConstants.MENU_ITEM_RESOURCE_TYPE});
	}
	
	@Override
	public AuthorizationMenu getAuthorizationMenu(final String menuId) {
		if(log.isDebugEnabled()) {
			log.debug(String.format("Query: %s", GET_AUTH_MENU_BY_ID));
		}
		try {
			return getJdbcTemplate().queryForObject(GET_AUTH_MENU_BY_ID, new Object[] {AuthorizationConstants.MENU_ITEM_RESOURCE_TYPE, menuId}, menuMapper);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
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
	
	private static class ResourceMapper implements RowMapper<AuthorizationResource> {

		@Override
		public AuthorizationResource mapRow(ResultSet rs, int rowNum) throws SQLException {
			final AuthorizationResource resource = new AuthorizationResource();
			resource.setId(rs.getString("RESOURCE_ID"));
			resource.setName(rs.getString("NAME"));
            resource.setDescription(rs.getString("DESCRIPTION"));
            resource.setResourceTypeId(rs.getString("RESOURCE_TYPE_ID"));
            resource.setRisk(rs.getString("RISK"));
			resource.setPublic("Y".equals(rs.getString("IS_PUBLIC")));
			return resource;
		}
		
	}
}
