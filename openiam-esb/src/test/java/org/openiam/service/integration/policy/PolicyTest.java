package org.openiam.service.integration.policy;

import org.apache.commons.collections.CollectionUtils;
import org.junit.AfterClass;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.dto.PolicyConstants;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.service.integration.AbstractAttributeServiceTest;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.openiam.service.integration.AbstractMetadataTypeServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PolicyTest extends AbstractKeyNameServiceTest<Policy, PolicySearchBean> {

	private List<PolicyDefParam> policyAttrList;
	
    @Autowired
    @Qualifier("policyServiceClient")
    private PolicyDataService policyServiceClient;

    @BeforeClass
    protected void _setUp() throws Exception {
    	policyAttrList = policyServiceClient.getAllPolicyAttributes(PolicyConstants.AUTHENTICATION_POLICY, null);
    }

    @AfterClass
    public void _tearDown() throws Exception {
    	
    }

	@Override
	protected Policy newInstance() {
		final Policy policy = new Policy();
		policy.setPolicyDefId(policyAttrList.get(0).getPolicyDefId());
		return policy;
	}

	@Override
	protected PolicySearchBean newSearchBean() {
		return new PolicySearchBean();
	}

	@Override
	protected Response save(Policy t) {
		return policyServiceClient.savePolicy(t);
	}

	@Override
	protected Response delete(Policy t) {
		return policyServiceClient.deletePolicy(t.getPolicyId());
	}

	@Override
	protected Policy get(String key) {
		return policyServiceClient.getPolicy(key);
	}

	@Override
	public List<Policy> find(PolicySearchBean searchBean, int from, int size) {
		return policyServiceClient.findBeans(searchBean, from, size);
	}

	@Override
	protected String getId(Policy bean) {
		return bean.getPolicyId();
	}

	@Override
	protected void setId(Policy bean, String id) {
		bean.setPolicyId(id);
	}

	@Override
	protected void setName(Policy bean, String name) {
		bean.setName(name);
	}

	@Override
	protected String getName(Policy bean) {
		return bean.getName();
	}

	@Override
	protected void setNameForSearch(PolicySearchBean searchBean, String name) {
		searchBean.setName(name);
	}
	
	@Test
	public void clusterTest() throws Exception {
		final ClusterKey<Policy, PolicySearchBean> key = doClusterTest();
		final Policy instance = key.getDto();
		if(instance != null && instance.getPolicyId() != null) {
			deleteAndAssert(instance);
    	}
	}

	@Override
	public ClusterKey<Policy, PolicySearchBean> doClusterTest() throws Exception {
		ClusterKey<Policy, PolicySearchBean> key = super.doClusterTest();
		Policy instance = key.getDto();
		PolicySearchBean searchBean = key.getSearchBean();
		
		for(int i = 0; i < policyAttrList.size(); i++) {
			addAttribute(policyAttrList.get(i), instance);
		}
		instance = assertSave(instance);
		
		/* remove half */
		Set<PolicyAttribute> policyAttributes = new HashSet<>();
		int idx = 0;
		for(final PolicyAttribute attribute : instance.getPolicyAttributes()) {
			policyAttributes.add(attribute);
			if(idx++ > instance.getPolicyAttributes().size()) {
				break;
			}
		}
		instance.setPolicyAttributes(policyAttributes);
		instance = assertSave(instance);
		
		instance.setPolicyAttributes(null);
		for(int i = 0; i < policyAttrList.size(); i++) {
			addAttribute(policyAttrList.get(i), instance);
		}
		instance = assertSave(instance);
		
		instance.setPolicyAttributes(null);
		instance = assertSave(instance);
		return new ClusterKey<Policy, PolicySearchBean>(instance, searchBean);
	}
	
	public Policy assertSave(final Policy instance) {
		final Response wsResponse = super.saveAndAssert(instance);
		final String id = (String)wsResponse.getResponseValue();
		Policy instance1 = get(id);
		Policy instance2 = get(id);
		if(CollectionUtils.isEmpty(instance.getPolicyAttributes())) {
			Assert.assertTrue(CollectionUtils.isEmpty(instance1.getPolicyAttributes()));
			Assert.assertTrue(CollectionUtils.isEmpty(instance2.getPolicyAttributes()));
		} else {
			Assert.assertTrue(CollectionUtils.isNotEmpty(instance1.getPolicyAttributes()), String.format("Empty attributes for %s", instance1));
			Assert.assertTrue(CollectionUtils.isNotEmpty(instance2.getPolicyAttributes()), String.format("Empty attributes for %s", instance2));
			Assert.assertEquals(CollectionUtils.size(instance.getPolicyAttributes()), CollectionUtils.size(instance2.getPolicyAttributes()));
		}
		return instance2;
	}
	
	private void addAttribute(final PolicyDefParam param, final Policy instance) {
		final PolicyAttribute attribute = new PolicyAttribute();
		attribute.setDefParamId(param.getDefParamId());
		attribute.setPolicyId(instance.getPolicyId());
		attribute.setName(getRandomName());
		attribute.setValue1(getRandomName());
		attribute.setValue2(getRandomName());
		attribute.setRule(getRandomName());
		instance.addPolicyAttribute(attribute);
	}
}
