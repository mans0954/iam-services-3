package org.openiam.authentication.integration;

import java.util.HashSet;

import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXrefId;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LoginModuleServiceTest extends AbstractServiceTest {
	
	@Value("${org.openiam.auth.level.kerberos.id}")
	private String kerbAuthId;

	private URIPattern pattern = null;
	private ContentProvider cp = null;
	private User user = null;
	
	@BeforeClass
	public void _init() {
		user = super.createUser();
		cp = super.createContentProvider();
		Response wsResponse = contentProviderServiceClient.saveContentProvider(cp);
		Assert.assertTrue(wsResponse.isSuccess());
		final String contentProviderId = (String)wsResponse.getResponseValue();
		wsResponse = contentProviderServiceClient.createDefaultURIPatterns(contentProviderId);
		Assert.assertTrue(wsResponse.isSuccess());
		cp = contentProviderServiceClient.getContentProvider(contentProviderId);
		pattern = cp.getPatternSet().stream().filter(e -> e.getPattern().equals("/webconsole/*")).findFirst().get();
		pattern.setGroupingXrefs(new HashSet<AuthLevelGroupingURIPatternXref>());
		final AuthLevelGroupingURIPatternXref xref = new AuthLevelGroupingURIPatternXref();
		xref.setId(new AuthLevelGroupingURIPatternXrefId());
		xref.getId().setPatternId(pattern.getId());
		xref.getId().setGroupingId(kerbAuthId);
		pattern.getGroupingXrefs().add(xref);
		wsResponse = contentProviderServiceClient.saveURIPattern(pattern);
		Assert.assertTrue(wsResponse.isSuccess());
		
		refreshAuthorizationManager();
		refreshContentProviderManager();
	}
	
	@AfterClass
	public void _destroy() {
		Response wsResponse = userServiceClient.removeUser(user.getId());
		Assert.assertTrue(wsResponse.isSuccess());
		wsResponse = contentProviderServiceClient.deleteContentProvider(cp.getId());
		Assert.assertTrue(wsResponse.isSuccess());
	}
	
	@Test
	public void testLoginWithKerberosFlag() {
		final LoginResponse loginResponse = loginServiceClient.getPrimaryIdentity(user.getId());
		Assert.assertNotNull(loginResponse);
		Assert.assertTrue(loginResponse.isSuccess());
		Assert.assertNotNull(loginResponse.getPrincipal());
		
		Response wsResponse = loginServiceClient.decryptPassword(user.getId(), loginResponse.getPrincipal().getPassword());
		Assert.assertNotNull(wsResponse);
		Assert.assertTrue(wsResponse.isSuccess());
		final String password = (String)wsResponse.getResponseValue();
		
		final AuthenticationRequest request = new AuthenticationRequest();
		request.setKerberosAuth(true);
		request.setPrincipal(loginResponse.getPrincipal().getLogin());
		request.setPatternId(pattern.getId());
		request.setPassword(null);
		request.setLanguageId("1");
		AuthenticationResponse authResponse = authServiceClient.login(request);
		Assert.assertNotNull(authResponse);
		Assert.assertTrue(authResponse.isSuccess());
		Assert.assertNotNull(authResponse.getSubject());
		Assert.assertNotNull(authResponse.getSubject().getSsoToken());
		
		request.setKerberosAuth(false);
		request.setPatternId(cp.getPatternSet().stream().filter(e -> !e.getId().equals(pattern.getId())).findAny().get().getId());
		authResponse = authServiceClient.login(request);
		Assert.assertNotNull(authResponse);
		Assert.assertTrue(authResponse.isFailure());
		
		request.setPassword(password);
		authResponse = authServiceClient.login(request);
		Assert.assertNotNull(authResponse);
		Assert.assertTrue(authResponse.isSuccess());
		Assert.assertNotNull(authResponse.getSubject());
		Assert.assertNotNull(authResponse.getSubject().getSsoToken());
	}
}
