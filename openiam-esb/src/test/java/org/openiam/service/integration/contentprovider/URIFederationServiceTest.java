package org.openiam.service.integration.contentprovider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.uriauth.dto.SSOLoginResponse;
import org.openiam.base.response.URIFederationResponse;
import org.openiam.base.Tuple;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.request.AuthenticationRequest;
import org.openiam.base.response.AuthenticationResponse;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.srvc.am.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClientException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:test-integration-environment.xml","classpath:test-esb-integration.xml"})
public class URIFederationServiceTest extends AbstractURIFederationTest {	
	
	private ContentProvider cp = null;
	private User user = null;


	@Autowired
    @Qualifier("authServiceClient")
    private AuthenticationService authServiceClient;

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
	    	//pattern.setErrorMappings(getErrorMappings());
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
		    Assert.assertTrue(response.isSuccess(), String.format("Response: %s", response));
		    
		    //pattern = contentProviderServiceClient.getURIPattern((String)response.getResponseValue());
	    }
	    cp = contentProviderServiceClient.getContentProvider(id);
	    Assert.assertNotNull(cp);
	    
	    refreshContentProviderManager();
	    
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
    public void testKerberosAuthentication() {
    	final AuthenticationRequest request = new AuthenticationRequest();
    	request.setPrincipal("snelson");
    	request.setSkipPasswordCheck(true);
    	request.setAuthPolicyId(null);
		request.setLanguageId(getDefaultLanguage().getId());
    	AuthenticationResponse response = authServiceClient.login(request);
    	Assert.assertNotNull(response);
    	Assert.assertTrue(response.getStatus().equals(ResponseStatus.SUCCESS));
    	Assert.assertNotNull(response.getSubject());
    	Assert.assertNotNull(response.getSubject().getSsoToken());
    	
/*    	request.setKerberosAuth(false);
    	response = authServiceClient.login(request);
    	Assert.assertNotNull(response);
    	Assert.assertTrue(response.getStatus().equals(ResponseStatus.FAILURE));
    	Assert.assertEquals(response.getAuthErrorCode(), AuthenticationConstants.RESULT_INVALID_PASSWORD);*/
    }
    

    private URIFederationResponse federateProxyURI(final String userId, final String proxyURI, final String method) {
    	final String baseURL = String.format("%s?userId=%s&proxyURI=%s&method=%s", getESBRestfulURL("/openiam-esb/auth/proxy/federateUser"),
    			encode(userId), encode(proxyURI), encode(method));
    	try {
			return restTemplate.getForEntity(new URI(baseURL) , URIFederationResponse.class).getBody();
		} catch (RestClientException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
    }
    
    private URIFederationResponse getMetadata(final String proxyURI, final String method) {
    	final String baseURL = String.format("%s?proxyURI=%s&method=%s", getESBRestfulURL("/openiam-esb/auth/proxy/metadata"),
    			encode(proxyURI), encode(method));
    	try {
			return restTemplate.getForEntity(new URI(baseURL) , URIFederationResponse.class).getBody();
		} catch (RestClientException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
    }
    
    private Response renewToken(final String principal, final String patternId, final String token, final String tokenType) {
    	final String baseURL = String.format("%s?principal=%s&token=%s&tokenType=%s&patternId=%s", getESBRestfulURL("/openiam-esb/auth/renewToken"),
    							encode(principal), encode(token), encode(tokenType), encode(patternId));
    	try {
			return restTemplate.getForEntity(new URI(baseURL) , Response.class).getBody();
		} catch (RestClientException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
    }
    
    private void assertMetadataEquals(final String proxyURI, final String method, final URIFederationResponse response) {
    	URIFederationResponse metadataResponse = getMetadata(proxyURI, method);
    	Assert.assertEquals(response.getCpId(), metadataResponse.getCpId());
    	Assert.assertEquals(response.getPatternId(), metadataResponse.getPatternId());
    }
    
    private SSOLoginResponse getCookieFromProxyURIAndPrincipal(final String proxyURI, final String principal, final String method) {
    	final String baseURL = String.format("%s?proxyURI=%s&method=%s&principal=%s", getESBRestfulURL("/openiam-esb/auth/proxy/getCookieFromProxyURIAndPrincipal"),
    			encode(proxyURI), encode(method), encode(principal));
    	try {
			return restTemplate.getForEntity(new URI(baseURL) , SSOLoginResponse.class).getBody();
		} catch (RestClientException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
    }
	
	@Test
	public void testPatternFederation() {
		final String userId = user.getId();
		final String principal = user.getPrincipalList().get(0).getLogin();
		
		String url = "http://www.example.com";
		String method = null;
		URIFederationResponse response = federateProxyURI(userId, url, method);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_PATTERN);
		assertMetadataEquals(url, method, response);
		
		cp.getPatternSet().forEach(pattern -> {
			final Response entResponse = resourceDataService.addUserToResource(pattern.getResourceId(), userId, null, null, null, null);
			Assert.assertTrue(entResponse.isSuccess());
		});

		refreshAuthorizationManager();
		refreshAuthorizationManager();
		
		url = "http://www.foo.com";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertFalse(response.isConfigured());
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/ignore/foobar";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER);
		assertMetadataEquals(url, method, response);
		
		Response entitlementsResponse = resourceDataService.addUserToResource(cp.getResourceId(), userId, null, null, null, null);
		Assert.assertTrue(entitlementsResponse.isSuccess());
		
		url = "http://www.example.com/paramsWithNoMethod/foobar";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertResponseCode(response, ResponseCode.URI_FEDERATION_PATTERN_NOT_FOUND);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/paramsWithMethod/foobar";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertResponseCode(response, ResponseCode.URI_FEDERATION_PATTERN_NOT_FOUND);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/paramsWithNoMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/paramsWithMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER);
		assertMetadataEquals(url, method, response);
		
		refreshAuthorizationManager();
		
		url = "http://www.example.com";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertSuccess(response, false);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertSuccess(response, false);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/ignore/foobar";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertSuccess(response, false);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/paramsWithNoMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertSuccess(response, false);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/paramsWithMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5";
		method = null;
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertSuccess(response, false);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com";
		method = "GET";
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertSuccess(response, false);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/";
		method = "GET";
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertSuccess(response, false);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/ignore/foobar";
		method = "GET";
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertSuccess(response, false);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/paramsWithNoMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5";
		method = "TRACE";
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_METHOD);
		assertMetadataEquals(url, method, response);
		
		url = "http://www.example.com/paramsWithMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5";
		method = "TRACE";
		response = federateProxyURI(userId, url, method);
		Assert.assertTrue(response.isConfigured());
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_METHOD);
		assertMetadataEquals(url, method, response);
		
		cp.getPatternSet().forEach(pattern -> {
			if(pattern.getMethods() != null) {
				pattern.getMethods().forEach(uriMethod -> {
					final Response entResponse = resourceDataService.addUserToResource(uriMethod.getResourceId(), userId, null, null, null, null);
					Assert.assertTrue(entResponse.isSuccess());
				});
			}
		});
		
		refreshAuthorizationManager();
		
		url = "http://www.example.com/paramsWithNoMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5";
		method = "TRACE";
		response = federateProxyURI(userId, url, method);
		assertSuccess(response, true);
		Assert.assertTrue(response.isConfigured());
		Assert.assertNotNull(response.getCacheTTL());
		assertMetadataEquals(url, method, response);
		SSOLoginResponse loginResponse = getCookieFromProxyURIAndPrincipal(url, principal, method);
		Assert.assertNotNull(loginResponse);
		assertSuccess(loginResponse);
		Assert.assertTrue(renewToken(loginResponse.getOpeniamPrincipal(), response.getPatternId(), loginResponse.getSsoToken().getToken(), loginResponse.getSsoToken().getTokenType()).isSuccess());
		
		url = "http://www.example.com/paramsWithMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5";
		method = "TRACE";
		response = federateProxyURI(userId, url, method);
		assertSuccess(response, true);
		Assert.assertTrue(response.isConfigured());
		Assert.assertNotNull(response.getCacheTTL());
		assertMetadataEquals(url, method, response);
		loginResponse = getCookieFromProxyURIAndPrincipal(url, principal, method);
		Assert.assertNotNull(loginResponse);
		assertSuccess(loginResponse);
		Assert.assertTrue(renewToken(loginResponse.getOpeniamPrincipal(), response.getPatternId(), loginResponse.getSsoToken().getToken(), loginResponse.getSsoToken().getTokenType()).isSuccess());

/* all auth level groupings should have been added by the code that created the cp */

		Assert.assertTrue(CollectionUtils.isNotEmpty(response.getAuthLevelTokenList()));
		Assert.assertTrue(response.getAuthLevelTokenList().stream().filter(e -> e.getAuthLevelId().equals("OAUTH")).count() > 0, 
				"OAuth Auth Level shoudl have been present");
	}
	
}

