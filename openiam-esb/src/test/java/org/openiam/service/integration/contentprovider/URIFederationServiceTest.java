package org.openiam.service.integration.contentprovider;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternErrorMapping;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.dto.URIPatternMethodMeta;
import org.openiam.am.srvc.dto.URIPatternMethodMetaValue;
import org.openiam.am.srvc.dto.URIPatternMethodParameter;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.am.srvc.dto.URIPatternServer;
import org.openiam.am.srvc.dto.URIPatternSubstitution;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbeans.URIPatternSearchBean;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.am.srvc.ws.AuthResourceAttributeWebService;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.am.srvc.ws.URIFederationWebService;
import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.openiam.base.Tuple;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class URIFederationServiceTest extends AbstractServiceTest {	
	
	@Autowired
	@Qualifier("authorizationManagerServiceClient")
	private AuthorizationManagerWebService authorizationManagerServiceClient;

	@Autowired
	@Qualifier("uriFederationServiceClient")
	private URIFederationWebService uriFederationServiceClient;
	
	@Autowired
	@Qualifier("contentProviderServiceClient")
	private ContentProviderWebService contentProviderServiceClient;
	
	@Autowired
	@Qualifier("resourceServiceClient")
    protected ResourceDataService resourceDataService;
	
	@Autowired
	@Qualifier("authAttributeServiceClient")
	private AuthResourceAttributeWebService authAttributeServiceClient;
	
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
    		userServiceClient.deleteUser(user.getId());
    	}
    }
	
	@Test
	public void testPatternFederation() {
		final String userId = user.getId();
		
		URIFederationResponse response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com", null);
		assertResponseCode(response, ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER);
		
		Response entitlementsResponse = resourceDataService.addUserToResource(cp.getResourceId(), userId, null);
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
			final Response entResponse = resourceDataService.addUserToResource(pattern.getResourceId(), userId, null);
			Assert.assertTrue(entResponse.isSuccess());
		});
		
		authorizationManagerServiceClient.refreshCache();
		authorizationManagerServiceClient.refreshCache();
		
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
					final Response entResponse = resourceDataService.addUserToResource(method.getResourceId(), userId, null);
					Assert.assertTrue(entResponse.isSuccess());
				});
			}
		});
		
		authorizationManagerServiceClient.refreshCache();
		authorizationManagerServiceClient.refreshCache();
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithNoMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5", "TRACE");
		assertSuccess(response, true);
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/paramsWithMethod/foobar?0=0&1=1&2=2&3=3&4=4&5=5", "TRACE");
		assertSuccess(response, true);
		
		//uriFederationServiceClient.getMetadata(proxyURI, method);
	}
	
	private void assertSuccess(final URIFederationResponse response, final boolean shouldHaveMethod) {
		Assert.assertTrue(response.isSuccess());
		Assert.assertTrue(StringUtils.isNotBlank(response.getRedirectTo()));
		Assert.assertNotNull(response.getServer());
		Assert.assertTrue(CollectionUtils.isNotEmpty(response.getSubstitutionList()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(response.getErrorMappingList()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(response.getAuthLevelTokenList()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(response.getRuleTokenList()));
		if(shouldHaveMethod) {
			Assert.assertTrue(StringUtils.isNotBlank(response.getMethodId()));
		}
	}
	
	private Set<URIPatternServer> getPatternServers() {
		final Set<URIPatternServer> retval = new HashSet<URIPatternServer>();
		for(int i = 0; i < 3; i++) {
			final URIPatternServer val = new URIPatternServer();
			val.setServerURL(getRandomName());
			retval.add(val);
		}
		return retval;
	}
	
	private Set<URIPatternErrorMapping> getErrorMappings() {
		final Set<URIPatternErrorMapping> retval = new HashSet<URIPatternErrorMapping>();
		for(int i = 0; i < 3; i++) {
			final URIPatternErrorMapping val = new URIPatternErrorMapping();
			val.setErrorCode(RandomUtils.nextInt(500));
			val.setRedirectURL("/" + getRandomName());
			retval.add(val);
		}
		return retval;
	}
	
	private Set<URIPatternSubstitution> getSubstitutions() {
		final Set<URIPatternSubstitution> substitutions = new HashSet<URIPatternSubstitution>();
		for(int i = 0; i < 3; i++) {
			final URIPatternSubstitution substitution = new URIPatternSubstitution();
			substitution.setQuery(getRandomName());
			substitution.setReplaceWith(getRandomName());
			substitution.setOrder(Integer.valueOf(i));
			substitutions.add(substitution);
		}
		return substitutions;
	}
	
	private Set<URIPatternMethodMeta> getMethodMetaEntitySet() {
		final Set<URIPatternMethodMeta> retval = new HashSet<URIPatternMethodMeta>();
		contentProviderServiceClient.getAllMetaType().forEach(type -> {
			final URIPatternMethodMeta meta = new URIPatternMethodMeta();
			meta.setContentType(getRandomName());
			meta.setName(getRandomName());
			meta.setMetaType(type);
			
			final Set<URIPatternMethodMetaValue> metaValueSet = new HashSet<URIPatternMethodMetaValue>();
			for(int i = 0; i < 4; i++) {
				final URIPatternMethodMetaValue value = new URIPatternMethodMetaValue();
				value.setName(getRandomName());
				if(i == 0) {
					value.setEmptyValue(true);
				} else if(i == 1) {
					value.setStaticValue(getRandomName());
				} else if(i == 2) {
					value.setFetchedValue(getRandomName());
				} else if(i == 3) {
					value.setAmAttribute(authAttributeServiceClient.getAmAttributeList().get(0));
				}
				metaValueSet.add(value);
			}
			meta.setMetaValueSet(metaValueSet);
			retval.add(meta);
		});
		return retval;
	}
	
	private Set<URIPatternMeta> getPatternMetaEntitySet() {
		final Set<URIPatternMeta> retval = new HashSet<URIPatternMeta>();
		contentProviderServiceClient.getAllMetaType().forEach(type -> {
			final URIPatternMeta meta = new URIPatternMeta();
			meta.setContentType(getRandomName());
			meta.setName(getRandomName());
			meta.setMetaType(type);
			
			final Set<URIPatternMetaValue> metaValueSet = new HashSet<URIPatternMetaValue>();
			for(int i = 0; i < 4; i++) {
				final URIPatternMetaValue value = new URIPatternMetaValue();
				value.setName(getRandomName());
				if(i == 0) {
					value.setEmptyValue(true);
				} else if(i == 1) {
					value.setStaticValue(getRandomName());
				} else if(i == 2) {
					value.setFetchedValue(getRandomName());
				} else if(i == 3) {
					value.setAmAttribute(authAttributeServiceClient.getAmAttributeList().get(0));
				}
				metaValueSet.add(value);
			}
			meta.setMetaValueSet(metaValueSet);
			retval.add(meta);
		});
		return retval;
	}
	
	private Set<URIPatternMethod> getMethods(final HttpMethod method, final int count) {
		final Set<URIPatternMethod> retVal = new HashSet<URIPatternMethod>();
		for(int i = 0; i < count; i++) {
			final URIPatternMethod patternMethod = new URIPatternMethod();
			patternMethod.setMetaEntitySet(getMethodMetaEntitySet());
			if(i == 0) {
				patternMethod.setMatchMode(PatternMatchMode.IGNORE);
			} else if(i == 1) {
				patternMethod.setMatchMode(PatternMatchMode.NO_PARAMS);
			} else if(i == 2) {
				patternMethod.setMatchMode(PatternMatchMode.ANY_PARAMS);
			} else if(i > 2) {
				patternMethod.setMatchMode(PatternMatchMode.SPECIFIC_PARAMS);
				for(int j = i; j < count + 1; j++) {
					final URIPatternMethodParameter param = new URIPatternMethodParameter();
					param.setName("" + j);
					final List<String> values = new LinkedList<String>();
					values.add("" + j);
					param.setValues(values);
					patternMethod.addParam(param);
				}
			}
			
			patternMethod.setMethod(method);
			retVal.add(patternMethod);
		}
		return retVal;
	}
}
