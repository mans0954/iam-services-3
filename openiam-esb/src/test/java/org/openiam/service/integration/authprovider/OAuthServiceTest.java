package org.openiam.service.integration.authprovider;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.openiam.am.srvc.searchbeans.AuthAttributeSearchBean;
import org.openiam.am.srvc.ws.AuthProviderWebService;
import org.openiam.am.srvc.ws.AuthResourceAttributeWebService;
import org.openiam.base.ws.Response;
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
	
	@Value("${org.openiam.auth.provider.type.oauth.id}")
	private String oauthProviderTypeId;
	
	private AuthProvider authProvider;

	@BeforeClass
	public void init() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		final Map<String, String> attributeValues = new HashMap<String, String>();
		attributeValues.put("OAuthAuthorizationGrantFlow", "IMPLICIT");
		attributeValues.put("OAuthClientAuthType", "REQUEST_BODY");
		attributeValues.put("OAuthRedirectUrl", "http://www.google.com");
		attributeValues.put("OAuthTokenExpiration", "30");
		attributeValues.put("OAuthUseRefreshToken", "false");
		//attributeValues.put("OAuthClientScopes", clientScopes);
		attributeValues.put("OAuthClientID", OAuth2Utils.randomClientId());
		attributeValues.put("OAuthClientSecret", OAuth2Utils.randomClientSecret());
		
		final AuthProvider provider = new AuthProvider();
		provider.setName(getRandomName());
		provider.setManagedSysId("0");
		provider.setProviderType(oauthProviderTypeId);
		final Set<AuthProviderAttribute> attributes = new HashSet<AuthProviderAttribute>();
		provider.setAttributes(attributes);
		attributeValues.forEach((attributeId, value) -> {
			final AuthAttributeSearchBean searchBean = new AuthAttributeSearchBean();
			searchBean.setKey(attributeId);
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
	}
	
	@Test
	public void foo() {}
}
