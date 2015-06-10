package org.openiam.service.integration;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.runner.RunWith;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXrefId;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.ws.AuthProviderWebService;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.lang.service.LanguageWebService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.idm.srvc.user.ws.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public abstract class AbstractServiceTest extends AbstractTestNGSpringContextTests {

	@Autowired
	@Qualifier("languageServiceClient")
	protected LanguageWebService languageServiceClient;

	@Autowired
	@Qualifier("userServiceClient")
	protected UserDataWebService userServiceClient;

    @Autowired
    @Qualifier("loginServiceClient")
    protected LoginDataWebService loginServiceClient;
	
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

	@Value("${openiam.default_managed_sys}")
	protected String defaultManagedSysId;
//	@Autowired
//	@Qualifier("propertyValuerServiceClient")
//	protected PropertyValueWebService propertyValuerServiceClient;
//
//	protected String getString(final String key) {
//		return propertyValuerServiceClient.getCachedValue(key, getDefaultLanguage());
//	}
	
	protected String getDefaultManagedSystemId() {
		return defaultManagedSysId;
	}
	
	protected interface CollectionOperation<T, S> {
		public Set<S> get(T t);
		public void set(T t, Set<S> set);
	}
	
	protected List<MetadataType> getMetadataTypesByGrouping(final MetadataTypeGrouping grouping) {
    	final MetadataTypeSearchBean searchBean = new MetadataTypeSearchBean();
    	searchBean.setGrouping(grouping);
    	searchBean.setActive(true);
        final List<MetadataType> types = metadataServiceClient.findTypeBeans(searchBean, 0, Integer.MAX_VALUE, getDefaultLanguage());
        return types;
    }
	
	//DO NOT MERGE INTO DEVELOPMENT!!
	protected ContentProvider createContentProvider() {
		final ContentProvider cp = new ContentProvider();
		cp.setName(getRandomName());
//		cp.setAuthCookieName(getRandomName());
		cp.setDomainPattern(getRandomName());
//		cp.setAuthCookieDomain(cp.getDomainPattern());
		cp.setUrl(getRandomName());
//		cp.setAuthProviderId(authProviderServiceClient.findAuthProviderBeans(null, 0, 1).get(0).getId());
		cp.setManagedSysId("0");
		
		/*
		 * ONLY required in 4.0 when you create a CP. In 3.x, you first create a CP, then a Server.
		final ContentProviderServer server = new ContentProviderServer();
		server.setServerURL(getRandomName());
		final Set<ContentProviderServer> serverSet = new HashSet<ContentProviderServer>();
		serverSet.add(server);
		cp.setServerSet(serverSet);
		*/
		
		final Set<AuthLevelGroupingContentProviderXref> groupingXrefs = new HashSet<AuthLevelGroupingContentProviderXref>();
		//for(final AuthLevelGrouping grouping : contentProviderServiceClient.getAuthLevelGroupingList()) {
		List<AuthLevelGrouping> authLevelGroupingList = contentProviderServiceClient.getAuthLevelGroupingList();

		if(CollectionUtils.isNotEmpty(authLevelGroupingList)){
			for(AuthLevelGrouping grouping: authLevelGroupingList){
				final AuthLevelGroupingContentProviderXref xref = new AuthLevelGroupingContentProviderXref();
				final AuthLevelGroupingContentProviderXrefId id = new AuthLevelGroupingContentProviderXrefId();
				id.setGroupingId(grouping.getId());
				xref.setId(id);
				groupingXrefs.add(xref);
			}
		}
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
		return languageServiceClient.findBeans(new LanguageSearchBean(), 0, Integer.MAX_VALUE, null);
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
}
