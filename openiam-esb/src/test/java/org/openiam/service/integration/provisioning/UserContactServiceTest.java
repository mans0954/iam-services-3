package org.openiam.service.integration.provisioning;


import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.continfo.dto.Phone;
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

import java.util.List;

import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class UserContactServiceTest extends AbstractTestNGSpringContextTests {

	private static final Log log = LogFactory.getLog(UserContactServiceTest.class);

	private User user = null;
	private UserSearchBean userSearchBean = null;


	@BeforeClass
	public void _init() {
		user = createUser();

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

	//Phone
	///IDMAPPS-2938, IDMAPPS-2941, IDMAPPS-2944
	@Test(priority=1)
	public void Phones() {

		//********************** ADD Phones *************************
		Phone phone = new Phone();
		phone.setName("phone 1");
		phone.setDescription("Description phone 1");
		phone.setPhoneNbr("111-11-11");
		phone.setAreaCd("a");
		phone.setOperation(AttributeOperationEnum.ADD);
		phone.setIsActive(true);
		phone.setIsDefault(true);
		phone.setParentId(user.getId());
		phone.setMetadataTypeId("HOME_PHONE");

		Response phoneResp = userServiceClient.addPhone(phone);
		Assert.assertTrue(phoneResp.isSuccess(), "Cann't add first phone to user");

		phone = new Phone();
		phone.setName("phone 2");
		phone.setDescription("Description phone 2");
		phone.setPhoneNbr("222-22-22");
		phone.setAreaCd("a");
		phone.setOperation(AttributeOperationEnum.ADD);
		phone.setIsActive(true);
		phone.setParentId(user.getId());
		phone.setMetadataTypeId("OFFICE_PHONE");

		phoneResp = userServiceClient.addPhone(phone);
		Assert.assertTrue(phoneResp.isSuccess(), "Cann't add second phone  to user");

		//********************** Get Phones *************************
		List<Phone> phoneList = userServiceClient.getPhoneListByPage(user.getId(), Integer.MAX_VALUE, 0);
		Assert.assertTrue((phoneList != null) && (phoneList.size() == 2), "Cann't find all User's phones");
		int count = userServiceClient.getNumOfPhonesForUser(user.getId());
		Assert.assertEquals(count, 2, "Wrong Phone count");



		//********************** Delete Phones *************************
		//Response phoneDelResp = null;
		//for (Phone ph : phoneList) {
		//	phoneDelResp = userServiceClient.removePhone(ph.getPhoneId());
		//	Assert.assertTrue(phoneDelResp.isSuccess(), "Cann't delete phone");
		//}
		//phoneList = userServiceClient.getPhoneListByPage(user.getId(), Integer.MAX_VALUE, 0);
		//Assert.assertTrue((phoneList == null) || (phoneList.size() == 0), "Cann't delete User's phones");

	}

}
