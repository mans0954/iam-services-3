package org.openiam.service.integration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.runner.RunWith;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXrefId;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.am.srvc.ws.AuthProviderWebService;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.config.TestConfg;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.access.ws.AccessRightDataService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.org.service.OrganizationTypeDataService;
import org.openiam.idm.srvc.property.ws.PropertyValueWebService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.idm.srvc.user.ws.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
//@Import(TestConfg.class)
@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public abstract class AbstractServiceTest extends AbstractTestNGSpringContextTests {

	@Autowired
	@Qualifier("languageServiceClient")
	protected LanguageWebService languageServiceClient;

	@Autowired
	@Qualifier("userServiceClient")
	protected UserDataWebService userServiceClient;
	
	@Autowired
	@Qualifier("authProviderServiceClient")
	protected AuthProviderWebService authProviderServiceClient;
	
	@Autowired
    @Qualifier("metadataServiceClient")
    protected MetadataWebService metadataServiceClient;
	
	@Autowired
	@Qualifier("contentProviderServiceClient")
	protected ContentProviderWebService contentProviderServiceClient;

	@Autowired
	@Qualifier("authorizationManagerServiceClient")
	protected AuthorizationManagerWebService authorizationManagerServiceClient;
	
	@Autowired
	@Qualifier("propertyValuerServiceClient")
	protected PropertyValueWebService propertyValuerServiceClient;
	

	@Autowired
	@Qualifier("groupServiceClient")
	protected GroupDataWebService groupServiceClient;
	
	@Autowired
	@Qualifier("roleServiceClient")
	protected RoleDataWebService roleServiceClient;
	

	@Autowired
	@Qualifier("resourceServiceClient")
    protected ResourceDataService resourceDataService;
	
	@Autowired
	@Qualifier("organizationServiceClient")
	protected OrganizationDataService organizationServiceClient;
	
	@Autowired
	@Qualifier("organizationTypeClient")
	private OrganizationTypeDataService organizationTypeClient;
	
	@Autowired
	@Qualifier("accessRightServiceClient")
	protected AccessRightDataService accessRightServiceClient;

	@Autowired
	@Qualifier("loginServiceClient")
	protected LoginDataWebService loginServiceClient;
	
	@Autowired
	@Qualifier("authServiceClient")
	protected AuthenticationService authServiceClient;
	
	
	protected Set<String> getRightIdsNotIn(final Set<String> rightIds) {
		return accessRightServiceClient.findBeans(null, 0, Integer.MAX_VALUE, getDefaultLanguage())
									   .stream()
									   .map(e -> e.getId())
									   .filter(e -> !rightIds.contains(e))
									   .collect(Collectors.toSet());
	}

	protected Set<String> getRightIds() {
		final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, Integer.MAX_VALUE, getDefaultLanguage());
		final Set<String> rightIds = rights.subList(0, rights.size() / 2).stream().map(e -> e.getId()).collect(Collectors.toSet());
		return rightIds;
	}
	
	protected Set<String> getAllRightIds() {
		final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, Integer.MAX_VALUE, getDefaultLanguage());
		final Set<String> rightIds = rights.stream().map(e -> e.getId()).collect(Collectors.toSet());
		return rightIds;
	}

	
	protected String getString(final String key) {
		return propertyValuerServiceClient.getCachedValue(key, getDefaultLanguage());
	}
	
	protected String getDefaultManagedSystemId() {
		return getString("openiam.default_managed_sys");
	}
	
	protected interface CollectionOperation<T, S> {
		public Set<S> get(T t);
		public void set(T t, Set<S> set);
	}
	
	protected void assertSuccess(final Response response) {
		Assert.assertTrue(response.isSuccess());
	}
	
	protected List<MetadataType> getMetadataTypesByGrouping(final MetadataTypeGrouping grouping) {
    	final MetadataTypeSearchBean searchBean = new MetadataTypeSearchBean();
    	searchBean.setGrouping(grouping);
    	searchBean.setActive(true);
        final List<MetadataType> types = metadataServiceClient.findTypeBeans(searchBean, 0, Integer.MAX_VALUE, getDefaultLanguage());
        return types;
    }
	
	protected ContentProvider createContentProvider() {
		final ContentProvider cp = new ContentProvider();
		cp.setName(getRandomName());
		cp.setAuthCookieName(getRandomName());
		cp.setDomainPattern(getRandomName());
		cp.setAuthCookieDomain(cp.getDomainPattern());
		cp.setUrl(getRandomName());
		cp.setAuthProviderId(authProviderServiceClient.findAuthProviderBeans(null, 0, 1).get(0).getId());
		cp.setUnavailable(false);
		
		final ContentProviderServer server = new ContentProviderServer();
		server.setServerURL(getRandomName());
		final Set<ContentProviderServer> serverSet = new HashSet<ContentProviderServer>();
		serverSet.add(server);
		cp.setServerSet(serverSet);
		
		final Set<AuthLevelGroupingContentProviderXref> groupingXrefs = new HashSet<AuthLevelGroupingContentProviderXref>();
		//for(final AuthLevelGrouping grouping : contentProviderServiceClient.getAuthLevelGroupingList()) {
		contentProviderServiceClient.getAuthLevelGroupingList().forEach((final AuthLevelGrouping grouping) -> {
			final AuthLevelGroupingContentProviderXref xref = new AuthLevelGroupingContentProviderXref();
			final AuthLevelGroupingContentProviderXrefId id = new AuthLevelGroupingContentProviderXrefId();
			id.setGroupingId(grouping.getId());
			xref.setId(id);
			groupingXrefs.add(xref);
		});
		//}
		cp.setGroupingXrefs(groupingXrefs);
		return cp;
	}
	
	protected interface EntityGenerator<T> {
		public T generate();
	}
	
	protected void assertResponseCode(final Response response, final ResponseCode responseCode) {
		Assert.assertNotNull(responseCode);
		Assert.assertNotNull(response);
		Assert.assertEquals(response.getErrorCode(), responseCode, String.format("Expteded '%s'.  Got Response: '%s'", responseCode, response));
	}
	
	protected User createUser() {
		User user = new User();
		user.setFirstName(getRandomName());
		user.setLastName(getRandomName());
		user.setLogin(getRandomName());
		user.setPassword(getRandomName());
		user.setNotifyUserViaEmail(false);

		final Login login = new Login();
		login.setLogin(getRandomName());
		login.setManagedSysId(getDefaultManagedSystemId());
		user.setPrincipalList(Arrays.asList(new Login[] {login}));
		
		final UserResponse userResponse = userServiceClient.saveUserInfo(user, null);
		Assert.assertTrue(userResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", user, userResponse));
		return userServiceClient.getUserWithDependent(userResponse.getUser().getId(), null, true);
	}
	
	protected Language getDefaultLanguage() {
		final LanguageSearchBean searchBean = new LanguageSearchBean();
		searchBean.setKey("1");
		return languageServiceClient.findBeans(searchBean, 0, 1, null).get(0);
	}
	
	protected final Map<String, LanguageMapping> generateRandomLanguageMapping() {
		Map<String, LanguageMapping> retVal = new HashMap<>();
		for(final Language language : getAllLanguages()) {
			final LanguageMapping mapping = new LanguageMapping();
			mapping.setLanguageId(language.getId());
			mapping.setValue(getRandomName());
			retVal.put(language.getId(), mapping);
		}
		return retVal;
	}
	
	protected final List<Language> getAllLanguages() {
		return languageServiceClient.findBeans(null, 0, Integer.MAX_VALUE, null);
	}
	
	protected String getRandomName(final int count) {
		return RandomStringUtils.randomAlphanumeric(count);
	}
	
	protected String getRandomName() {
		return getRandomName(5);
	}
	
	protected void refreshAuthorizationManager() {
		authorizationManagerServiceClient.refreshCache();
	}
	
	
	protected Group createGroup() {
		Group group = new Group();
		group.setName(getRandomName());
		final Response wsResponse = groupServiceClient.saveGroup(group, null);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", group, wsResponse));
		group = groupServiceClient.getGroup((String)wsResponse.getResponseValue(), null);
		return group;
	}
	
	protected Organization createOrganization() {
		Organization organization = new Organization();
		organization.setOrganizationTypeId(organizationTypeClient.findBeans(null, 0, 1, null).get(0).getId());
		organization.setName(getRandomName());
		final Response wsResponse = organizationServiceClient.saveOrganization(organization, null);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", organization, wsResponse));
		organization = organizationServiceClient.getOrganizationLocalized((String)wsResponse.getResponseValue(), null, getDefaultLanguage());
		return organization;
	}
	
	protected Resource createResource() {
		Resource resource = new Resource();
		final ResourceTypeSearchBean resourceTypeSearchBean = new ResourceTypeSearchBean();
		resourceTypeSearchBean.setSupportsHierarchy(true);
		resource.setResourceType(resourceDataService.findResourceTypes(resourceTypeSearchBean, 0, 1, null).get(0));
		resource.setName(getRandomName());
		final Response wsResponse = resourceDataService.saveResource(resource, null);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", resource, wsResponse));
		resource = resourceDataService.getResource((String)wsResponse.getResponseValue(), getDefaultLanguage());
		return resource;
	}
	
	protected Role createRole() {
		Role role = new Role();
		role.setName(getRandomName());
		final Response wsResponse = roleServiceClient.saveRole(role, null);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", role, wsResponse));
		role = roleServiceClient.getRoleLocalized((String)wsResponse.getResponseValue(), null, getDefaultLanguage());
		return role;
	}
	
	protected String getRequestorId() {
		return "3000";
	}
}
