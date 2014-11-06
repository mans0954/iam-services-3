package org.openiam.authmanager.common.model.url;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.protocol.UriPatternMatcher;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class URIPatternTreeTest {

	private static final Log log = LogFactory.getLog(URIPatternTreeTest.class);
	
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
	
	/*
	@Test
	public void testCompilcatedPatterns() {
		List<AuthorizationToken> patternList = new LinkedList<AuthorizationToken>();

		patternList.add(new AuthorizationToken("/openiam/*", new String[] {"/openiam/login"}, new String[] {"/openiam", "/openiam/selfservice", "/openiam/userroles", "/openiam/userroles/add", "/openiam/selfservice/index.html"}));
		patternList.add(new AuthorizationToken("/openiam/selfservice", new String[] {"/openiam/selfservice/"}, new String[] {"/openiam/self", "openiam/self/service"}));
		patternList.add(new AuthorizationToken("/openiam/selfservice/*", new String[] {"/openiam/selfservice/index.html"}, new String[] {"/openiam/selfservice"}));
		patternList.add(new AuthorizationToken("/openiam/userroles", new String[] {"/openiam/userroles"}, new String[] {"/openiam/userroles/access"}));
		patternList.add(new AuthorizationToken("/openiam/userroles*", new String[] {"/openiam/userroles/add", "/openiam/userroles/delete"}, new String[] {"/openiam/userroles"}));
		patternList.add(new AuthorizationToken("/openiam/userroles*.html", new String[] {"/openiam/userroles.html", "/openiam/userroles/save/result.html"}, new String[] {"/openiam/userroles"}));
		patternList.add(new AuthorizationToken("/openiam/userroles*.htmll", new String[] {"/openiam/userroles.htmll", "/openiam/userroles/save/result.htmll"}, new String[] {"/openiam/userroles", "/openiam/userroles.html"}));
		patternList.add(new AuthorizationToken("/openiam/selfservice*.html", new String[] {"/openiam/selfservice.html"}, new String[] {"/openiam/selfservicehtml"}));
		patternList.add(new AuthorizationToken("/openiam/self*service", new String[] {"/openiam/selffffservice", "/openiam/selfservice.service"}, new String[] {"/openiam/self", "openiam/service"}));
		patternList.add(new AuthorizationToken("/*", new String[] {"/foobar"}, new String[] {"/openiam/self", "openiam/service", "/openiam/selffffservice", "/openiam/selfservice.service"}));

		for(int i = 0; i < 100; i++) {
			Collections.shuffle(patternList);
			final URIPatternTree tree = new URIPatternTree();
			for(final AuthorizationToken token : patternList) {
				log.info(String.format("Putting: %s", token.pattern));
				try {
					tree.addPattern(token.patterns.iterator().next());
				} catch (InvalidPatternException e) {
					log.error("Exception", e);
				}
			}
			
			log.info(tree);
			for(final AuthorizationToken token : patternList) {
				for(final URI uri : token.URIs) {
					final URIPatternSearchResult expected = new URIPatternSearchResult();
					expected.addPatterns(token.patterns);
					
					final URIPatternSearchResult found = tree.find(uri);
					Assert.assertEquals(expected, found, String.format("Can't match for %s, uri: %s", token.pattern.getPattern(), uri));
				}
				
				for(final URI uri : token.badURIs) {
					final URIPatternSearchResult expected = new URIPatternSearchResult();
					expected.addPatterns(token.patterns);
					
					final URIPatternSearchResult found = tree.find(uri);
					Assert.assertFalse(expected.equals(found), String.format("URI returned resources when it should not have.  Pattern: %s, URI: %s", token.pattern.getPattern(), uri));
				}
			}
		}
	}
	*/
	
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
