package org.openiam.service.integration;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXrefId;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.am.srvc.searchbean.AuthProviderSearchBean;
import org.openiam.srvc.am.AuthProviderWebService;
import org.openiam.srvc.am.ContentProviderWebService;
import org.openiam.srvc.am.AuthenticationService;
import org.openiam.srvc.common.LanguageWebService;
import org.openiam.srvc.common.PolicyDataService;
import org.openiam.srvc.am.AuthorizationManagerWebService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.http.client.OpenIAMHttpClient;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.srvc.am.AccessRightDataService;
import org.openiam.srvc.audit.IdmAuditLogWebDataService;
import org.openiam.base.request.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.base.response.AuthenticationResponse;
import org.openiam.srvc.user.LoginDataWebService;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.srvc.am.GroupDataWebService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.srvc.common.MetadataWebService;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.srvc.idm.ManagedSystemWebService;
import org.openiam.srvc.idm.ProvisionConnectorWebService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyConstants;
import org.openiam.srvc.am.OrganizationDataService;
import org.openiam.srvc.am.OrganizationTypeDataService;
import org.openiam.srvc.common.PropertyValueWebService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.srvc.am.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.srvc.am.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.srvc.user.UserDataWebService;
import org.openiam.base.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
//@Import(TestConfg.class)
//@SpringApplicationConfiguration(IntegrationTestConfig.class)
public abstract class AbstractServiceTest extends AbstractTestNGSpringContextTests {

	protected ContentProvider cp = null;
	protected User user = null;
	
    @Autowired
    @Qualifier("policyServiceClient")
    protected PolicyDataService policyServiceClient;
    
    protected Policy getPasswordPolicy() {
    	final PolicySearchBean sb = new PolicySearchBean();
    	sb.setPolicyDefId(PolicyConstants.PASSWORD_POLICY);
    	return policyServiceClient.findBeans(sb, 0, 10).get(0);
    }
	
	@Autowired
	@Qualifier("languageServiceClient")
	protected LanguageWebService languageServiceClient;
	
	@Autowired
	@Qualifier("auditServiceClient")
	protected IdmAuditLogWebDataService auditLogService;
	
	@Autowired
	@Qualifier("managedSysServiceClient")
	protected ManagedSystemWebService managedSysServiceClient;

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
	@Qualifier("provisionConnectorWebServiceClient")
	protected ProvisionConnectorWebService provisionConnectorWebServiceClient;
	
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
	protected OrganizationTypeDataService organizationTypeClient;
	
	@Autowired
	@Qualifier("accessRightServiceClient")
	protected AccessRightDataService accessRightServiceClient;

	@Autowired
	@Qualifier("loginServiceClient")
	protected LoginDataWebService loginServiceClient;
	
	@Autowired
	@Qualifier("authServiceClient")
	protected AuthenticationService authServiceClient;
	
	@Autowired
	protected RestTemplate restTemplate;
	
	@Value("${openiam.service_host}")
	private String serviceHost;
	
	@Value("${org.openiam.auth.provider.type.totp.id}")
	private String totpAuthLevelId;
	
	@Autowired
	private OpenIAMHttpClient httpClient;
	
	protected Language defaultLanguage;
	
	protected MetadataType getRandomMetadataType() {
		final List<MetadataType> types = metadataServiceClient.findTypeBeans(null, 0, 10, getDefaultLanguage());
		Assert.assertTrue(CollectionUtils.isNotEmpty(types));
		return types.get(RandomUtils.nextInt(0, types.size()));
	}
	
