package org.openiam.service.integration.policy;

import org.apache.commons.collections.CollectionUtils;
import org.junit.AfterClass;
import org.mortbay.log.Log;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.dto.PolicyConstants;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		policy.setPolicyDefId(policyAttrList.get(0).getId());
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
		attribute.setDefParamId(param.getId());
		attribute.setPolicyId(instance.getId());
		attribute.setName(getRandomName());
		attribute.setValue1(getRandomName());
		attribute.setValue2(getRandomName());
		attribute.setRule(getRandomName());
		instance.addPolicyAttribute(attribute);
	}
	
	private PolicySearchBean getCacheableSearchBean(final Policy entity) {
		final PolicySearchBean sb = new PolicySearchBean();
		sb.setFindInCache(true);
		sb.setDeepCopy(true);
		sb.setName(entity.getName());
		return sb;
	}
	
	private Policy createPolicy() {
		Policy entity = createBean();
		final Response response = policyServiceClient.savePolicy(entity);
		assertSuccess(response);
		entity = get((String)response.getResponseValue());
		Assert.assertNotNull(entity);
		return entity;
	}
	
	@Test
	public void testGetPolicyCache() throws Exception {
		for(int j = 0; j < 2; j++) {
			final Policy entity = createPolicy();
			Assert.assertNotNull(get(entity.getId()));
			deleteAndAssert(entity);
			Assert.assertNull(get(entity.getId()));
		}
	}
	
	//TODO.  Fix these test failures on CI.  They fail only on CircleCI.
	@Test(enabled=false)
	public void testSearchBeanCache() throws Exception {
		for(int j = 0; j < 2; j++) {
			final Policy entity = createPolicy();
			final PolicySearchBean sb = getCacheableSearchBean(entity);
			try {
				searchAndAssertCacheHit(sb, entity, "policies");
			} finally {
				deleteAndAssert(entity);
				sleep(1);
				Assert.assertTrue(CollectionUtils.isEmpty(find(sb, 0, Integer.MAX_VALUE)));
			}
		}
	}
	
	@Test(enabled=false)
	public void testSearchBeanCacheAfterSave() {
		final Policy entity = createPolicy();
		final PolicySearchBean sb = getCacheableSearchBean(entity);
		try {
			Log.info(String.format("Search Bean: %s.  Policy:  %s", sb, entity));
			/* trigger and assert cache hit */
			searchAndAssertCacheHit(sb, entity, "policies");
			
			saveAndAssertCachePurge(sb, entity, new String[] {"policies"}, 2, 1);
			
			/* trigger and assert cache hit */
			searchAndAssertCacheHit(sb, entity, "policies");
		} finally {
			deleteAndAssert(entity);
		}
	}
}
