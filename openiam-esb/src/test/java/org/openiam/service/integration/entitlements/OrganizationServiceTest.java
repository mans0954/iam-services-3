package org.openiam.service.integration.entitlements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.org.service.OrganizationTypeDataService;
import org.openiam.service.integration.AbstractAttributeServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class OrganizationServiceTest extends AbstractAttributeServiceTest<Organization, OrganizationSearchBean, OrganizationAttribute> {
	
	private static final int CACHE_IMPROVEMENT_FACTOR = 2;
	
	@Autowired
	@Qualifier("organizationServiceClient")
	private OrganizationDataService organizationServiceClient;
	
	@Autowired
	@Qualifier("organizationTypeClient")
	private OrganizationTypeDataService organizationTypeClient;

	@Override
	protected OrganizationAttribute createAttribute(Organization t) {
		final OrganizationAttribute attribute = new OrganizationAttribute();
		attribute.setName(getRandomName());
		attribute.setValue(getRandomName());
		attribute.setOrganizationId(t.getId());
		return attribute;
	}

	@Override
	protected Set<OrganizationAttribute> createAttributeSet() {
		return new HashSet<>();
	}

	@Override
	protected void setAttributes(Organization t, Set<OrganizationAttribute> attributes) {
		t.setAttributes(attributes);
	}

	@Override
	protected Set<OrganizationAttribute> getAttributes(Organization t) {
		return t.getAttributes();
	}

	@Override
	protected Organization newInstance() {
		final Organization organization = new Organization();
		organization.setOrganizationTypeId(organizationTypeClient.findBeans(new OrganizationTypeSearchBean(), 0, 1, null).get(0).getId());
		return organization;
	}

	@Override
	protected OrganizationSearchBean newSearchBean() {
		return new OrganizationSearchBean();
	}

	@Override
	protected Response save(Organization t) {
		return organizationServiceClient.saveOrganization(t, null);
	}

	@Override
	protected Response delete(Organization t) {
		return organizationServiceClient.deleteOrganization(t.getId(), null);
	}

	@Override
	protected Organization get(String key) {
		return organizationServiceClient.getOrganizationLocalized(key, null, null);
	}

	@Override
	public List<Organization> find(OrganizationSearchBean searchBean, int from,
			int size) {
		return organizationServiceClient.findBeansLocalized(searchBean, null, from, size, null);
	}

/*	@Override
	protected String getId(Organization bean) {
		return bean.getId();
	}

	@Override
	protected void setId(Organization bean, String id) {
		bean.setId(id);
	}

	@Override
	protected void setName(Organization bean, String name) {
		bean.setName(name);
	}

	@Override
	protected String getName(Organization bean) {
		return bean.getName();
	}

	@Override
	protected void setNameForSearch(OrganizationSearchBean searchBean, String name) {
		searchBean.setName(name);
	}*/

	@Test
	public void testContraintViolationWithNoManagedSystem() {
		final String name = getRandomName();
		Organization r1 = newInstance();
		Organization r2 = newInstance();
		
		r1.setName(name);
		r2.setName(name);
		Response response = organizationServiceClient.saveOrganization(r1, getRequestorId());
		assertSuccess(response);
		response = organizationServiceClient.saveOrganization(r2, getRequestorId());
		assertResponseCode(response, ResponseCode.CONSTRAINT_VIOLATION);
	}
	
	@Test
	public void testContraintViolationWithManagedSystem() {
		final String name = getRandomName();
		final String typeId = organizationTypeClient.findBeans(null, 0, 10, null).get(0).getId();
		Organization r1 = newInstance();
		Organization r2 = newInstance();
		
		r1.setName(name);
		r1.setOrganizationTypeId(typeId);
		r2.setName(name);
		r2.setOrganizationTypeId(typeId);
		Response response = organizationServiceClient.saveOrganization(r1, getRequestorId());
		assertSuccess(response);
		response = organizationServiceClient.saveOrganization(r2, getRequestorId());
		assertResponseCode(response, ResponseCode.CONSTRAINT_VIOLATION);
	}
	
	@Test
	public void testContraintViolationDifferentManagedSystem() {
		final String name = getRandomName();
		Organization r1 = newInstance();
		Organization r2 = newInstance();
		
		r1.setName(name);
		r1.setOrganizationTypeId(organizationTypeClient.findBeans(null, 0, 10, null).get(0).getId());
		r2.setName(name);
		r2.setOrganizationTypeId(organizationTypeClient.findBeans(null, 0, 10, null).get(1).getId());
		Response response = organizationServiceClient.saveOrganization(r1, getRequestorId());
		assertSuccess(response);
		response = organizationServiceClient.saveOrganization(r2, getRequestorId());
		assertSuccess(response);
	}

@Test
	public void testHibernateCache() {
		Organization instance = newInstance();
		try {
			instance.setName(getRandomName());
			Response response = save(instance);
	
			final String id = (String)response.getResponseValue();
			instance = get(id);
			instance = get(id);
		} catch(Throwable e) {
			if(instance != null) {
				delete(instance);
			}
		}
	}
	

/**
	 * assumes that find() calls organizationService.findBeansDto, at some point
	 * @throws Exception 
	 */

	@Test
	public void testOrganizationCache() throws Exception {
		final String typeId = "DEPARTMENT";
		OrganizationSearchBean sb = new OrganizationSearchBean();
		sb.setFindInCache(true);
		sb.setDeepCopy(true);
		sb.setOrganizationTypeId(typeId);
		
		final StopWatch sw = new StopWatch();
		sw.start();
		List<Organization> orgs = find(sb, 0, Integer.MAX_VALUE);
		sw.stop();
		long time = sw.getTime();
		

/* flush cache */

		Assert.assertTrue(CollectionUtils.isNotEmpty(orgs));
		final Organization org = get(orgs.get(0).getId());
		saveAndAssert(org);
		

/* cache miss */

		sw.reset();
		sw.start();
		orgs = find(sb, 0, Integer.MAX_VALUE);
		sw.stop();
		time = sw.getTime();
/* cache hits */

		for(int i = 0; i < 100; i++) {
			sw.reset();
			sw.start();
			orgs = find(sb, 0, Integer.MAX_VALUE);
			sw.stop();
			//Assert.assertTrue(String.format("Cache hit took %s, cache miss took %s.  Cache hit should have been much faster", time, sw.getTime()), (time / CACHE_IMPROVEMENT_FACTOR) > sw.getTime());
		}
		
		//repeat
		

/* flush cache */

		saveAndAssert(org);
		

/* cache miss */

		sw.reset();
		sw.start();
		orgs = find(sb, 0, Integer.MAX_VALUE);
		sw.stop();
		time = sw.getTime();
		

/* cache hits */

		for(int i = 0; i < 100; i++) {
			sw.reset();
			sw.start();
			orgs = find(sb, 0, Integer.MAX_VALUE);
			sw.stop();
			//Assert.assertTrue(String.format("Cache hit took %s, cache miss took %s.  Cache hit should have been much faster", time, sw.getTime()), (time / CACHE_IMPROVEMENT_FACTOR) > sw.getTime());
		}
		
		long lastCacheHitTime = sw.getTime();
		
		sb.setFindInCache(false);
		
		sw.reset();
		sw.start();
		orgs = find(sb, 0, Integer.MAX_VALUE);
		sw.stop();




		// more time taken!!!!!!!!!
		//Assert.assertTrue(String.format("  Cache hit should have been much faster.  Cache miss time should have taken much longer", lastCacheHitTime, sw.getTime()), sw.getTime() > lastCacheHitTime * CACHE_IMPROVEMENT_FACTOR);
	}

}