	protected Date getMiddleDate(final Date startDate, final Date endDate) {
		if(startDate != null) {
			return DateUtils.addSeconds(new Date(), 10);
		} else if(endDate != null) {
			return DateUtils.addSeconds(new Date(), -10);
		} else {
			return null;
		}
	}
	
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
		rightIds.removeAll(rightsToIgnore());
		return rightIds;
	}
	
	protected Set<String> rightsToIgnore() {
		return Collections.EMPTY_SET;
	}
	
	protected Set<String> getAllRightIds() {
		final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, Integer.MAX_VALUE, getDefaultLanguage());
		final Set<String> rightIds = rights.stream().map(e -> e.getId()).collect(Collectors.toSet());
		rightIds.removeAll(rightsToIgnore());
		return rightIds;
	}

	
	protected String getString(final String key) {
		return propertyValuerServiceClient.getCachedValue(key, getDefaultLanguage());
	}
	
	protected String getDefaultManagedSystemId() {
		return getString("openiam.default_managed_sys");
	}
	
	protected interface CollectionOperation<T, S> {
		Set<S> get(T t);
		void set(T t, Set<S> set);
	}
	
	protected void assertSuccess(final Response response) {
		Assert.assertTrue(response.isSuccess());
	}
	
	protected void assertFailure(final Response response) {
		Assert.assertFalse(response.isSuccess());
	}
	
	protected void sleep(final long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			
		}
	}
	
	protected List<MetadataType> getMetadataTypesByGrouping(final MetadataTypeGrouping grouping) {
    	final MetadataTypeSearchBean searchBean = new MetadataTypeSearchBean();
    	searchBean.setGrouping(grouping);
    	searchBean.setActive(true);
        final List<MetadataType> types = metadataServiceClient.findTypeBeans(searchBean, 0, Integer.MAX_VALUE, getDefaultLanguage());
        return types;
    }
	
	protected void sleep(final int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected ContentProvider createContentProvider() {
		final ContentProvider cp = new ContentProvider();
		cp.setName(getRandomName());
		cp.setAuthCookieName(getRandomName());
		cp.setDomainPattern(getRandomName());
		cp.setAuthCookieDomain(cp.getDomainPattern());
		cp.setUrl(getRandomName());
		
		final AuthProviderSearchBean sb = new AuthProviderSearchBean();
		sb.setLinkableToContentProvider(true);
		sb.setDefaultAuthProvider(true);
		cp.setAuthProviderId(authProviderServiceClient.findAuthProviderBeans(sb, 0, 1).get(0).getId());
		cp.setUnavailable(false);
		
		final ContentProviderServer server = new ContentProviderServer();
		server.setServerURL(getRandomName());
		final Set<ContentProviderServer> serverSet = new HashSet<ContentProviderServer>();
		serverSet.add(server);
		cp.setServerSet(serverSet);
		
		final Set<AuthLevelGroupingContentProviderXref> groupingXrefs = new HashSet<AuthLevelGroupingContentProviderXref>();
		//for(final AuthLevelGrouping grouping : contentProviderServiceClient.getAuthLevelGroupingList()) {
		contentProviderServiceClient.getAuthLevelGroupingList().forEach((final AuthLevelGrouping grouping) -> {
			/* don't add totp, b/c SMS will already be added.  Error will be thrown if both SMS and TOTP are present */
			if(!grouping.getId().equals(totpAuthLevelId)) {
				final AuthLevelGroupingContentProviderXref xref = new AuthLevelGroupingContentProviderXref();
				final AuthLevelGroupingContentProviderXrefId id = new AuthLevelGroupingContentProviderXrefId();
				id.setGroupingId(grouping.getId());
				xref.setId(id);
				groupingXrefs.add(xref);
			}
		});
		//}
		cp.setGroupingXrefs(groupingXrefs);
		return cp;
	}
	
	protected interface EntityGenerator<T> {
		T generate();
	}
	
	protected void assertResponseCode(final Response response, final ResponseCode responseCode) {
		Assert.assertNotNull(responseCode);
		Assert.assertNotNull(response);
		Assert.assertEquals(response.getErrorCode(), responseCode, String.format("Expteded '%s'.  Got Response: '%s'", responseCode, response));
	}
	
	protected void login(final String userId) {
		final Login login = loginServiceClient.getPrimaryIdentity(userId).getPrincipal();
		Assert.assertNotNull(login);
		final String password = (String)loginServiceClient.decryptPassword(userId, login.getPassword()).getResponseValue();
		Assert.assertTrue(StringUtils.isNotBlank(password));
		
		final AuthenticationRequest authenticatedRequest = new AuthenticationRequest();
		authenticatedRequest.setPassword(password);
		authenticatedRequest.setPrincipal(login.getLogin());
		authenticatedRequest.setLanguageId(getDefaultLanguage().getId());
		try {
			authenticatedRequest.setNodeIP(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			
		}
		final AuthenticationResponse authenticationResponse = authServiceClient.login(authenticatedRequest);
		Assert.assertNotNull(authenticationResponse);
		Assert.assertEquals(authenticationResponse.getStatus(), ResponseStatus.SUCCESS);
	}
	
	protected User createUser() {
		return createUser(null);
	}
	
	protected User createUser(final String email) {
		User user = new User();
		user.setFirstName(getRandomName());
		user.setLastName(getRandomName());
		user.setLogin(getRandomName(10));
		user.setPassword(getRandomName());
		user.setNotifyUserViaEmail(false);
		
		if(StringUtils.isNotBlank(email)) {
			user.setEmailAddresses(new HashSet<EmailAddress>());
			final EmailAddress addr = new EmailAddress();
			addr.setDescription(getRandomName());
			addr.setName(getRandomName());
			addr.setMdTypeId("HOME_EMAIL");
			addr.setEmailAddress(email);
			user.getEmailAddresses().add(addr);
		}

		final Login login = new Login();
		login.setLogin(getRandomName(10));
		login.setManagedSysId(getDefaultManagedSystemId());
		user.setPrincipalList(Arrays.asList(new Login[] {login}));
		
		final UserResponse userResponse = userServiceClient.saveUserInfo(user, null);
		Assert.assertTrue(userResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", user, userResponse));
		return userServiceClient.getUserWithDependent(userResponse.getUser().getId(), true);
	}
	
	protected Language getDefaultLanguage() {
		if(defaultLanguage == null) {
			final LanguageSearchBean searchBean = new LanguageSearchBean();
			searchBean.addKey("1");
			defaultLanguage = languageServiceClient.findBeans(searchBean, 0, 1, null).get(0);
		}
		return defaultLanguage;
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
	
	protected static String getRandomNameStatic() {
		return RandomStringUtils.randomAlphanumeric(5);
	}
	
	protected String getRandomName() {
		return getRandomName(5);
	}
	
	protected String encode(final String param) {
		if(StringUtils.isNotBlank(param)) {
			try {
				return URLEncoder.encode(param, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		} else {
			return "";
		}
	}
	
	protected String getESBRestfulURL(final String path) {
		return new StringBuilder(serviceHost).append(path).toString();
	}
	
	protected void refreshAuthorizationManager() {
		final String endpoint = getESBRestfulURL("/openiam-esb/authmanager/refresh");
		try {
			httpClient.getResponse(new URL(endpoint));
			Thread.sleep(500L);
		} catch (Exception e) {
			logger.error("Can't refresh auth manager", e);
			throw new RuntimeException(e);
		}
	}
	
	protected void refreshContentProviderManager() {
		final String endpoint = getESBRestfulURL("/openiam-esb/contentprovider/refresh");
		try {
			httpClient.getResponse(new URL(endpoint));
			Thread.sleep(3000L);
		} catch (Exception e) {
			logger.error("Can't refresh auth manager", e);
			throw new RuntimeException(e);
		}
	}
	
	
	protected Group createGroup() {
		Group group = new Group();
		group.setName(getRandomName());
		Response wsResponse = groupServiceClient.saveGroup(group);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", group, wsResponse));
		group = groupServiceClient.getGroup((String)wsResponse.getResponseValue());
		
		if(user != null) {
			final ApproverAssociation association = new ApproverAssociation();
			association.setApproverEntityId(user.getId());
			association.setApproverEntityType(AssociationType.USER);
			association.setAssociationEntityId(group.getId());
			association.setAssociationType(AssociationType.GROUP);
			association.setTestRequest(true);
			wsResponse = managedSysServiceClient.saveApproverAssociation(association);
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		}
		return group;
	}
	
	protected Organization createOrganization() {
		final List<MetadataType> types = metadataServiceClient.findTypeBeans(null, 0, 10, getDefaultLanguage());
		Organization organization = new Organization();
		organization.setOrganizationTypeId(organizationTypeClient.findBeans(null, 0, 1, null).get(0).getId());
		organization.setName(getRandomName());
		organization.setMdTypeId(types.get(RandomUtils.nextInt(0, types.size())).getId());
		Response wsResponse = organizationServiceClient.saveOrganization(organization);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", organization, wsResponse));
		organization = organizationServiceClient.getOrganizationLocalized((String)wsResponse.getResponseValue(), getDefaultLanguage());
		
		if(user != null) {
			final ApproverAssociation association = new ApproverAssociation();
			association.setApproverEntityId(user.getId());
			association.setApproverEntityType(AssociationType.USER);
			association.setAssociationEntityId(organization.getId());
			association.setAssociationType(AssociationType.ORGANIZATION);
			association.setTestRequest(true);
			wsResponse = managedSysServiceClient.saveApproverAssociation(association);
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		}
		return organization;
	}
	
	protected Resource createResource() {
		Resource resource = new Resource();
		final ResourceTypeSearchBean resourceTypeSearchBean = new ResourceTypeSearchBean();
		resourceTypeSearchBean.setSupportsHierarchy(true);
		resource.setResourceType(resourceDataService.findResourceTypes(resourceTypeSearchBean, 0, 1).get(0));
		resource.setName(getRandomName());
		Response wsResponse = resourceDataService.saveResource(resource);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", resource, wsResponse));
		resource = resourceDataService.getResource((String)wsResponse.getResponseValue());
		
		if(user != null) {
			final ApproverAssociation association = new ApproverAssociation();
			association.setApproverEntityId(user.getId());
			association.setApproverEntityType(AssociationType.USER);
			association.setAssociationEntityId(resource.getId());
			association.setAssociationType(AssociationType.RESOURCE);
			association.setTestRequest(true);
			wsResponse = managedSysServiceClient.saveApproverAssociation(association);
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		}
		return resource;
	}
	
	protected Role createRole() {
		Role role = new Role();
		role.setName(getRandomName());
		Response wsResponse = roleServiceClient.saveRole(role);
		Assert.assertTrue(wsResponse.isSuccess(), String.format("Could not save %s.  Reason: %s", role, wsResponse));
		role = roleServiceClient.getRole((String)wsResponse.getResponseValue());
		
		if(user != null) {
			final ApproverAssociation association = new ApproverAssociation();
			association.setApproverEntityId(user.getId());
			association.setApproverEntityType(AssociationType.USER);
			association.setAssociationEntityId(role.getId());
			association.setAssociationType(AssociationType.ROLE);
			association.setTestRequest(true);
			wsResponse = managedSysServiceClient.saveApproverAssociation(association);
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		}
		return role;
	}
	
	protected String getRequestorId() {
		return "3000";
	}
}
