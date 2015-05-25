package org.openiam.service.integration.provisioning;


import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.pswd.service.ChallengeResponseService;
import org.openiam.idm.srvc.pswd.service.ChallengeResponseWebService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.idm.srvc.user.ws.UserResponse;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class UserCreateServiceTest extends AbstractTestNGSpringContextTests {

	private static final Log log = LogFactory.getLog(UserCreateServiceTest.class);

	private User user = null;
	private UserSearchBean userSearchBean = null;
	private Set<String> userIdSet = new HashSet<String>();


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
		for (String userId : userIdSet) {
			userServiceClient.removeUser(userId);
		}
	}

	@Autowired
	@Qualifier("languageServiceClient")
	protected LanguageWebService languageServiceClient;

	@Autowired
	@Qualifier("userServiceClient")
	protected UserDataWebService userServiceClient;

	@Autowired
	@Qualifier("roleServiceClient")
	protected RoleDataWebService roleServiceClient;

	@Autowired
	@Qualifier("provisionServiceClient")
	protected ProvisionService provisionServiceClient;

	@Autowired
	@Qualifier("loginServiceClient")
	protected LoginDataWebService loginServiceClient;

	@Autowired
	@Qualifier("challengeResponseServiceClient")
	protected ChallengeResponseWebService challengeResponseServiceClient;

	@Value("${openiam.default_managed_sys}")
	protected String defaultManagedSysId;





	protected String getDefaultManagedSystemId() {
		return defaultManagedSysId;
	}

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

	//Create a user with a predefined role
	///IDMAPPS-2908
	@Test(priority=1)
	public void createUserWithRole() {
		Role role = roleServiceClient.getRoleLocalized("1", null, getDefaultLanguage());
		role.setOperation(AttributeOperationEnum.ADD);
		Assert.assertNotNull(role, "Cann't find role with ID : END_USER");

		User user = new User();
		user.setFirstName(getRandomName());
		user.setLastName(getRandomName());
		user.setNotifyUserViaEmail(false);
		user.getRoles().add(role);

		Response wsResult = new Response();
		try {
			ProvisionUser pUser = new ProvisionUser(user);
			pUser.setOperation(AttributeOperationEnum.ADD);
			wsResult = provisionServiceClient.addUser(pUser);
			if (wsResult.isSuccess()) {
				userIdSet.add(((ProvisionUserResponse) wsResult).getUser().getId());
				User user2 = userServiceClient.getUserWithDependent(((ProvisionUserResponse) wsResult).getUser().getId(), null, true);
				Assert.assertNotNull(user, "Cann't find new user");

				userSearchBean.setKey(((ProvisionUserResponse) wsResult).getUser().getId());
				List<User> user3 = userServiceClient.findBeans(userSearchBean, 0, 1);
				if (user2 != null) {
					Assert.assertTrue(user3.get(0).getRoles().contains(role), "User hasn't role : END_USER");
				}
			}
		} catch (Exception e) {
			Assert.assertNull(e, "Cann't save new User");
		}


	}

	//Create a user with a predefined identity
	///IDMAPPS-2907
	@Test(priority=2)
	public void createUserWithIdentity() {
		Login login = new Login();
		login.setLogin(getRandomName());
		login.setManagedSysId(getDefaultManagedSystemId());

		User user = new User();
		user.setFirstName(getRandomName());
		user.setLastName(getRandomName());
		user.setNotifyUserViaEmail(false);
		user.addPrincipal(login);

		Response wsResult = new Response();
		try {
			ProvisionUser pUser = new ProvisionUser(user);
			pUser.setOperation(AttributeOperationEnum.ADD);
			wsResult = provisionServiceClient.addUser(pUser);
			if (wsResult.isSuccess()) {
				userIdSet.add(((ProvisionUserResponse) wsResult).getUser().getId());
				userSearchBean.setKey(((ProvisionUserResponse) wsResult).getUser().getId());
				List<User> user2 = userServiceClient.findBeans(userSearchBean, 0, 1);
				if (user2 != null) {
					Assert.assertEquals(user2.get(0).getPrincipalList().size(), 1, "User hasn't principal");
				}
			}
		} catch (Exception e) {
			Assert.assertNull(e, "Cann't save new User");
		}


	}

	//Reset the challenge questions for the user
	///IDMAPPS-2929
	@Test(priority=3)
	public void resetChallengeQuestion() {

		List<UserIdentityAnswer> answList = new ArrayList<UserIdentityAnswer>();

		UserIdentityAnswer answ1 = new UserIdentityAnswer();
		answ1.setQuestionId("200");
		answ1.setQuestionAnswer("answer 1");
		answ1.setUserId(user.getId());
		answList.add(answ1);

		UserIdentityAnswer answ2 = new UserIdentityAnswer();
		answ2.setQuestionId("201");
		answ2.setQuestionAnswer("answer 2");
		answ2.setUserId(user.getId());
		answList.add(answ2);

		UserIdentityAnswer answ3 = new UserIdentityAnswer();
		answ3.setQuestionId("202");
		answ3.setQuestionAnswer("answer 3");
		answ3.setUserId(user.getId());
		answList.add(answ3);

		challengeResponseServiceClient.saveAnswers(answList);

		IdentityAnswerSearchBean iasb = new IdentityAnswerSearchBean();
		iasb.setUserId(user.getId());
		iasb.setDeepCopy(false);

		try {
			answList = challengeResponseServiceClient.findAnswerBeans(iasb, null, 0, Integer.MAX_VALUE);
		} catch (Exception e) {
			Assert.assertNull(e, "Exception when try find answer for User");
		}

		if ((answList != null) && (answList.size() > 0)) {
			challengeResponseServiceClient.resetQuestionsForUser(user.getId());
		} else {
			Assert.assertTrue(false, "Cann't find new Answer for User");
		}

		try {
			answList = challengeResponseServiceClient.findAnswerBeans(iasb, null, 0, Integer.MAX_VALUE);
			Assert.assertNull(answList, "Cann't reset user Answers");
		} catch (Exception e) {
			Assert.assertNull(e, "Exception when try find answer for User");
		}
	}

}
