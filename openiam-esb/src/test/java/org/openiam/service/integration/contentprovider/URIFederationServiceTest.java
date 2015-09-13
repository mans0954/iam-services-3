package org.openiam.service.integration.contentprovider;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.base.Tuple;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.user.dto.User;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class URIFederationServiceTest extends AbstractURIFederationTest {	
	
	private ContentProvider cp = null;
	private User user = null;
	
	@BeforeClass
	protected void _setUp() throws Exception {
		Response response = null;
		
		final ContentProviderSearchBean sb = new ContentProviderSearchBean();
		sb.setDomainPattern("www.example.com");
		final List<ContentProvider> cpList = contentProviderServiceClient.findBeans(sb, 0, 1);
		if(CollectionUtils.isNotEmpty(cpList)) {
			response = contentProviderServiceClient.deleteContentProvider(cpList.get(0).getId());
			Assert.assertNotNull(response);
		    Assert.assertTrue(response.isSuccess());
		}
		
	    cp = super.createContentProvider();
	    cp.setDomainPattern("www.example.com");
	    cp.setAuthCookieDomain(".example.com");
	    response = contentProviderServiceClient.saveContentProvider(cp);
	    Assert.assertNotNull(response);
	    Assert.assertTrue(response.isSuccess());
	    final String id = (String)response.getResponseValue();
	    cp = contentProviderServiceClient.getContentProvider(id);
	    Assert.assertNotNull(cp);
	    
	    final List<Tuple<String, PatternMatchMode>> tuples = new LinkedList<Tuple<String,PatternMatchMode>>();
	    tuples.add(new Tuple<String, PatternMatchMode>("/*", PatternMatchMode.IGNORE));
	    tuples.add(new Tuple<String, PatternMatchMode>("/ignore/*", PatternMatchMode.IGNORE));
	    tuples.add(new Tuple<String, PatternMatchMode>("/any/*", PatternMatchMode.ANY_PARAMS));
	    tuples.add(new Tuple<String, PatternMatchMode>("/none/*", PatternMatchMode.NO_PARAMS));
	    tuples.add(new Tuple<String, PatternMatchMode>("/paramsWithNoMethod/*", PatternMatchMode.SPECIFIC_PARAMS));
	    tuples.add(new Tuple<String, PatternMatchMode>("/paramsWithMethod/*", PatternMatchMode.SPECIFIC_PARAMS));
	    
	    for(final Tuple<String, PatternMatchMode> tuple : tuples) {
	    	final URIPattern pattern = new URIPattern();
	    	pattern.setCacheable(true);
	    	pattern.setCacheTTL(Integer.valueOf(RandomUtils.nextInt(30, 100)));
	    	pattern.setContentProviderId(cp.getId());
	    	pattern.setPattern(tuple.getKey());
	    	pattern.setMatchMode(tuple.getValue());
	    	pattern.setErrorMappings(getErrorMappings());
	    	pattern.setSubstitutions(getSubstitutions());
	    	pattern.setServers(getPatternServers());
	    	pattern.setRedirectTo("/" + getRandomName());
	    	pattern.setMetaEntitySet(getPatternMetaEntitySet());
	    	for(int i = 0; i < 3; i++) {
				final URIPatternParameter param = new URIPatternParameter();
				param.setName("" + i);
				final List<String> values = new LinkedList<String>();
				values.add("" + i);
				param.setValues(values);
				pattern.addParam(param);
			}
	    	if(PatternMatchMode.SPECIFIC_PARAMS.equals(pattern.getMatchMode())) {
		    	for(final HttpMethod method : HttpMethod.values()) {
		    		pattern.setMethods(getMethods(method, 3));
		    	}
	    	}
	    	response = contentProviderServiceClient.saveURIPattern(pattern);
	    	Assert.assertNotNull(response);
		    Assert.assertTrue(response.isSuccess());
		    
		    //pattern = contentProviderServiceClient.getURIPattern((String)response.getResponseValue());
	    }
	    cp = contentProviderServiceClient.getContentProvider(id);
	    Assert.assertNotNull(cp);
	    
	    uriFederationServiceClient.sweep();
	    uriFederationServiceClient.sweep();
	    
	    user = super.createUser();
	    Assert.assertNotNull(user);
	}
	
	@AfterClass
    public void _tearDown() throws Exception {
    	if(cp != null) {
    		final Response response = contentProviderServiceClient.deleteContentProvider(cp.getId());
    		Assert.assertNotNull(response);
    		Assert.assertTrue(response.isSuccess());
    	}
    	if(user != null) {
    		userServiceClient.removeUser(user.getId());
    	}
    }
	
	@Test
	public void testPatternFederation() {
		final String userId = user.getId();
		
		URIFederationResponse response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com", null);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER);
		
		Response entitlementsResponse = resourceDataService.addUserToResource(cp.getResourceId(), userId, null, null);
		Assert.assertTrue(entitlementsResponse.isSuccess());
		
		authorizationManagerServiceClient.refreshCache();
		authorizationManagerServiceClient.refreshCache();
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.foo.com", null);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com", null);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_PATTERN);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/", null);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_PATTERN);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/ignore/foobar", null);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_PATTERN);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithNoMethod/foobar", null);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_PATTERN_NOT_FOUND);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithMethod/foobar", null);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_PATTERN_NOT_FOUND);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithNoMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5", null);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_PATTERN);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5", null);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_PATTERN);
		
		cp.getPatternSet().forEach(pattern -> {
			final Response entResponse = resourceDataService.addUserToResource(pattern.getResourceId(), userId, null, null);
			Assert.assertTrue(entResponse.isSuccess());
		});
		
		refreshAuthorizationManager();
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com", null);
		assertSuccess(response, false);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/", null);
		assertSuccess(response, false);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/ignore/foobar", null);
		assertSuccess(response, false);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithNoMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5", null);
		assertSuccess(response, false);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5", null);
		assertSuccess(response, false);
		
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com", "GET");
		assertSuccess(response, false);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/", "GET");
		assertSuccess(response, false);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/ignore/foobar", "GET");
		assertSuccess(response, false);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithNoMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5", "TRACE");
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_METHOD);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5", "TRACE");
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_METHOD);
		
		cp.getPatternSet().forEach(pattern -> {
			if(pattern.getMethods() != null) {
				pattern.getMethods().forEach(method -> {
					final Response entResponse = resourceDataService.addUserToResource(method.getResourceId(), userId, null, null);
					Assert.assertTrue(entResponse.isSuccess());
				});
			}
		});
		
		refreshAuthorizationManager();
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithNoMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5", "TRACE");
		assertSuccess(response, true);
		Assert.assertNotNull(response.getCacheTTL());
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5", "TRACE");
		assertSuccess(response, true);
		Assert.assertNotNull(response.getCacheTTL());
		
		//uriFederationServiceClient.getMetadata(proxyURI, method);
	}
	
}
