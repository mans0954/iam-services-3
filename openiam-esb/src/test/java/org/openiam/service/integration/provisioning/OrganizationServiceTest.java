package org.openiam.service.integration.provisioning;


import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.res.service.ResourceDataService;
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

import java.util.Date;
import java.util.List;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class OrganizationServiceTest extends AbstractTestNGSpringContextTests {

	private static final Log log = LogFactory.getLog(OrganizationServiceTest.class);

	private User user = null;
	private Organization org = null;


	@BeforeClass
	public void _init() {
		user = createUser();
		org = createOrganization();
	}
	
	@AfterClass
	public void _destroy() {
		if (user != null)
			userServiceClient.removeUser(user.getId());
		if (org != null)
			organizationServiceClient.deleteOrganization(org.getId());
	}

	@Autowired
	@Qualifier("organizationServiceClient")
	private OrganizationDataService organizationServiceClient;

	@Autowired
	@Qualifier("languageServiceClient")
	protected LanguageWebService languageServiceClient;

	@Autowired
	@Qualifier("userServiceClient")
	protected UserDataWebService userServiceClient;

	@Autowired
	@Qualifier("resourceServiceClient")
	protected ResourceDataService resourceServiceClient;


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

	protected Organization createOrganization(){
		ResourceTypeSearchBean resTypeSearchBean = new ResourceTypeSearchBean();
		resTypeSearchBean.setKey("ADMIN_RESOURCE");
		ResourceType admResType = new ResourceType();
		List<ResourceType> admResTypeList = resourceServiceClient.findResourceTypes(resTypeSearchBean, 0, 1, getDefaultLanguage());
		Assert.assertNotEquals(admResTypeList.size(), 0, "Can't find resourceType : ADMIN_RESOURCE");
		if (admResTypeList.size() > 0) {
			admResType = admResTypeList.get(0);
		}

		Resource admRes = new Resource();
		admRes.setName(getRandomName());
		admRes.setDisplayName(getRandomName());
		admRes.setResourceType(admResType);
		Response resResponse = resourceServiceClient.saveResource(admRes, null);
		Assert.assertTrue(resResponse.isSuccess(), "Can't save new Admin Resource");

		Organization org = new Organization();
		org.setName(getRandomName());
		org.setDescription(getRandomName());
		org.setAlias(getRandomName());
		org.setAbbreviation(getRandomName());
		org.setCreateDate(new Date());
		org.setDomainName(getRandomName());
		org.setSelectable(true);
		org.setOrganizationTypeId("ORGANIZATION");
		org.setAdminResourceId((String)resResponse.getResponseValue());

		final Response orgResponse = organizationServiceClient.saveOrganization(org, null);
		Assert.assertTrue(orgResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", org, orgResponse));
		return organizationServiceClient.getOrganizationLocalized((String)orgResponse.getResponseValue(), null, getDefaultLanguage());
	}

	///IDMAPPS-2933,2934,2935
	@Test
	public void addUserToOrg() throws Exception {
		OrganizationSearchBean orgSearchBean = new OrganizationSearchBean();
		orgSearchBean.setKey(org.getId());
		List<Organization> orgList = organizationServiceClient.findBeans(orgSearchBean, null, 0, 1);
		Assert.assertTrue((orgList != null)&&(orgList.size() > 0), "Organization does not exists");

		orgList = organizationServiceClient.getOrganizationsForUserLocalized(user.getId(), null, 0, 1, getDefaultLanguage());
		Assert.assertFalse((orgList != null) && (orgList.size() < 1), "Organization already contains such User");

		Response orgAddResp = organizationServiceClient.addUserToOrg(org.getId(), user.getId());
		Assert.assertTrue(orgAddResp.isSuccess(), "Cann't add user to Organization");

		orgList = organizationServiceClient.getOrganizationsForUserLocalized(user.getId(), null, 0, 1, getDefaultLanguage());
		Assert.assertNotEquals(orgList.size(), 0, "Can't get Organization for User");

		Response orgDelResp = organizationServiceClient.removeUserFromOrg(org.getId(), user.getId());
		Assert.assertTrue(orgDelResp.isSuccess(), "Cann't del user from Organization");

		if (orgDelResp.isSuccess()) {
			orgList = organizationServiceClient.getOrganizationsForUserLocalized(user.getId(), null, 0, 1, getDefaultLanguage());
			Assert.assertNull(orgList, "Del user from Organization - success, but not delete fom DB");
		}


	}

}
