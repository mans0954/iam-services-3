package org.openiam.service.integration.contentprovider;

import java.util.HashSet;
import java.util.Iterator;
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
import org.testng.Assert;
import org.testng.annotations.Test;

public class ContentProviderServiceTest extends AbstractContentProviderServiceTest<ContentProvider, ContentProviderSearchBean> {
	
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
		
//		cp = createBean();
//		cp.setServerSet(null);
//		response = save(cp);
//		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_SERVER_REQUIRED);
		
		cp = createBean();
		cp.getServerSet().iterator().next().setServerURL(null);
		response = save(cp);
		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_SERVER_URL_NOT_SET);
		
//		cp = createBean();
//		cp.setAuthCookieDomain(null);
//		response = save(cp);
//		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_COOKIE_DOMAIN_REQUIRED);
		
//		cp = createBean();
//		cp.setAuthCookieName(null);
//		response = save(cp);
//		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_COOKIE_NAME_REQUIRED);
		
		cp = createBean();
		cp.setGroupingXrefs(null);
		response = save(cp);
		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET);
		
//		cp = createBean();
//		cp.setDomainPattern(getRandomName());
//		response = save(cp);
//		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_COOKIE_DOMAIN_NOT_SUBSTR_OF_DOMAIN_PATTERN);
		
		cp = createBean();
//		cp.setAuthProviderId(null);
		response = save(cp);
		assertResponseCode(response, ResponseCode.AUTH_PROVIDER_NOT_SET);
		
		cp = createBean();
//		cp.setAuthProviderId(getRandomName());
		response = save(cp);
		assertResponseCode(response, ResponseCode.AUTH_PROVIDER_NOT_SET);
	}
	
	private void addServers(final int howMany, final Set<ContentProviderServer> serverSet) {
		for(int i=0; i< howMany; i++){
			final ContentProviderServer server = new ContentProviderServer();
			server.setServerURL(getRandomName());
			serverSet.add(server);
		}
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
			for(int i=0; i<2; i++){
				it1.next();
				it1.remove();
			}
//			IntStream.range(0, 1).forEach( i -> {
//				it1.next();
//				it1.remove();
//			});
			cp = assertGroupings(cp, cp.getGroupingXrefs().size());
		} finally {
			if(cp != null && cp.getId() != null) {
				delete(cp);
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
			
			/* add 4 */
			addServers(4, serverSet);
			cp.setServerSet(serverSet);
			cp = assertContentProviderServer(cp, serverSet.size());
			serverSet = cp.getServerSet();
			
			/* delete all but 1 */
			final Iterator<ContentProviderServer> it1 = serverSet.iterator();
			for(int i=0; i<2; i++){
				it1.next();
				it1.remove();
			}
//			IntStream.range(0, 2).forEach( i -> {
//				it1.next();
//				it1.remove();
//			});
			cp = assertContentProviderServer(cp, serverSet.size());
			serverSet = cp.getServerSet();
			
			/* add 2 */
			addServers(2, serverSet);
			cp = assertContentProviderServer(cp, serverSet.size());
			serverSet = cp.getServerSet();
			
			/* add 2 more */
			addServers(2, serverSet);
			cp = assertContentProviderServer(cp, serverSet.size());
			serverSet = cp.getServerSet();
			
			/* remove 2, and add 3 more */
			final Iterator<ContentProviderServer> it2 = serverSet.iterator();
			for(int i=0; i<3; i++){
				it2.next();
				it2.remove();
			}
//			IntStream.range(0, 2).forEach( (int nbr) -> {
//				it2.next();
//				it2.remove();
//			});
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
			Response response = saveAndAssert(cp);
			cp = get((String)response.getResponseValue());
			Assert.assertNotNull(cp);
			
			response = contentProviderServiceClient.createDefaultURIPatterns(cp.getId());
			Assert.assertNotNull(response);
			Assert.assertTrue(response.isSuccess());
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

	@Override
	protected String getId(ContentProvider bean) {
		return bean.getId();
	}

	@Override
	protected void setId(ContentProvider bean, String id) {
		bean.setId(id);
	}

}
