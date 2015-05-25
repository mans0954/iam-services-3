package org.openiam.service.integration.provisioning;


import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
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

import java.util.*;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class UserStatusServiceTest extends AbstractTestNGSpringContextTests {

	private static final Log log = LogFactory.getLog(UserStatusServiceTest.class);

	private User user = null;
	private UserSearchBean userSearchBean = null;


	@BeforeClass
	public void _init() {
		user = createUser();
		userSearchBean = new UserSearchBean();
		userSearchBean.setKey(user.getId());

	}
	
	@AfterClass
	public void _destroy() {
		if (user != null)
			userServiceClient.removeUser(user.getId());
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

	//Change Secondary status
	///IDMAPPS-2925, 2926
	@Test(priority=1)
	public void changeSecondaryStatus() {
		Response changeStatusResponse = userServiceClient.setSecondaryStatus(user.getId(), UserStatusEnum.DISABLED);
		Assert.assertTrue(changeStatusResponse.isSuccess(), "Cann't CHANGE SECONDARY STATUS to DISABLED");
		List<User> userList = userServiceClient.findBeans(userSearchBean, 0, 1);
		Assert.assertTrue((userList != null) && (userList.size() > 0), "Cann't find user");
		Assert.assertEquals(UserStatusEnum.DISABLED, userList.get(0).getSecondaryStatus(), "SECONDARY STATUS wasn't changed to DISABLED");

		changeStatusResponse = userServiceClient.setSecondaryStatus(user.getId(), null);
		Assert.assertTrue(changeStatusResponse.isSuccess(), "Cann't CHANGE SECONDARY STATUS to null");
		userList = userServiceClient.findBeans(userSearchBean, 0, 1);
		Assert.assertTrue((userList != null)&&(userList.size() > 0), "Cann't find user");
		Assert.assertNull(userList.get(0).getSecondaryStatus(), "SECONDARY STATUS wasn't changed to null");
	}

	//Change User status
	///IDMAPPS-2910, 2912
	@Test(priority=2)
	public void changeUserStatus() {
		Response changeStatusResponse = userServiceClient.deleteUser(user.getId());
		Assert.assertTrue(changeStatusResponse.isSuccess(), "Cann't CHANGE User STATUS to DELETED");
		List<User> userList = userServiceClient.findBeans(userSearchBean, 0, 1);
		Assert.assertTrue((userList != null) && (userList.size() > 0), "Cann't find user");
		Assert.assertEquals(UserStatusEnum.DELETED, userList.get(0).getStatus(), "User STATUS wasn't changed to DELETED");

		changeStatusResponse = userServiceClient.activateUser(user.getId());
		Assert.assertTrue(changeStatusResponse.isSuccess(), "Cann't CHANGE User STATUS to active");
		userList = userServiceClient.findBeans(userSearchBean, 0, 1);
		Assert.assertTrue((userList != null) && (userList.size() > 0), "Cann't find user");
		Assert.assertEquals(UserStatusEnum.ACTIVE, userList.get(0).getStatus(), "User STATUS wasn't changed to ACTIVE");

		changeStatusResponse = userServiceClient.deleteUser(user.getId());
		Assert.assertTrue(changeStatusResponse.isSuccess(), "Cann't CHANGE User STATUS to DELETED");
		userList = userServiceClient.findBeans(userSearchBean, 0, 1);
		Assert.assertTrue((userList != null) && (userList.size() > 0), "Cann't find user");
		Assert.assertEquals(UserStatusEnum.DELETED, userList.get(0).getStatus(), "User STATUS wasn't changed to DELETED");
	}

}
