package org.openiam.service.integration.policy;

import org.apache.commons.collections.CollectionUtils;
import org.junit.AfterClass;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
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
		policy.setPolicyDefId(policyAttrList.get(0).getDefParamId());
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
		return policyServiceClient.deletePolicy(t.getId());
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
	public ClusterKey<Policy, PolicySearchBean> doClusterTest() throws Exception {
		ClusterKey<Policy, PolicySearchBean> key = super.doClusterTest();
		Policy instance = key.getDto();
		PolicySearchBean searchBean = key.getSearchBean();
		
		for(final PolicyDefParam param : policyAttrList) {
			final PolicyAttribute attribute = new PolicyAttribute();
			attribute.setDefParamId(param.getDefParamId());
			attribute.setPolicyId(instance.getId());
			attribute.setName(getRandomName());
			attribute.setValue1(getRandomName());
			attribute.setValue2(getRandomName());
			attribute.setRule(getRandomName());
			instance.addPolicyAttribute(attribute);
		}
		saveAndAssert(instance);
		instance = get(instance.getId());
		Policy instance2 = get(instance.getId());
		Assert.assertTrue(CollectionUtils.isNotEmpty(instance.getPolicyAttributes()), String.format("Empty attributes for %s", instance));
		Assert.assertEquals(instance.getPolicyAttributes(), instance2.getPolicyAttributes());
		
		final Set<PolicyAttribute> newAttributeList = new HashSet<>();
		final Iterator<PolicyAttribute> it = instance.getPolicyAttributes().iterator();
		int i = 0;
		while(it.hasNext()) {
			final PolicyAttribute attribute = it.next();
			if(i++ < instance.getPolicyAttributes().size() / 2) {
				newAttributeList.add(attribute);
			}
		}
		instance.setPolicyAttributes(newAttributeList);
		saveAndAssert(instance);
		instance = get(instance.getId());
		instance2 = get(instance.getId());
		Assert.assertTrue(CollectionUtils.isNotEmpty(instance.getPolicyAttributes()), String.format("Empty attributes for %s", instance));
		Assert.assertEquals(instance.getPolicyAttributes(), instance2.getPolicyAttributes());
		
		instance.setPolicyAttributes(null);
		saveAndAssert(instance);
		instance = get(instance.getId());
		instance2 = get(instance.getId());
		Assert.assertTrue(CollectionUtils.isNotEmpty(instance.getPolicyAttributes()), String.format("Empty attributes for %s", instance));
		Assert.assertEquals(instance.getPolicyAttributes(), instance2.getPolicyAttributes());
		return new ClusterKey<Policy, PolicySearchBean>(instance, searchBean);
	}
}
