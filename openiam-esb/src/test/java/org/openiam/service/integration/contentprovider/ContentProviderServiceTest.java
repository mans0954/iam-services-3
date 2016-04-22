package org.openiam.service.integration.contentprovider;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.sql.Insert;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXrefId;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.ws.AuthProviderWebService;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.meta.dto.TemplateRequest;
import org.openiam.idm.srvc.meta.ws.MetadataElementTemplateWebService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openiam.idm.srvc.meta.dto.PageTempate;

public class ContentProviderServiceTest extends AbstractContentProviderServiceTest<ContentProvider, ContentProviderSearchBean> {
	
	 @Value("${org.openiam.selfservice.password.authlevel.id}")
	 private String passwordAuthLevelId;
	 
	 @Autowired
	 @Qualifier("metadataTemplateServiceClient")
	 private MetadataElementTemplateWebService metadataTemplateServiceClient;
	
	@Test
	public void testErrorCodes() {
		
		ContentProvider cp = createBean();
		cp.setName(null);
		Response response = save(cp);
		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_NAME_NOT_SET);
		
		cp = createBean();
		cp.setDomainPattern(null);
		response = save(cp);
		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET);
		
		cp = createBean();
		cp.setServerSet(null);
		response = save(cp);
		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_SERVER_REQUIRED);
		
		cp = createBean();
		cp.getServerSet().iterator().next().setServerURL(null);
		response = save(cp);
		assertResponseCode(response, ResponseCode.SERVER_URL_NOT_SET);
		
		cp = createBean();
		cp.setAuthCookieDomain(null);
		response = save(cp);
		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_COOKIE_DOMAIN_REQUIRED);
		
		cp = createBean();
		cp.setAuthCookieName(null);
		response = save(cp);
		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_COOKIE_NAME_REQUIRED);
		
		cp = createBean();
		cp.setGroupingXrefs(null);
		response = save(cp);
		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET);
		
		cp = createBean();
		cp.setDomainPattern(getRandomName());
		response = save(cp);
		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_COOKIE_DOMAIN_NOT_SUBSTR_OF_DOMAIN_PATTERN);
		
		cp = createBean();
		cp.setAuthProviderId(null);
		response = save(cp);
		assertResponseCode(response, ResponseCode.AUTH_PROVIDER_NOT_SET);
		
		cp = createBean();
		cp.setAuthProviderId(getRandomName());
		response = save(cp);
		assertResponseCode(response, ResponseCode.AUTH_PROVIDER_NOT_SET);
		
		final AuthProviderSearchBean sb = new AuthProviderSearchBean();
		sb.setLinkableToContentProvider(false);
		final List<AuthProvider> authProviders = authProviderServiceClient.findAuthProviderBeans(sb, 0, Integer.MAX_VALUE);
		if(CollectionUtils.isNotEmpty(authProviders)) {
			cp.setAuthProviderId(authProviders.get(0).getId());
			response = save(cp);
			assertResponseCode(response, ResponseCode.AUTH_PROVIDER_NOT_LINKABLE);
		}
		
		cp = createBean();
		cp.setUnavailable(true);
		response = save(cp);
		assertResponseCode(response, ResponseCode.UNAVAILABLE_URL_REQUIRED);
		
	}
	
	private void addServers(final int howMany, final Set<ContentProviderServer> serverSet) {
		IntStream.range(0, howMany).forEach( (int nbr) -> {
			final ContentProviderServer server = new ContentProviderServer();
			server.setServerURL(getRandomName());
			serverSet.add(server);
		});
	}
	
	private ContentProvider assertContentProviderServer(final ContentProvider cp, int size) {
		final Response response = saveAndAssert(cp);
		final ContentProvider serviceObject = get((String)response.getResponseValue());
		Assert.assertNotNull(serviceObject);
		Assert.assertNotNull(serviceObject.getServerSet());
		Assert.assertTrue(serviceObject.getServerSet().size() == size);
		return serviceObject;
	}
	
	private ContentProvider assertGroupings(final ContentProvider cp, int size) {
		final Response response = saveAndAssert(cp);
		final ContentProvider serviceObject = get((String)response.getResponseValue());
		Assert.assertNotNull(serviceObject);
		Assert.assertNotNull(serviceObject.getGroupingXrefs());
		Assert.assertTrue(serviceObject.getGroupingXrefs().size() == size);
		return serviceObject;
	}
	
	@Test
	public void testGroupings() {
		ContentProvider cp = null;
		try {
			cp = createBean();
			cp = assertGroupings(cp, cp.getGroupingXrefs().size());
			final Iterator<AuthLevelGroupingContentProviderXref> it1 = cp.getGroupingXrefs().iterator();
			IntStream.range(0, 1).forEach( i -> {
				it1.next();
				it1.remove();
			});
			cp = assertGroupings(cp, cp.getGroupingXrefs().size());
		} finally {
			if(cp != null && cp.getId() != null) {
				delete(cp);
			}
		}
	}
	
	@Test
	public void testSetup() {
		Assert.assertTrue(contentProviderServiceClient.setupApplication(null).isFailure());
		
		ContentProvider provider = null;
		try {
			provider = super.createContentProvider();
			provider.setName(getRandomName());
			final Response wsResponse = contentProviderServiceClient.setupApplication(provider);
			refreshContentProviderManager();
			refreshAuthorizationManager();
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess(), String.format("Response: %s", wsResponse));
			provider = get((String)wsResponse.getResponseValue());
			Assert.assertNotNull(provider);
			
			provider.getPatternSet().stream().filter(e -> 
				StringUtils.startsWithIgnoreCase(e.getPattern(), "/selfservice/selfRegistration") ||
				StringUtils.startsWithIgnoreCase(e.getPattern(), "/selfservice/newUser") ||
				StringUtils.startsWithIgnoreCase(e.getPattern(), "/selfservice/editUser")
			).forEach(e -> {
				final TemplateRequest templateRequest = new TemplateRequest();
				templateRequest.setLanguageId(getDefaultLanguage().getId());
				//templateRequest.setRequestURI(URIUtils.getRequestURL(request));
				templateRequest.setPatternId(e.getId());
				templateRequest.setTargetObjectId("3000");
				final PageTempate template = metadataTemplateServiceClient.getTemplate(templateRequest);
				Assert.assertNotNull(template);
				Assert.assertTrue(StringUtils.isNotBlank(template.getTemplateId()));
				
/*
 the current UI Field size is based on # of elemtns in defualt.page.template.fields.json
*/

				Assert.assertTrue(template.getUiFields() != null && template.getUiFields().size() == 3);
			});
			
/*
 b/c default patterns were created in setup
*/

			Assert.assertTrue(CollectionUtils.isNotEmpty(provider.getPatternSet()));
		} finally {
			if(provider != null && StringUtils.isNotBlank(provider.getId())) {
				delete(provider);
			}
		}
	}
	
	@Test
	public void testServers() {
		ContentProvider cp = null;
		try {
			cp = createBean();
			cp.setServerSet(null);
			Set<ContentProviderServer> serverSet = new HashSet<ContentProviderServer>();
			
/*
 add 4
*/

			addServers(4, serverSet);
			cp.setServerSet(serverSet);
			cp = assertContentProviderServer(cp, serverSet.size());
			serverSet = cp.getServerSet();
			
/*
 delete all but 1
*/

			final Iterator<ContentProviderServer> it1 = serverSet.iterator();
			IntStream.range(0, 2).forEach( i -> {
				it1.next();
				it1.remove();
			});
			cp = assertContentProviderServer(cp, serverSet.size());
			serverSet = cp.getServerSet();
			
/*
 add 2
*/

			addServers(2, serverSet);
			cp = assertContentProviderServer(cp, serverSet.size());
			serverSet = cp.getServerSet();
			
/*
 add 2 more
*/

			addServers(2, serverSet);
			cp = assertContentProviderServer(cp, serverSet.size());
			serverSet = cp.getServerSet();
			
/*
 remove 2, and add 3 more
*/

			final Iterator<ContentProviderServer> it2 = serverSet.iterator();
			IntStream.range(0, 2).forEach( (int nbr) -> {
				it2.next();
				it2.remove();
			});
			addServers(3, serverSet);
			cp = assertContentProviderServer(cp, serverSet.size());
		} finally {
			if(cp != null && cp.getId() != null) {
				delete(cp);
			}
		}
	}
	
	@Test
	public void testCreateDefautPatterns() {
		ContentProvider cp = null;
		try {
			cp = super.createContentProvider();
			cp.setName(getRandomName());
			Response response = saveAndAssert(cp);
			cp = get((String)response.getResponseValue());
			Assert.assertNotNull(cp);
			
			response = contentProviderServiceClient.createDefaultURIPatterns(cp.getId());
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isSuccess(), response.toString());
			
			response = contentProviderServiceClient.createDefaultURIPatterns(cp.getId());
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isSuccess(), response.toString());
			
			final ContentProvider tempCP = contentProviderServiceClient.getContentProvider(cp.getId());
			Assert.assertNotNull(tempCP);
			Assert.assertTrue(CollectionUtils.isNotEmpty(tempCP.getPatternSet()));
			tempCP.getPatternSet().forEach(pattern -> {
				if(pattern.isCacheable()) {
					Assert.assertNotNull(pattern.getCacheTTL(), String.format("Pattern '%s:%s' did not have a cache TTL", pattern.getId(), pattern.getPattern()));
				}
			});
		} finally {
			if(cp != null && cp.getId() != null) {
				delete(cp);
			}
		}
	}
		
	@Override
	protected ContentProvider newInstance() {
		return super.createContentProvider();
	}
	
	@Override
	protected boolean useDeepCopyOnFindBeans() {
		return true;
	}

	@Override
	protected ContentProviderSearchBean newSearchBean() {
		final ContentProviderSearchBean searchBean = new ContentProviderSearchBean();
		return searchBean;
	}

	@Override
	protected Response save(ContentProvider t) {
		return contentProviderServiceClient.saveContentProvider(t);
	}

	@Override
	protected Response delete(ContentProvider t) {
		return contentProviderServiceClient.deleteContentProvider(t.getId());
	}

	@Override
	protected ContentProvider get(String key) {
		return contentProviderServiceClient.getContentProvider(key);
	}

	@Override
	public List<ContentProvider> find(ContentProviderSearchBean searchBean, int from, int size) {
		return contentProviderServiceClient.findBeans(searchBean, from, size);
	}

}
