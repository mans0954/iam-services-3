package org.openiam.service.integration.authprovider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.openiam.am.srvc.constants.AuthAttributeDataType;
import org.openiam.am.srvc.constants.SsoAttributeType;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.am.srvc.ws.AuthProviderWebService;
import org.openiam.am.srvc.ws.AuthResourceAttributeWebService;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class AuthProviderServiceTest extends AbstractKeyNameServiceTest<AuthProvider, AuthProviderSearchBean> {
	
	@Autowired
	@Qualifier("authAttributeServiceClient")
	private AuthResourceAttributeWebService authAttributeServiceClient;
	
	@Autowired
	@Qualifier("authProviderServiceClient")
	private AuthProviderWebService authProviderServiceClient;
	
	@Autowired
	@Qualifier("managedSysServiceClient")
	private ManagedSystemWebService managedSysServiceClient;
	
	private Map<String, AuthResourceAttributeMap> getNewResourceAttributeMap(final AuthProvider provider) {
		final Map<String, AuthResourceAttributeMap> resourceAttributeMap = new HashMap<>();
		for(final AuthResourceAMAttribute attribute : authAttributeServiceClient.getAmAttributeList()) {
			final AuthResourceAttributeMap mapAttribute = new AuthResourceAttributeMap();
			mapAttribute.setProviderId(provider.getId());
			mapAttribute.setName(getRandomName());
			mapAttribute.setAttributeValue(getRandomName());
			mapAttribute.setAttributeType(SsoAttributeType.String);
			mapAttribute.setAmResAttributeId(attribute.getId());
			mapAttribute.setAmPolicyUrl(getRandomName());
			final Response response = authAttributeServiceClient.saveAttributeMap(mapAttribute);
			Assert.assertTrue(response.isSuccess());
			final AuthResourceAttributeMap attribute1 = authAttributeServiceClient.getAttribute((String)response.getResponseValue());
			final AuthResourceAttributeMap attribute2 = authAttributeServiceClient.getAttribute((String)response.getResponseValue());
			Assert.assertNotNull(attribute1);
			Assert.assertNotNull(attribute2);
			Assert.assertEquals(attribute1, attribute2);
			
			resourceAttributeMap.put(attribute1.getName(), attribute1);
		}
		return resourceAttributeMap;
	}
	
	@Test
	public void clusterTest() throws Exception {
		AuthProvider instance = null;
		
		try {
			final ClusterKey<AuthProvider, AuthProviderSearchBean> key = super.doClusterTest();
			instance  = key.getDto();
			Map<String, AuthResourceAttributeMap> resourceAttributeMap = getNewResourceAttributeMap(instance);
			instance.setResourceAttributeMap(resourceAttributeMap);
			saveAndAssert(instance);
			instance = get(instance.getId());
			int size = instance.getResourceAttributeMap().size();
			int idx = 0;
			for(Iterator<Map.Entry<String, AuthResourceAttributeMap>> it = instance.getResourceAttributeMap().entrySet().iterator(); it.hasNext();) {
				final Map.Entry<String, AuthResourceAttributeMap> entry = it.next();
				final AuthResourceAttributeMap value = entry.getValue();
				if(idx < size / 2) {
					final Response response = authAttributeServiceClient.removeAttributeMap(value.getId());
					Assert.assertTrue(response.isSuccess());
					Assert.assertNull(authAttributeServiceClient.getAttribute(value.getId()));
					Assert.assertNull(authAttributeServiceClient.getAttribute(value.getId()));
					it.remove();
				}
			}
			saveAndAssert(instance);
			instance = get(instance.getId());
			instance.setResourceAttributeMap(null);
			saveAndAssert(instance);
			instance = get(instance.getId());
			
			resourceAttributeMap = getNewResourceAttributeMap(instance);
			instance.setResourceAttributeMap(resourceAttributeMap);
			saveAndAssert(instance);
			
			final List<AuthAttribute> authAttributes = authProviderServiceClient.findAuthAttributeBeans(null, 0, Integer.MAX_VALUE);
			final Set<AuthProviderAttribute> attributes = new HashSet<>(instance.getAttributes());
			int numOfAttributes = attributes.size();
			for(int i = 0; i < authAttributes.size(); i++) {
				final AuthAttribute authAttribute = authAttributes.get(i);
				if(!authAttribute.isRequired()) {
					for(final Iterator<AuthProviderAttribute> it = attributes.iterator(); it.hasNext();) {
						final AuthProviderAttribute attribute = it.next();
						if(StringUtils.equals(attribute.getAttributeId(), authAttribute.getId())) {
							it.remove();
							numOfAttributes--;
							break;
						}
					}
				}
			}
			instance.setAttributes(attributes);
			saveAndAssert(instance);
			instance = get(instance.getId());
			Assert.assertEquals(instance.getAttributes().size(), numOfAttributes);
		} finally {
			if(instance != null && instance.getId() != null) {
				deleteAndAssert(instance);
	    	}
		}
	}
	
	@Override
	protected Response saveAndAssert(final AuthProvider instance) {
		final Response response = super.saveAndAssert(instance);
		final String id = (String)response.getResponseValue();
		final AuthProvider instance1 = get(id);
		final AuthProvider instance2 = get(id);
		
		Assert.assertNotNull(instance1);
		Assert.assertNotNull(instance2);
		if(MapUtils.isNotEmpty(instance.getResourceAttributeMap())) {
			Assert.assertTrue(MapUtils.isNotEmpty(instance1.getResourceAttributeMap()));
			Assert.assertTrue(MapUtils.isNotEmpty(instance2.getResourceAttributeMap()));
			Assert.assertEquals(instance.getResourceAttributeMap().size(), instance1.getResourceAttributeMap().size());
			Assert.assertEquals(instance.getResourceAttributeMap().size(), instance2.getResourceAttributeMap().size());
		} else {
			Assert.assertTrue(MapUtils.isEmpty(instance1.getResourceAttributeMap()));
			Assert.assertTrue(MapUtils.isEmpty(instance2.getResourceAttributeMap()));
		}
		if(CollectionUtils.isNotEmpty(instance.getAttributes())) {
			Assert.assertTrue(CollectionUtils.isNotEmpty(instance1.getAttributes()));
			Assert.assertTrue(CollectionUtils.isNotEmpty(instance2.getAttributes()));
			Assert.assertEquals(instance.getAttributes().size(), instance1.getAttributes().size());
			Assert.assertEquals(instance.getAttributes().size(), instance2.getAttributes().size());
		} else {
			Assert.assertTrue(CollectionUtils.isEmpty(instance1.getAttributes()));
			Assert.assertTrue(CollectionUtils.isEmpty(instance2.getAttributes()));
		}
		return response;
	}

	@Override
	protected AuthProvider newInstance() {
		final AuthProvider provider = new AuthProvider();
		provider.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
		provider.setPrivateKey(new byte[] {'a', 'b', 'c', 'd'});
		provider.setPublicKey(new byte[] {'e', 'f', 'g', 'h'});
		provider.setProviderType(authProviderServiceClient.getAuthProviderTypeList().get(0).getId());
		provider.setSignRequest(true);
		
		final List<AuthAttribute> authAttributes = authProviderServiceClient.findAuthAttributeBeans(null, 0, Integer.MAX_VALUE);
		//AuthAttributeEntity
		final Set<AuthProviderAttribute> attributes = new HashSet<>();
		for(int i = 0; i < authAttributes.size(); i++) {
			final AuthAttribute authAttribute = authAttributes.get(i);
			final AuthProviderAttribute attribute = new AuthProviderAttribute();
			attribute.setAttributeId(authAttribute.getId());
			attribute.setDataType(AuthAttributeDataType.singleValue);
			attribute.setDefaultValue(getRandomName());
			attribute.setValue(getRandomName());
			attributes.add(attribute);
		}
		provider.setAttributes(attributes);
		return provider;
	}

	@Override
	protected AuthProviderSearchBean newSearchBean() {
		final AuthProviderSearchBean searchBean = new AuthProviderSearchBean();
		searchBean.setDeepCopy(true);
		return searchBean;
	}

	@Override
	protected Response save(AuthProvider t) {
		return authProviderServiceClient.saveAuthProvider(t, null);
	}

	@Override
	protected Response delete(AuthProvider t) {
		return authProviderServiceClient.deleteAuthProvider(t.getId());
	}

	@Override
	protected AuthProvider get(String key) {
		final AuthProviderSearchBean searchBean = new AuthProviderSearchBean();
		searchBean.setKey(key);
		searchBean.setDeepCopy(true);
		final List<AuthProvider> providers = find(searchBean, 0, 1);
		return (CollectionUtils.isNotEmpty(providers)) ? providers.get(0) : null;
	}

	@Override
	public List<AuthProvider> find(AuthProviderSearchBean searchBean, int from,
			int size) {
		searchBean.setDeepCopy(true);
		final List<AuthProvider> results = authProviderServiceClient.findAuthProviderBeans(searchBean, from, size);
		return results;
	}

}
