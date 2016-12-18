package org.openiam.service.integration.entitlements;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.openiam.base.Tuple;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.dto.ResourceRisk;
import org.openiam.srvc.am.ResourceDataService;
import org.openiam.service.integration.AbstractAttributeServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class ResourceServiceTest extends AbstractAttributeServiceTest<Resource, ResourceSearchBean, ResourceProp> {

	@Autowired
	@Qualifier("resourceServiceClient")
    protected ResourceDataService resourceDataService;
	
	@Override
	protected ResourceProp createAttribute(Resource t) {
		final ResourceProp attribute = new ResourceProp();
		attribute.setResourceId(t.getId());
		return attribute;
	}

	@Override
	protected Set<ResourceProp> createAttributeSet() {
		return new HashSet<>();
	}

	@Override
	protected void setAttributes(Resource t, Set<ResourceProp> attributes) {
		t.setResourceProps(attributes);
	}

	@Override
	protected Set<ResourceProp> getAttributes(Resource t) {
		return t.getResourceProps();
	}

	@Override
	protected Resource newInstance() {
		final Resource resource = new Resource();
		resource.setResourceType(resourceDataService.findResourceTypes(null, 0, Integer.MAX_VALUE, null).stream().filter(e -> e.isSupportsHierarchy()).findFirst().get());
		resource.setCoorelatedName(getRandomName());
		resource.setDescription(getRandomName());
		resource.setDisplayOrder(RandomUtils.nextInt(100));
		resource.setGroovyScript(getRandomName());
		resource.setIsPublic(true);
		resource.setReferenceId(getRandomName());
		resource.setRisk(ResourceRisk.HIGH);
		resource.setTestRequest(true);
		resource.setURL(getRandomName());
		return resource;
	}

	@Override
	protected ResourceSearchBean newSearchBean() {
		return new ResourceSearchBean();
	}

	@Override
	protected Response save(Resource t) {
		return resourceDataService.saveResource(t);
	}

	@Override
	protected Response delete(Resource t) {
		return resourceDataService.deleteResource(t.getId());
	}

	@Override
	protected Resource get(String key) {
		return resourceDataService.getResource(key, null);
	}

	@Override
	public List<Resource> find(ResourceSearchBean searchBean, int from, int size) {
		return resourceDataService.findBeans(searchBean, from, size, null);
	}
	
	@Test
	public void testAddChildResourceNoRange() {
		testAddChildResource(null, null);
	}
	
	@Test
	public void testAddChildResourceWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testAddChildResource(now, tomorrow);
	}

	
	private void testAddChildResource(final Date startDate, final Date endDate) {
		Tuple<Resource, Resource> t = null;
		try {
			addChildResource(startDate, endDate);
		} finally {
			if(t != null) {
				delete(t.getValue());
				delete(t.getKey());
			}
		}
	}
	
	@Test
	public void testRemoveChildResourceNoRange() {
		testRemoveChildResource(null, null);
	}
	
	@Test
	public void testRemoveChildResourceWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testRemoveChildResource(now, tomorrow);
	}
	
	private void testRemoveChildResource(final Date startDate, final Date endDate) {
		Tuple<Resource, Resource> t = null;
		try {
			t = addChildResource(startDate, endDate);
			Response response = resourceDataService.deleteChildResource(t.getKey().getId(), t.getValue().getId());
			Assert.assertTrue(String.format("Can't delete resource: %s", response), response.isSuccess());
		} finally {
			if(t != null) {
				delete(t.getValue());
				delete(t.getKey());
			}
		}
	}
	
	@Test
	private void testTouchFindBeansWithRightsNoRange() {
		testTouchFindBeansWithRights(null, null);
	}
	
	@Test
	private void testTouchFindBeansWithRightsWithRange() {
		final Date now = new Date();
		final Date tomorrow = DateUtils.addDays(now, 1);
		testTouchFindBeansWithRights(now, tomorrow);
	}
	
	private void testTouchFindBeansWithRights(final Date startDate, final Date endDate) {
		final Tuple<Resource, Resource> t = addChildResource(startDate, endDate);
		try {
			
			/* check parents */
			final ResourceSearchBean sb = new ResourceSearchBean();
			sb.addParentId(t.getKey().getId());
			sb.setIncludeAccessRights(true);
			List<Resource> resources = find(sb, 0, Integer.MAX_VALUE);
			/* assert you get back any results */
			Assert.assertTrue(CollectionUtils.isNotEmpty(resources));
			
			/* assert you get back the correct results */
			Assert.assertTrue(resources.stream().map(e -> e.getId()).filter(e -> e.equals(t.getValue().getId())).findAny().isPresent());
			resources.forEach(e -> {
				Assert.assertTrue(CollectionUtils.isEmpty(e.getAccessRightIds()));
			});
			
			/* check children */
			sb.setParentIdSet(null);
			sb.addChildId(t.getValue().getId());
			resources = find(sb, 0, Integer.MAX_VALUE);
			
			/* assert you get back any results */
			Assert.assertTrue(CollectionUtils.isNotEmpty(resources));
			
			/* assert you get back the correct results */
			Assert.assertTrue(resources.stream().map(e -> e.getId()).filter(e -> e.equals(t.getKey().getId())).findAny().isPresent());
			resources.forEach(e -> {
				Assert.assertTrue(CollectionUtils.isEmpty(e.getAccessRightIds()));
			});
		} finally {
			if(t != null) {
				delete(t.getValue());
				delete(t.getKey());
			}
		}
	}
	
	private Tuple<Resource, Resource> addChildResource(final Date startDate, final Date endDate) {
		Resource resource1 = super.createBean();
		Response response = saveAndAssert(resource1);
		resource1 = get((String)response.getResponseValue());
		
		Resource resource2 = super.createBean();
		response = saveAndAssert(resource2);
		resource2 = get((String)response.getResponseValue());
		
		response = resourceDataService.addChildResource(resource1.getId(), resource2.getId(), null, startDate, endDate);
		Assert.assertTrue(String.format("Could not add child resource: %s", response), response.isSuccess());
		
		return new Tuple<Resource, Resource>(resource1, resource2);
	}
	
	private ResourceSearchBean getCacheableSearchBean(final Resource resource) {
		final ResourceSearchBean sb = new ResourceSearchBean();
		sb.setFindInCache(true);
		sb.setDeepCopy(true);
		sb.setNameToken(new SearchParam(resource.getName(), MatchType.EXACT));
		return sb;
	}
	
}
