package org.openiam.am.srvc.service;

import org.apache.commons.lang.RandomStringUtils;
import org.openiam.am.srvc.dao.AuthLevelDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

@Transactional
@TransactionConfiguration(defaultRollback = true)
@ContextConfiguration(locations={"classpath:test-integration-environment.xml", 
								 "classpath:test-service-integration.xml"})
public class TestURIFederationServiceImpl extends AbstractTestNGSpringContextTests {

	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private LoginDAO loginDAO;
	
	@Autowired
	private ResourceDAO resourceDAO;
	
	@Autowired
	private ResourceTypeDAO resourceTypeDAO;
	
	@Autowired
	private ContentProviderDao contentProviderDAO;
	
	@Autowired
	private AuthorizationManagerService authManager;
	
	@Autowired
	private AuthLevelDao authLevelDAO;
	
	@Autowired
	private URIFederationService uriFederationService;
	
	@Test
	public void testCompilcatedPatterns() {
		final UserEntity userEntity = new UserEntity();
		userDao.save(userEntity);
		
		final LoginEntity login = new LoginEntity();
		login.setManagedSysId("0");
		login.setLogin(RandomStringUtils.randomAlphabetic(7));
		login.setUserId(userEntity.getId());
		loginDAO.save(login);
		
		final ResourceEntity cpResource = getCPResource();
		final ContentProviderEntity cpEntity = new ContentProviderEntity();
		cpEntity.setDomainPattern("www.testng.com");
		cpEntity.setMinAuthLevel(authLevelDAO.findById("PASSWORD_AUTH"));
		cpEntity.setName(RandomStringUtils.randomAlphabetic(7));
		cpEntity.setResource(cpResource);
		//cpEntity.setResourceId(cpResource.getResourceId());
		contentProviderDAO.save(cpEntity);
	}
	
	private ResourceEntity getURIResource() {
		final ResourceEntity uriRes = new ResourceEntity();
		uriRes.setResourceType(resourceTypeDAO.findById("URL_PATTERN"));
		uriRes.setName(RandomStringUtils.randomAlphabetic(7));
		return uriRes;
	}
	
	private ResourceEntity getCPResource() {
		final ResourceEntity uriRes = new ResourceEntity();
		uriRes.setResourceType(resourceTypeDAO.findById("CONTENT_PROVIDER"));
		uriRes.setName(RandomStringUtils.randomAlphabetic(7));
		return uriRes;
	}
}
