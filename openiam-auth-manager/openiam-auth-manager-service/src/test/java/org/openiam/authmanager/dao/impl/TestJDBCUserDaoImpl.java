package org.openiam.authmanager.dao.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openiam.authmanager.dao.UserDAO;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/*
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:authorizationManagerContext.xml","classpath:applicationContext.xml", "classpath:idmservice-Context.xml"})
public class TestJDBCUserDaoImpl {
	
	@Autowired
	@Qualifier("jdbcUserDao")
	private UserDAO userDAO;
	
	@Autowired
	@Qualifier("authorizationManagerService")
	private AuthorizationManagerService authorizationManagerService;
	
	@Test
	@Rollback
	public void testGetFullUser() {
		System.out.println(userDAO);
		System.out.println(authorizationManagerService);
	}
}
*/
