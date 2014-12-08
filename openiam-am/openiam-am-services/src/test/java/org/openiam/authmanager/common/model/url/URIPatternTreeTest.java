package org.openiam.authmanager.common.model.url;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.protocol.UriPatternMatcher;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.dto.URIPatternMethodParameter;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.am.srvc.model.URIPatternSearchResult;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;
import org.openiam.am.srvc.uriauth.model.ContentProviderNode;
import org.openiam.base.Tuple;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

public class URIPatternTreeTest {

	private static final Log log = LogFactory.getLog(URIPatternTreeTest.class);
	
	@Test
	public void testContentProviderNodeWithNoMethods() throws URISyntaxException {
		final ContentProvider cp = new ContentProvider();
		cp.setDomainPattern("www.example.com");
		
		final URIPattern pattern = new URIPattern();
		pattern.setPattern("/example.html");
		pattern.setMatchMode(PatternMatchMode.IGNORE);
		
		final Set<URIPattern> patterns = new HashSet<URIPattern>();
		patterns.add(pattern);
		cp.setPatternSet(patterns);
		
		final ContentProviderNode node = new ContentProviderNode(cp);
		URIPatternSearchResult result = node.getURIPattern(new URI("http://www.example.com/foowww.example.com.html"), null);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), null);
		assertNotNullPattern(result);
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&c=d"), null);
		assertNotNullPattern(result);
		
		pattern.setMatchMode(PatternMatchMode.NO_PARAMS);
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), null);
		assertNotNullPattern(result);
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&c=d"), null);
		Assert.assertNull(result.getPattern());
		
		pattern.setMatchMode(PatternMatchMode.ANY_PARAMS);
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), null);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&c=d"), null);
		assertNotNullPattern(result);
		
		pattern.setMatchMode(PatternMatchMode.SPECIFIC_PARAMS);
		pattern.addParam(getParam("a", "b"));
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), null);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a="), null);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b"), null);
		assertNotNullPattern(result);
		
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&a="), null);
		assertNotNullPattern(result);
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&a=c"), HttpMethod.OPTIONS);
		assertNotNullPattern(result);
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&c=d"), HttpMethod.PATCH);
		assertNotNullPattern(result);
		
		pattern.addParam(getParam("a", "c"));
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a="), HttpMethod.PUT);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b"), HttpMethod.HEAD);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&a=c"), HttpMethod.TRACE);
		assertNotNullPattern(result);
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&a=c&a=d&e=f"), null);
		assertNotNullPattern(result);
		
		pattern.addParam(getParam("c", "d"));
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&c=d"), HttpMethod.POST);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&c=d&a=c"), HttpMethod.GET);
		assertNotNullPattern(result);
	}
	
	@Test
	public void testContentProviderNodeWithNonSpecificParams() throws URISyntaxException {
		final ContentProvider cp = new ContentProvider();
		cp.setDomainPattern("www.example.com");
		
		final URIPattern pattern = new URIPattern();
		pattern.setPattern("/example.html");
		pattern.setMatchMode(PatternMatchMode.IGNORE);
		
		final Set<URIPattern> patterns = new HashSet<URIPattern>();
		patterns.add(pattern);
		cp.setPatternSet(patterns);
		
		pattern.addMethod(getMethod("ignoreGET", HttpMethod.GET, PatternMatchMode.IGNORE, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("noparmasGET", HttpMethod.GET, PatternMatchMode.NO_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("anyparamsGET", HttpMethod.GET, PatternMatchMode.ANY_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.initTreeSet();
		
		pattern.addMethod(getMethod("ignorePOST", HttpMethod.POST, PatternMatchMode.IGNORE, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("noparmasPOST", HttpMethod.POST, PatternMatchMode.NO_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("anyparamsPOST", HttpMethod.POST, PatternMatchMode.ANY_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.initTreeSet();
		
		final ContentProviderNode node = new ContentProviderNode(cp);
		URIPatternSearchResult result = node.getURIPattern(new URI("http://www.example.com/foowww.example.com.html"), null);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), null);
		assertNotNullPattern(result);
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), HttpMethod.HEAD);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), HttpMethod.GET);
		assertNotNullMethod(result);
		Assert.assertEquals("noparmasGET", result.getMethod().getId());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a="), HttpMethod.GET);
		assertNotNullMethod(result);
		Assert.assertEquals("anyparamsGET", result.getMethod().getId());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b"), HttpMethod.GET);
		assertNotNullMethod(result);
		Assert.assertEquals("anyparamsGET", result.getMethod().getId());
	}
	
	@Test
	public void testContentProviderNodeWithNonIgnore() throws URISyntaxException {
		final ContentProvider cp = new ContentProvider();
		cp.setDomainPattern("www.example.com");
		
		final URIPattern pattern = new URIPattern();
		pattern.setPattern("/example.html");
		pattern.setMatchMode(PatternMatchMode.IGNORE);
		
		final Set<URIPattern> patterns = new HashSet<URIPattern>();
		patterns.add(pattern);
		cp.setPatternSet(patterns);
		
		pattern.addMethod(getMethod("ignoreGET", HttpMethod.GET, PatternMatchMode.IGNORE, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("noparmasGET", HttpMethod.GET, PatternMatchMode.NO_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.initTreeSet();
		
		pattern.addMethod(getMethod("ignorePOST", HttpMethod.POST, PatternMatchMode.IGNORE, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("noparmasPOST", HttpMethod.POST, PatternMatchMode.NO_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("anyparamsPOST", HttpMethod.POST, PatternMatchMode.ANY_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.initTreeSet();
		
		final ContentProviderNode node = new ContentProviderNode(cp);
		URIPatternSearchResult result = node.getURIPattern(new URI("http://www.example.com/foowww.example.com.html"), null);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), null);
		assertNotNullPattern(result);
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), HttpMethod.HEAD);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), HttpMethod.GET);
		assertNotNullMethod(result);
		Assert.assertEquals("noparmasGET", result.getMethod().getId());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a="), HttpMethod.GET);
		assertNotNullMethod(result);
		Assert.assertEquals("ignoreGET", result.getMethod().getId());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b"), HttpMethod.GET);
		assertNotNullMethod(result);
		Assert.assertEquals("ignoreGET", result.getMethod().getId());
	}
	
	@Test
	public void testContentProviderNodeWithSpecificParams() throws URISyntaxException {
		final ContentProvider cp = new ContentProvider();
		cp.setDomainPattern("www.example.com");
		
		final URIPattern pattern = new URIPattern();
		pattern.setPattern("/example.html");
		pattern.setMatchMode(PatternMatchMode.IGNORE);
		
		final Set<URIPattern> patterns = new HashSet<URIPattern>();
		patterns.add(pattern);
		cp.setPatternSet(patterns);
		
		pattern.addMethod(getMethod("ignoreGET", HttpMethod.GET, PatternMatchMode.IGNORE, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("noparmasGET", HttpMethod.GET, PatternMatchMode.NO_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		
		pattern.addMethod(getMethod("ignorePOST", HttpMethod.POST, PatternMatchMode.IGNORE, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("noparmasPOST", HttpMethod.POST, PatternMatchMode.NO_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("anyparamsPOST", HttpMethod.POST, PatternMatchMode.ANY_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		
		pattern.addMethod(getMethod("ab", HttpMethod.POST, PatternMatchMode.SPECIFIC_PARAMS, getMethodParam("a", "b")));
		pattern.addMethod(getMethod("abac", HttpMethod.POST, PatternMatchMode.SPECIFIC_PARAMS, getMethodParam("a", "b"), getMethodParam("a", "c")));
		pattern.addMethod(getMethod("abcd", HttpMethod.POST, PatternMatchMode.SPECIFIC_PARAMS, getMethodParam("a", "b"), getMethodParam("c", "d")));
		
		pattern.initTreeSet();
		
		final ContentProviderNode node = new ContentProviderNode(cp);
		URIPatternSearchResult result = node.getURIPattern(new URI("http://www.example.com/foowww.example.com.html"), null);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), null);
		assertNotNullPattern(result);
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), HttpMethod.HEAD);
		Assert.assertNull(result.getPattern());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html"), HttpMethod.POST);
		assertNotNullMethod(result);
		Assert.assertEquals("noparmasPOST", result.getMethod().getId());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a="), HttpMethod.POST);
		assertNotNullMethod(result);
		Assert.assertEquals("anyparamsPOST", result.getMethod().getId());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b"), HttpMethod.POST);
		assertNotNullMethod(result);
		Assert.assertEquals("ab", result.getMethod().getId());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&a="), HttpMethod.POST);
		assertNotNullMethod(result);
		Assert.assertEquals("ab", result.getMethod().getId());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&a=c"), HttpMethod.POST);
		assertNotNullMethod(result);
		Assert.assertEquals("abac", result.getMethod().getId());
		
		result = node.getURIPattern(new URI("http://www.example.com/example.html?a=b&c=d"), HttpMethod.POST);
		assertNotNullMethod(result);
		Assert.assertEquals("abcd", result.getMethod().getId());
		
	}
	
	private void assertNotNullMethod(final URIPatternSearchResult result) {
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getPattern());
		Assert.assertNotNull(result.getMethod());
	}
	
	private void assertNotNullPattern(final URIPatternSearchResult result) {
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getPattern());
		Assert.assertNull(result.getMethod());
	}
	
	private URIPatternMethod getMethod(final String id, final HttpMethod httpMethod, final PatternMatchMode matchMode, final URIPatternMethodParameter ... params) {
		final URIPatternMethod method = new URIPatternMethod();
		method.setId(id);
		method.setMatchMode(matchMode);
		method.setMethod(httpMethod);
		if(params != null) {
			for(final URIPatternMethodParameter param : params) {
				method.addParam(param);
			}
		}
		return method;
	}
	
	private URIPatternMethodParameter getMethodParam(final String name, final String ... values) {
		final URIPatternMethodParameter param = new URIPatternMethodParameter();
		param.setName(name);
		param.setValues(Arrays.asList(values));
		return param;
	}
	
	private URIPatternParameter getParam(final String name, final String ... values) {
		final URIPatternParameter param = new URIPatternParameter();
		param.setName(name);
		param.setValues(Arrays.asList(values));
		return param;
	}
	
	@Test
	public void testComplicatedPatternsUsingApapche() {
		List<AuthorizationToken> patternList = new LinkedList<AuthorizationToken>();
		patternList.add(new AuthorizationToken("/openiam/*", new String[] {"/openiam/login", "/openiam/"}, new String[] {"/openiam", "/openiam/selfservice", "/openiam/userroles", "/openiam/userroles/add", "/openiam/selfservice/index.html"}));
		patternList.add(new AuthorizationToken("/openiam/selfservice", new String[] {"/openiam/selfservice/"}, new String[] {"/openiam/self", "openiam/self/service"}));
		patternList.add(new AuthorizationToken("/openiam/selfservice/*", new String[] {"/openiam/selfservice/index.html"}, new String[] {"/openiam/selfservice"}));
		patternList.add(new AuthorizationToken("/openiam/userroles", new String[] {"/openiam/userroles"}, new String[] {"/openiam/userroles/access"}));
		patternList.add(new AuthorizationToken("/openiam/userroles*", new String[] {"/openiam/userroles/add", "/openiam/userroles/delete"}, new String[] {"/openiam/userroles"}));
		patternList.add(new AuthorizationToken("/*", new String[] {"/foobar"}, new String[] {"/openiam/self", "openiam/service", "/openiam/selffffservice", "/openiam/selfservice.service"}));
		
		for(int i = 0; i < 100; i++) {
			final UriPatternMatcher<AuthorizationToken> matcher = new UriPatternMatcher<>();
			Collections.shuffle(patternList);
			for(final AuthorizationToken token : patternList) {
				log.info(String.format("Putting: %s", token.pattern));
				matcher.register(token.patterns.iterator().next().getPattern(), token);
			}
			
			for(final AuthorizationToken expected : patternList) {
				final String uri = expected.patterns.iterator().next().getPattern();
				final AuthorizationToken found = matcher.lookup(uri);
				Assert.assertEquals(expected, found, String.format("Can't match for %s, uri: %s", expected.pattern.getPattern(), uri));
				
				for(final URI badURI : expected.badURIs) {
					final AuthorizationToken notExpected = matcher.lookup(badURI.toString());
					Assert.assertNotEquals(expected, notExpected, String.format("URI returned resources when it should not have.  URI: %s", uri));
				}
			}
		}
	}
	
	@Test
	public void testSimplePatterns() {
		List<AuthorizationToken> patternList = null;
		
		patternList = new LinkedList<AuthorizationToken>();
		patternList.add(new AuthorizationToken("/bar/test/openiam.jsp", new String[] {"/bar/test/openiam.jsp"}, null));
		patternList.add(new AuthorizationToken("/index.html", new String[] { "/index.html", "index.html"}, new String[] {"/foo/bar"}));
		patternList.add(new AuthorizationToken("/foo*", new String[] { "/foobar", "/foo", "/foo/bar"}, new String[] {"/ffoo"}));
		patternList.add(new AuthorizationToken("/fooo*", new String[] { "/fooobar", "/fooobo", "/fooo/bar"}, new String[] {"/ffooo"}));
		
		for(int i = 0; i < 100; i++) {
			final UriPatternMatcher<AuthorizationToken> matcher = new UriPatternMatcher<>();
			Collections.shuffle(patternList);
			
			for(final AuthorizationToken token : patternList) {
				matcher.register(token.patterns.iterator().next().getPattern(), token);
			}
			
			for(final AuthorizationToken expected : patternList) {
				final String uri = expected.patterns.iterator().next().getPattern();
				final AuthorizationToken found = matcher.lookup(uri);
				Assert.assertEquals(expected, found, String.format("Can't match for %s, uri: %s", expected.pattern.getPattern(), uri));
				
				for(final URI badURI : expected.badURIs) {
					final AuthorizationToken notExpected = matcher.lookup(badURI.toString());
					Assert.assertNotEquals(expected, notExpected, String.format("URI returned resources when it should not have.  URI: %s", uri));
				}
			}
		}
	}
	
	private URIPattern getRandomPattern(final String uri) {

		final URIPattern pattern = new URIPattern();
		pattern.setPattern(uri);
		pattern.setResourceId(RandomStringUtils.randomAlphanumeric(10));
		return pattern;
	}
	
	private URI[] getURIArray(final String[] URIListArray) {
		final List<URI> uriList = new LinkedList<URI>();
		if(URIListArray != null) {
			for(final String uri : URIListArray) {
				try {
					uriList.add(new URI(uri));
				} catch(Throwable e) {
					log.error("getURIArray", e);
				}
			}
		}
		
		final URI[] retVal = new URI[uriList.size()];
		for(int i = 0; i < uriList.size(); i++) {
			retVal[i] = uriList.get(0);
		}
		
		return retVal;
	}
	
	private class AuthorizationToken {
		
		private URIPattern pattern;
		private Set<URIPattern> patterns = new HashSet<URIPattern>();
		private URI[] URIs;
		private URI[] badURIs;
		
		private AuthorizationToken(final String pattern, final String[] URIs, final String[] badURIs) {
			final URIPattern patternObj = getRandomPattern(pattern);
			
			this.URIs = getURIArray(URIs);
			this.badURIs = getURIArray(badURIs);
			
			this.patterns.add(patternObj);
			this.pattern = patternObj;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + Arrays.hashCode(URIs);
			result = prime * result + Arrays.hashCode(badURIs);
			result = prime * result
					+ ((pattern == null) ? 0 : pattern.hashCode());
			result = prime * result
					+ ((patterns == null) ? 0 : patterns.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AuthorizationToken other = (AuthorizationToken) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (!Arrays.equals(URIs, other.URIs))
				return false;
			if (!Arrays.equals(badURIs, other.badURIs))
				return false;
			if (pattern == null) {
				if (other.pattern != null)
					return false;
			} else if (!pattern.equals(other.pattern))
				return false;
			if (patterns == null) {
				if (other.patterns != null)
					return false;
			} else if (!patterns.equals(other.patterns))
				return false;
			return true;
		}

		private URIPatternTreeTest getOuterType() {
			return URIPatternTreeTest.this;
		}
		
		
	}
}
