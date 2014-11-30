package org.openiam.service.integration.contentprovider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.sql.Insert;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXrefId;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.ws.AuthProviderWebService;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class ContentProviderServiceTest extends AbstractKeyNameServiceTest<ContentProvider, ContentProviderSearchBean> {
	
	@Autowired
	@Qualifier("contentProviderServiceClient")
	private ContentProviderWebService contentProviderServiceClient;
	
	@Autowired
	@Qualifier("authProviderServiceClient")
	private AuthProviderWebService authProviderServiceClient;
	
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
	}
		
	@Override
	protected ContentProvider newInstance() {
		final ContentProvider cp = new ContentProvider();
		cp.setAuthCookieName(getRandomName());
		cp.setDomainPattern(getRandomName());
		cp.setAuthCookieDomain(cp.getDomainPattern());
		cp.setUrl(getRandomName());
		cp.setAuthProviderId(authProviderServiceClient.findAuthProviderBeans(null, 0, 1).get(0).getId());
		
		final ContentProviderServer server = new ContentProviderServer();
		server.setServerURL(getRandomName());
		final Set<ContentProviderServer> serverSet = new HashSet<ContentProviderServer>();
		serverSet.add(server);
		cp.setServerSet(serverSet);
		
		final Set<AuthLevelGroupingContentProviderXref> groupingXrefs = new HashSet<AuthLevelGroupingContentProviderXref>();
		for(final AuthLevelGrouping grouping : contentProviderServiceClient.getAuthLevelGroupingList()) {
		//contentProviderServiceClient.getAuthLevelGroupingList().forEach((final AuthLevelGrouping grouping) -> {
			final AuthLevelGroupingContentProviderXref xref = new AuthLevelGroupingContentProviderXref();
			final AuthLevelGroupingContentProviderXrefId id = new AuthLevelGroupingContentProviderXrefId();
			id.setGroupingId(grouping.getId());
			xref.setId(id);
			groupingXrefs.add(xref);
		//});
		}
		cp.setGroupingXrefs(groupingXrefs);
		return cp;
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
