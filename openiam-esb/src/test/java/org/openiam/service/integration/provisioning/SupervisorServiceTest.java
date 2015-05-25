package org.openiam.service.integration.provisioning;


import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.idm.srvc.user.ws.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class SupervisorServiceTest extends AbstractTestNGSpringContextTests {

	private static final Log log = LogFactory.getLog(SupervisorServiceTest.class);

	private User user = null;
	private User userSuper = null;


	@BeforeClass
	public void _init() {
		user = createUser();
		userSuper = createUser();
	}
	
	@AfterClass
	public void _destroy() {
		if (user != null)
			userServiceClient.removeUser(user.getId());
		if (userSuper != null)
			userServiceClient.removeUser(userSuper.getId());
	}

	@Autowired
	@Qualifier("languageServiceClient")
	protected LanguageWebService languageServiceClient;

	@Autowired
	@Qualifier("userServiceClient")
	protected UserDataWebService userServiceClient;


	protected User createUser() {
		User user = new User();
		user.setFirstName(getRandomName());
		user.setLastName(getRandomName());
		user.setLogin(getRandomName());
		user.setPassword(getRandomName());
		user.setNotifyUserViaEmail(false);
		final UserResponse userResponse = userServiceClient.saveUserInfo(user, null);
		Assert.assertTrue(userResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", user, userResponse));
		return userServiceClient.getUserWithDependent(userResponse.getUser().getId(), null, true);
	}

	private String getRandomName() {
		return getRandomName(5);
	}

	private String getRandomName(final int count) {
		return RandomStringUtils.randomAlphanumeric(count);
	}

	protected Language getDefaultLanguage() {
		final LanguageSearchBean searchBean = new LanguageSearchBean();
		searchBean.setKey("1");
		return languageServiceClient.findBeans(searchBean, 0, 1, null).get(0);
	}

	///IDMAPPS-2930
	@Test(priority=1)
	public void addSupervisor() {
		Response supResponse = userServiceClient.addSuperior(userSuper.getId(), user.getId(), null);
		Assert.assertTrue(supResponse.isSuccess(), "Cann't ADD supervisor");
	}

	///IDMAPPS-2931, 2932
	@Test(priority=2)
	public void getDelSupervisor() {
		List<User> getSupList = userServiceClient.getSuperiors(user.getId(), 0, 1);
		Assert.assertTrue((getSupList != null) && (getSupList.get(0).equals(userSuper)), "Cann't get Supervisor");

		Response delResponse = userServiceClient.removeSuperior(userSuper.getId(), user.getId());
		Assert.assertTrue(delResponse.isSuccess(), "Cann't del Supervisor");

		if (delResponse.isSuccess()) {
			getSupList = userServiceClient.getSuperiors(user.getId(), 0, 1);
			Assert.assertFalse((getSupList != null) && (getSupList.get(0).equals(userSuper)), "Supervisor was deleted - success, but it find in DB");
		}
	}

}
