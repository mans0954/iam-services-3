package org.openiam.service.integration.authprovider;

import java.net.URL;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.srvc.am.AuthProviderWebService;
import org.openiam.srvc.am.AuthResourceAttributeWebService;
import org.openiam.srvc.am.OAuthWebService;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.srvc.am.ResourceDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.base.response.ProvisionUserResponse;
import org.openiam.srvc.idm.ProvisionService;
import org.openiam.security.oauth.util.OAuth2Utils;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OAuthServiceTest extends AbstractServiceTest {
	
	@Autowired
	@Qualifier("authAttributeServiceClient")
	private AuthResourceAttributeWebService authAttributeServiceClient;
	
	@Autowired
	@Qualifier("authProviderServiceClient")
	private AuthProviderWebService authProviderServiceClient;
	@Autowired
	@Qualifier("oauthServiceClient")
	private OAuthWebService oauthServiceClient;
	@Autowired
	@Qualifier("resourceServiceClient")
	private ResourceDataService resourceServiceClient;

	@Autowired
	@Qualifier("provisionServiceClient")
	protected ProvisionService provisionService;



	@Value("${org.openiam.auth.provider.type.oauth.id}")
	private String oauthProviderTypeId;
	
	private AuthProvider authProvider;

	private List<Resource> resourceList;
	private List<Resource> roleResourceList;

	private User testUser;
	private Role role;

	private String clientID;
	private String OAuthClientSecret;

	private Map<String, String> attributeValues = new HashMap<String, String>();

	private static final int MAX_SCOPE_LIST_SIZE=10;
	private static final int MAX_ROLE_SCOPE_LIST_SIZE=2;
	private static final int SCOPES_TO_DELETE=2;


	@BeforeClass
	public void init() throws Exception {
		ResourceSearchBean rsb = new ResourceSearchBean();
		rsb.setResourceTypeId("URL_PATTERN");

		resourceList = resourceServiceClient.findBeans(rsb,0,MAX_SCOPE_LIST_SIZE,null);
		StringBuilder scopeBuilder = new StringBuilder();

		Assert.assertEquals(CollectionUtils.isNotEmpty(resourceList), true, "Cannot find any scope");

		String userId = doCreateUser();
		for(Resource r: resourceList){
			if(scopeBuilder.length()>0){
				scopeBuilder.append(",");
			}
			scopeBuilder.append(r.getId());
			testUser.addResourceWithRights(r, null,null,null);
		}
		saveAndAssert(testUser);

		rsb = new ResourceSearchBean();
		rsb.setResourceTypeId("OAUTH_SCOPE");
		roleResourceList = resourceServiceClient.findBeans(rsb,0,MAX_ROLE_SCOPE_LIST_SIZE,null);
		role = createRole();
		for(Resource r: roleResourceList){
			resourceServiceClient.addRoleToResource(r.getId(), role.getId(), "3000", null, null, null);
		}
		refreshAuthorizationManager();

		attributeValues = new HashMap<String, String>();
		attributeValues.put("OAuthAuthorizationGrantFlow", "IMPLICIT");
		attributeValues.put("OAuthClientAuthType", "REQUEST_BODY");
		attributeValues.put("OAuthRedirectUrl", "http://www.google.com");
		attributeValues.put("OAuthTokenExpiration", "30");
		attributeValues.put("OAuthUseRefreshToken", "false");
		attributeValues.put("OAuthClientScopes", scopeBuilder.toString());

		clientID = OAuth2Utils.randomClientId();
		OAuthClientSecret=OAuth2Utils.randomClientSecret();
		attributeValues.put("OAuthClientID", clientID);
		attributeValues.put("OAuthClientSecret", OAuthClientSecret);
		
		final AuthProvider provider = new AuthProvider();
		provider.setName(getRandomName());
		provider.setManagedSysId("0");
		provider.setProviderType(oauthProviderTypeId);
		provider.setAttributes(getAttributeList());

		final Response wsResponse = authProviderServiceClient.saveAuthProvider(provider, getRequestorId());
		Assert.assertNotNull(wsResponse);
		Assert.assertTrue(wsResponse.isSuccess());
		final String providerId = (String)wsResponse.getResponseValue();
		authProvider = authProviderServiceClient.getAuthProvider(providerId);
		Assert.assertNotNull(authProvider);
	}
	
	@AfterClass
	public void destroy() {
		if(authProvider != null) {
			final Response wsResponse = authProviderServiceClient.deleteAuthProvider(authProvider.getId());
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		}
		if(testUser!=null){
			final Response wsResponse = userServiceClient.deleteUser(testUser.getId());
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		}
		if(role!=null){
			final Response wsResponse = roleServiceClient.removeRole(role.getId());
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		}
	}
	
	@Test
	public void testOAuthProvider() {
		// get oauth client by client id
		AuthProvider provider = oauthServiceClient.getClient(clientID);
		Assert.assertNotNull(provider);
		Assert.assertTrue(provider.getId().equals(authProvider.getId()));

		// get scope for authorization should be MAX_SCOPE_LIST_SIZE
		OAuthScopesResponse scopesResponse = oauthServiceClient.getScopesForAuthrorization(clientID, testUser.getId(), null);
		Assert.assertNotNull(scopesResponse);
		Assert.assertTrue(clientID.equals(scopesResponse.getClientId()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(scopesResponse.getList()));
		Assert.assertTrue(new Integer(MAX_SCOPE_LIST_SIZE).equals(scopesResponse.getList().size()));
		// do authorization
		doAuthorization(scopesResponse);

		// get authorized scopes should be MAX_SCOPE_LIST_SIZE
		List<Resource> authorizedScopes = oauthServiceClient.getAuthorizedScopesByUser(clientID, testUser.getId(), null);
		Assert.assertTrue(CollectionUtils.isNotEmpty(authorizedScopes));
		Assert.assertTrue(new Integer(MAX_SCOPE_LIST_SIZE).equals(authorizedScopes.size()));
		// remove SCOPES_TO_DELETE scope from client
		List<Resource> newScopes = new ArrayList<>(resourceList);
		for(int i=0;i<SCOPES_TO_DELETE;i++){
			newScopes.remove(newScopes.size()-1);
		}
		updateScopeAttribute(provider, buildScopeAttributeValue(newScopes));

		Response wsResponse = authProviderServiceClient.saveAuthProvider(provider, getRequestorId());
		Assert.assertNotNull(wsResponse);
		Assert.assertTrue(wsResponse.isSuccess());

		// get authorized scopes should be MAX_SCOPE_LIST_SIZE-SCOPES_TO_DELETE
		authorizedScopes = oauthServiceClient.getAuthorizedScopesByUser(clientID, testUser.getId(), null);
		Assert.assertTrue(CollectionUtils.isNotEmpty(authorizedScopes));
		Assert.assertTrue(new Integer(MAX_SCOPE_LIST_SIZE-SCOPES_TO_DELETE).equals(authorizedScopes.size()));

		// get scope for authorization should be 0
		scopesResponse = oauthServiceClient.getScopesForAuthrorization(clientID, testUser.getId(), null);
		Assert.assertNotNull(scopesResponse);
		Assert.assertTrue(clientID.equals(scopesResponse.getClientId()));
		Assert.assertTrue(CollectionUtils.isEmpty(scopesResponse.getList()));

		// add 2 more scopes (restore previous scopes)
		updateScopeAttribute(provider, buildScopeAttributeValue(new ArrayList<>(resourceList)));
		wsResponse = authProviderServiceClient.saveAuthProvider(provider, getRequestorId());
		Assert.assertNotNull(wsResponse);
		Assert.assertTrue(wsResponse.isSuccess());

		// get scope for authorization should be 2
		scopesResponse = oauthServiceClient.getScopesForAuthrorization(clientID, testUser.getId(), null);
		Assert.assertNotNull(scopesResponse);
		Assert.assertTrue(clientID.equals(scopesResponse.getClientId()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(scopesResponse.getList()));
		Assert.assertTrue(new Integer(SCOPES_TO_DELETE).equals(scopesResponse.getList().size()));
		// do authorization
		doAuthorization(scopesResponse);
		// get authorized scopes should be 10
		authorizedScopes = oauthServiceClient.getAuthorizedScopesByUser(clientID, testUser.getId(), null);
		Assert.assertTrue(CollectionUtils.isNotEmpty(authorizedScopes));
		Assert.assertTrue(new Integer(MAX_SCOPE_LIST_SIZE).equals(authorizedScopes.size()));

		oauthServiceClient.deAuthorizeClient(clientID, testUser.getId());
	}

	@Test
	public void testOAuthProviderCleanAuthorizedScopes() {
		// get oauth client by client id
		AuthProvider provider = oauthServiceClient.getClient(clientID);
		Assert.assertNotNull(provider);
		Assert.assertTrue(provider.getId().equals(authProvider.getId()));

		// get scope for authorization should be MAX_SCOPE_LIST_SIZE
		OAuthScopesResponse scopesResponse = oauthServiceClient.getScopesForAuthrorization(clientID, testUser.getId(), null);
		Assert.assertNotNull(scopesResponse);
		Assert.assertTrue(clientID.equals(scopesResponse.getClientId()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(scopesResponse.getList()));
		Assert.assertTrue(new Integer(MAX_SCOPE_LIST_SIZE).equals(scopesResponse.getList().size()));
		// do authorization
		doAuthorization(scopesResponse);

		// get authorized scopes should be MAX_SCOPE_LIST_SIZE
		List<Resource> authorizedScopes = oauthServiceClient.getAuthorizedScopesByUser(clientID, testUser.getId(), null);
		Assert.assertTrue(CollectionUtils.isNotEmpty(authorizedScopes));
		Assert.assertTrue(new Integer(MAX_SCOPE_LIST_SIZE).equals(authorizedScopes.size()));

		// remove SCOPES_TO_DELETE from user
		List<Resource> deletedResource = new ArrayList<>(SCOPES_TO_DELETE);
		for(int i=0;i<SCOPES_TO_DELETE;i++){
			deletedResource.add(resourceList.get(i));
			testUser.removeResource(resourceList.get(i));
		}
		saveAndAssert(testUser);
		refreshAuthorizationManager();
		cleanAuthorizedScopes();
		// get authorized scopes should be MAX_SCOPE_LIST_SIZE-SCOPES_TO_DELETE
		authorizedScopes = oauthServiceClient.getAuthorizedScopesByUser(clientID, testUser.getId(), null);
		Assert.assertTrue(CollectionUtils.isNotEmpty(authorizedScopes));
		Assert.assertTrue(new Integer(MAX_SCOPE_LIST_SIZE-SCOPES_TO_DELETE).equals(authorizedScopes.size()));
		authorizedScopes.forEach(s ->{
			for(Resource dr : deletedResource){
				Assert.assertTrue(!s.getId().equals(dr.getId()), String.format("Scope %s should not be authorized", dr.getId()));
			}
		});

		// add resources back to the user
		deletedResource.forEach(dr ->{
			testUser.addResourceWithRights(dr, null,null,null);
		});
		saveAndAssert(testUser);
		refreshAuthorizationManager();
		// get scope for authorization should be deletedResource.size()
		scopesResponse = oauthServiceClient.getScopesForAuthrorization(clientID, testUser.getId(), null);
		Assert.assertNotNull(scopesResponse);
		Assert.assertTrue(clientID.equals(scopesResponse.getClientId()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(scopesResponse.getList()));
		Assert.assertTrue(new Integer(SCOPES_TO_DELETE).equals(scopesResponse.getList().size()));
		doAuthorization(scopesResponse);
		// get scope for authorization should be 0
		scopesResponse = oauthServiceClient.getScopesForAuthrorization(clientID, testUser.getId(), null);
		Assert.assertNotNull(scopesResponse);
		Assert.assertTrue(clientID.equals(scopesResponse.getClientId()));
		Assert.assertTrue(CollectionUtils.isEmpty(scopesResponse.getList()));

		// add scope to client
		List<Resource> newScopes = new ArrayList<>(resourceList);
		newScopes.addAll(roleResourceList);

		updateScopeAttribute(provider, buildScopeAttributeValue(newScopes));
		Response wsResponse = authProviderServiceClient.saveAuthProvider(provider, getRequestorId());
		Assert.assertNotNull(wsResponse);
		Assert.assertTrue(wsResponse.isSuccess());


		// add role with 2 resources to the user
		testUser.addRoleWithRights(role, null, null, null);
		saveAndAssert(testUser);
		refreshAuthorizationManager();
		// get scope for authorization should be roleResourceList.size()
		scopesResponse = oauthServiceClient.getScopesForAuthrorization(clientID, testUser.getId(), null);
		Assert.assertNotNull(scopesResponse);
		Assert.assertTrue(clientID.equals(scopesResponse.getClientId()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(scopesResponse.getList()));
		Assert.assertTrue(new Integer(roleResourceList.size()).equals(scopesResponse.getList().size()));
		doAuthorization(scopesResponse);
		// get scope for authorization should be 0
		scopesResponse = oauthServiceClient.getScopesForAuthrorization(clientID, testUser.getId(), null);
		Assert.assertNotNull(scopesResponse);
		Assert.assertTrue(clientID.equals(scopesResponse.getClientId()));
		Assert.assertTrue(CollectionUtils.isEmpty(scopesResponse.getList()));
		// get authorized scopes should be MAX_SCOPE_LIST_SIZE+roleResourceList.size()
		authorizedScopes = oauthServiceClient.getAuthorizedScopesByUser(clientID, testUser.getId(), null);
		Assert.assertTrue(CollectionUtils.isNotEmpty(authorizedScopes));
		Assert.assertTrue(new Integer(MAX_SCOPE_LIST_SIZE+roleResourceList.size()).equals(authorizedScopes.size()));

		// now remove role from user and check once more
		testUser.removeRole(role.getId());
		saveAndAssert(testUser);
		refreshAuthorizationManager();
		cleanAuthorizedScopes();

		// get authorized scopes should be MAX_SCOPE_LIST_SIZE
		authorizedScopes = oauthServiceClient.getAuthorizedScopesByUser(clientID, testUser.getId(), null);
		Assert.assertTrue(CollectionUtils.isNotEmpty(authorizedScopes));
		Assert.assertTrue(new Integer(MAX_SCOPE_LIST_SIZE).equals(authorizedScopes.size()));

		oauthServiceClient.deAuthorizeClient(clientID, testUser.getId());
	}

	private void cleanAuthorizedScopes(){
		try {
			oauthServiceClient.cleanAuthorizedScopes();
			Thread.sleep(500L);
		} catch (Exception e) {
			logger.error("Can't refresh auth manager", e);
			throw new RuntimeException(e);
		}
	}

	private void updateScopeAttribute(AuthProvider provider, String scopeAttributeValue) {
		if(CollectionUtils.isNotEmpty(provider.getAttributes())){
			for(AuthProviderAttribute attr: provider.getAttributes()){
				if("OAuthClientScopes".equals(attr.getAttributeId())){
					attr.setValue(scopeAttributeValue);
					break;
				}
			}
		}
	}

	private String buildScopeAttributeValue(List<Resource> scopeList){
		StringBuilder scopeBuilder = new StringBuilder();
		scopeBuilder = new StringBuilder();
		for(Resource r: scopeList){
			if(scopeBuilder.length()>0){
				scopeBuilder.append(",");
			}
			scopeBuilder.append(r.getId());
		}
		return scopeBuilder.toString();
	}

	private Set<AuthProviderAttribute> getAttributeList(){
		final Set<AuthProviderAttribute> attributes = new HashSet<AuthProviderAttribute>();
		attributeValues.forEach((attributeId, value) -> {
			final AuthAttributeSearchBean searchBean = new AuthAttributeSearchBean();
			searchBean.addKey(attributeId);
			final List<AuthAttribute> authAttributeList = authProviderServiceClient.findAuthAttributeBeans(searchBean, 0, 1);
			if(CollectionUtils.isNotEmpty(authAttributeList)) {
				final AuthAttribute authAttribute = authAttributeList.get(0);

				/* set value and value types */
				final AuthProviderAttribute attribute = new AuthProviderAttribute();
				attribute.setAttributeId(attributeId);
				attribute.setValue(value);
				attribute.setDataType(authAttribute.getDataType());
				attribute.setAttributeName(authAttribute.getName());
				attributes.add(attribute);
			}
		});
		return attributes;
	}

	private void doAuthorization(OAuthScopesResponse scopesResponse){
		List<OAuthUserClientXref> authScopes = new ArrayList<OAuthUserClientXref>();
		for(Resource scope : scopesResponse.getList()){
			OAuthUserClientXref xref = new OAuthUserClientXref();
			xref.setClientId(clientID);
			xref.setUserId(testUser.getId());
			xref.setScopeId(scope.getId());
			xref.setIsAllowed(true);
			authScopes.add(xref);
		}
		Response wsResponse =  oauthServiceClient.saveClientScopeAuthorization(authProvider.getId(), testUser.getId(), authScopes);
		Assert.assertNotNull(wsResponse);
		Assert.assertTrue(wsResponse.isSuccess());
	}

	protected User createBean() {
		final User bean =  new User();
		bean.setFirstName(getRandomName());
		bean.setLastName(getRandomName());
		return bean;
	}

	protected String doCreateUser() throws Exception{
		User user = createBean();
		testUser = ((ProvisionUserResponse)saveAndAssert(user)).getUser();
		return testUser.getId();
	}

	protected Response saveAndAssert(User user) {
		final Response response = save(user);
		Assert.assertTrue(response.isSuccess(), String.format("Could not save entity.  %s", response));

		ProvisionUserResponse userResponse = (ProvisionUserResponse)response;
		Assert.assertNotNull(userResponse.getUser(), String.format("Could not save entity.  %s", userResponse));
		Assert.assertNotNull(userResponse.getUser().getId(), String.format("Could not save entity.  %s", userResponse));
		return response;
	}
	protected Response save(User user) {
		ProvisionUserResponse userResponse = null;
		if(StringUtils.isNotBlank(user.getId())){
			userResponse = provisionService.modifyUser(new ProvisionUser(user));
		} else {
			try {
				userResponse = provisionService.addUser(new ProvisionUser(user));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userResponse;
	}

}
