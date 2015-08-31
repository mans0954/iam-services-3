package org.openiam.service.integration.contentprovider;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.base.Tuple;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class URIFederationServiceCacheTest extends AbstractURIFederationTest {	
	
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
	    cp.setIsPublic(true);
	    response = contentProviderServiceClient.saveContentProvider(cp);
	    Assert.assertNotNull(response);
	    Assert.assertTrue(response.isSuccess());
	    final String id = (String)response.getResponseValue();
	    cp = contentProviderServiceClient.getContentProvider(id);
	    Assert.assertNotNull(cp);
	    
	    final List<Tuple<String, PatternMatchMode>> tuples = new LinkedList<Tuple<String,PatternMatchMode>>();
	    tuples.add(new Tuple<String, PatternMatchMode>("/groovy", PatternMatchMode.IGNORE));
	    tuples.add(new Tuple<String, PatternMatchMode>("/attribute", PatternMatchMode.IGNORE));
	    tuples.add(new Tuple<String, PatternMatchMode>("/static", PatternMatchMode.IGNORE));
	    tuples.add(new Tuple<String, PatternMatchMode>("/empty", PatternMatchMode.IGNORE));
	    tuples.add(new Tuple<String, PatternMatchMode>("/fetched", PatternMatchMode.IGNORE));
	    
	    for(final Tuple<String, PatternMatchMode> tuple : tuples) {
	    	URIPattern pattern = new URIPattern();
	    	pattern.setContentProviderId(cp.getId());
	    	pattern.setPattern(tuple.getKey());
	    	pattern.setMatchMode(tuple.getValue());
	    	pattern.setErrorMappings(getErrorMappings());
	    	pattern.setSubstitutions(getSubstitutions());
	    	pattern.setServers(getPatternServers());
	    	pattern.setRedirectTo("/" + getRandomName());
	    	pattern.setIsPublic(true);
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
		    
		    pattern = contentProviderServiceClient.getURIPattern((String)response.getResponseValue());
		    final URIPatternMeta meta = new URIPatternMeta();
		    final URIPatternMetaValue value = new URIPatternMetaValue();
		    if(tuple.getKey().contains("groovy")) {
		    	value.setGroovyScript("AM/ITPolicyUsing.groovy");
		    } else if(tuple.getKey().contains("attribute")) {
		    	value.setAmAttribute(authAttributeServiceClient.getAmAttributeList().get(0));
		    } else if(tuple.getKey().contains("static")) {
		    	value.setStaticValue("abcdefg");
		    } else if(tuple.getKey().contains("fetched")) {
		    	value.setFetchedValue("abcdefg");
		    } else if (tuple.getKey().contains("empty")) {
		    	value.setEmptyValue(true);
		    }
		    meta.setName(RandomStringUtils.randomAlphabetic(5));
		    meta.addMetaValue(value);
		    value.setName(RandomStringUtils.randomAlphabetic(5));
		    meta.setMetaType(contentProviderServiceClient.getAllMetaType().get(0));
		    final Set<URIPatternMeta> metaEntitySet = new HashSet<URIPatternMeta>();
		    metaEntitySet.add(meta);
		    pattern.setMetaEntitySet(metaEntitySet);
		    Assert.assertTrue(contentProviderServiceClient.saveURIPattern(pattern).isSuccess());
	    }
	    cp = contentProviderServiceClient.getContentProvider(id);
	    Assert.assertNotNull(cp);
	    
	    uriFederationServiceClient.sweep();
	    uriFederationServiceClient.sweep();
	    
	    user = super.createUser();
	    Assert.assertNotNull(user);
	    
	    refreshAuthorizationManager();
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
	public void testCacheable() {
		final String userId = user.getId();
		
		URIFederationResponse response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/attribute", null);
		Assert.assertFalse(response.isCacheable(), response.toString());

		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/fetched", null);
		Assert.assertFalse(response.isCacheable(), response.toString());
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/static", null);
		Assert.assertTrue(response.isCacheable(), response.toString());
		
		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/empty", null);
		Assert.assertTrue(response.isCacheable(), response.toString());

		response = uriFederationServiceClient.federateProxyURI(userId, "http://www.example.com/groovy", null);
		Assert.assertFalse(response.isCacheable(), response.toString());
	}
}
