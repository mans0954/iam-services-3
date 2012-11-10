package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openiam.authmanager.common.model.AuthorizationMenu;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.url.AuthorizationDomain;
import org.openiam.authmanager.common.model.url.InvalidPatternException;
import org.openiam.authmanager.dao.ResourceDAO;
import org.openiam.authmanager.util.AuthorizationConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcResourceDAO")
public class JDBCResoruceDAOImpl extends AbstractJDBCDao implements ResourceDAO  {

	private static final RowMapper<AuthorizationResource> rowMapper = new ResourceMapper();
	private static final RowMapper<AuthorizationMenu> menuMapper = new MenuMapper();
	
	private String GET_ALL = "SELECT RESOURCE_ID AS RESOURCE_ID, NAME AS NAME FROM %s.RES";
	private String GET_RESOURCE_DOMAINS_WITH_PATTERNS = "SELECT r.RESOURCE_ID AS RESOURCE_ID, prop.PROP_VALUE AS PATTERN, r.MIN_AUTH_LEVEL AS MIN_AUTH_LEVEL, r.DOMAIN AS DOMAIN, r.IS_PUBLIC AS IS_PUBLIC, r.IS_SSL AS IS_SSL" +
														"	FROM " +
														"		%s.RES r " +
														"		JOIN %s.RESOURCE_PROP prop " +
														"			ON  r.RESOURCE_ID=prop.RESOURCE_ID " +
														"			AND prop.NAME = ?";
	private String GET_ALL_MENUS = "SELECT RESOURCE_ID AS RESOURCE_ID, URL AS MENU_URL, NAME AS MENU_NAME, DISPLAY_ORDER AS DISPLAY_ORDER, IS_PUBLIC AS IS_PUBLIC FROM %s.RES WHERE RESOURCE_TYPE_ID = ?";
	private String GET_AUTH_MENU_BY_ID = "SELECT RESOURCE_ID AS RESOURCE_ID, URL AS MENU_URL, NAME AS MENU_NAME, DISPLAY_ORDER AS DISPLAY_ORDER FROM %s.RES WHERE RESOURCE_TYPE_ID = ? AND RESOURCE_ID = ?";
	
	@Override
	protected void initSqlStatements() {
		GET_ALL = String.format(GET_ALL, getSchemaName());
		GET_RESOURCE_DOMAINS_WITH_PATTERNS = String.format(GET_RESOURCE_DOMAINS_WITH_PATTERNS,  getSchemaName(), getSchemaName());
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
	public Set<AuthorizationDomain> getAuthorizationDomains(final Map<String, AuthorizationResource> resourceMap) {
		if(log.isDebugEnabled()) {
			log.debug(String.format("Query: %s", GET_RESOURCE_DOMAINS_WITH_PATTERNS));
		}
		return getJdbcTemplate().query(GET_RESOURCE_DOMAINS_WITH_PATTERNS, new Object[] {AuthorizationConstants.URL_PATTERN_PROPERTY}, new AuthorizationDomainRSE(resourceMap));
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
			return resource;
		}
		
	}
	
	private class AuthorizationDomainRSE implements ResultSetExtractor<Set<AuthorizationDomain>> {
		
		private Map<String, AuthorizationResource> resourceMap;
		
		private AuthorizationDomainRSE(final Map<String, AuthorizationResource> resourceMap) {
			this.resourceMap = resourceMap;
		}

		@Override
		public Set<AuthorizationDomain> extractData(ResultSet rs) throws SQLException, DataAccessException {
			
			/* need get and contains methods, which is why a Set is not used */
			final Map<AuthorizationDomain, AuthorizationDomain> domainSet = new HashMap<AuthorizationDomain, AuthorizationDomain>();
			while(rs.next()) {
				final AuthorizationResource resource = resourceMap.get(rs.getString("RESOURCE_ID"));
				final String pattern = StringUtils.trimToNull(StringUtils.lowerCase(rs.getString("PATTERN")));
				final String minAuthLevel = StringUtils.trimToNull(StringUtils.lowerCase(rs.getString("MIN_AUTH_LEVEL")));
				final String domain = StringUtils.trimToNull(StringUtils.lowerCase(rs.getString("DOMAIN")));
				final boolean isPublic = StringUtils.equalsIgnoreCase("y", rs.getString("IS_PUBLIC"));
				final boolean isSSL = StringUtils.equalsIgnoreCase("y", rs.getString("IS_SSL"));
				if(pattern != null && domain != null && resource != null) {
					AuthorizationDomain authDomain = new AuthorizationDomain();
					authDomain.setDomain(domain);
					authDomain.setMinAuthLevel(minAuthLevel);
					authDomain.setPublic(isPublic);
					authDomain.setSSL(isSSL);
					if(!domainSet.containsKey(authDomain)) {
						domainSet.put(authDomain, authDomain);
					} else {
						authDomain = domainSet.get(authDomain);
					}
					try {
						authDomain.addPattern(pattern, resource);
					} catch (InvalidPatternException e) {
						log.warn("Can't add uri pattern " + pattern, e);
					}
				}
			}
			return domainSet.keySet();
		}
		
	}
}
