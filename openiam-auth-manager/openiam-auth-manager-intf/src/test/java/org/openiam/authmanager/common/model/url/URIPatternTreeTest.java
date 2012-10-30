package org.openiam.authmanager.common.model.url;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.testng.Assert;
import org.testng.annotations.Test;

public class URIPatternTreeTest {

	private static final Log log = LogFactory.getLog(URIPatternTreeTest.class);
	
	@Test
	public void testCompilcatedPatterns() {
		final URIPatternTree tree = new URIPatternTree();
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
		Collections.shuffle(patternList);

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
			for(final String uri : token.URIs) {
				Assert.assertEquals(token.patterns, tree.find(uri), String.format("Can't match for %s, uri: %s", token.pattern.getPattern(), uri));
			}
		
			for(final String uri : token.badURIs) {
				Assert.assertFalse(token.patterns.equals(tree.find(uri)), String.format("URI returned resources when it should not have.  Pattern: %s, URI: %s", token.pattern.getPattern(), uri));
			}
		}
	}
	
	@Test
	public void testSimplePatterns() {
		
		URIPatternTree tree = null;
		List<AuthorizationToken> patternList = null;
		
		patternList = new LinkedList<AuthorizationToken>();
		patternList.add(new AuthorizationToken("/foo/bar/*.html", new String[] { "/foo/bar/index.html"}, new String[] {"/foo/bar/index.htmll", "/foo/bar/index/html", "/foo/bar/"}));
		patternList.add(new AuthorizationToken("/bar/test/fo*o", new String[] {"/bar/test/foo.io", "/bar/test/fo.io"}, new String[] {"/bar/test/fo/do/ioj"}));
		patternList.add(new AuthorizationToken("/bar/test/openiam.jsp", new String[] {"/bar/test/openiam.jsp"}, null));
		patternList.add(new AuthorizationToken("/fo*o", new String[] { "/fo.fio", "fo.io"}, new String[] {"ffoioj"}));
		patternList.add(new AuthorizationToken("/index.html", new String[] { "/index.html", "index.html"}, new String[] {"/foo/bar"}));
		patternList.add(new AuthorizationToken("/foo*", new String[] { "/foobar", "/foo", "/foo/bar"}, new String[] {"/ffoo"}));
		patternList.add(new AuthorizationToken("/fooo*", new String[] { "/fooobar", "/fooobo", "/fooo/bar"}, new String[] {"/ffooo"}));
		Collections.shuffle(patternList);
		
		
		tree = new URIPatternTree();
		
		for(final AuthorizationToken token : patternList) {
			try {
				tree.addPattern(token.patterns.iterator().next());
			} catch (InvalidPatternException e) {
				log.error("Exception", e);
			}
		}
		
		for(final AuthorizationToken token : patternList) {
			for(final String uri : token.URIs) {
				Assert.assertEquals(token.patterns, tree.find(uri), String.format("Can't match for %s, uri: %s", token.pattern.getPattern(), uri));
			}
		
			for(final String uri : token.badURIs) {
				Assert.assertFalse(token.patterns.equals(tree.find(uri)), String.format("URI returned resources when it should not have.  Pattern: %s, URI: %s", token.pattern.getPattern(), uri));
			}
		}
	}
	
	private AuthorizationURIPattern getRandomPattern(final String uri) {
		final AuthorizationResource resource = new AuthorizationResource();
		resource.setId(RandomStringUtils.randomAlphanumeric(10));

		final AuthorizationURIPattern pattern = new AuthorizationURIPattern();
		pattern.setPattern(uri);
		pattern.setResource(resource);
		return pattern;
	}
	
	private class AuthorizationToken {
		
		private AuthorizationURIPattern pattern;
		private Set<AuthorizationURIPattern> patterns = new HashSet<AuthorizationURIPattern>();
		private String[] URIs;
		private String[] badURIs;
		
		private AuthorizationToken(final String pattern, final String[] URIs, final String[] badURIs) {
			final AuthorizationURIPattern patternObj = getRandomPattern(pattern);
			
			this.URIs = (URIs != null) ? URIs : new String[0];
			this.badURIs = (badURIs != null) ? badURIs : new String[0];
			
			this.patterns.add(patternObj);
			this.pattern = patternObj;
		}
	}
}
