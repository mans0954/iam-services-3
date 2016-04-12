package org.openiam.service.integration.entitlements;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.openiam.base.Tuple;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
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
		return resource;
	}

	@Override
	protected ResourceSearchBean newSearchBean() {
		return new ResourceSearchBean();
	}

	@Override
	protected Response save(Resource t) {
		return resourceDataService.saveResource(t, null);
	}

	@Override
	protected Response delete(Resource t) {
		return resourceDataService.deleteResource(t.getId(), null);
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
			Response response = resourceDataService.deleteChildResource(t.getKey().getId(), t.getValue().getId(), "3000");
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
		
		response = resourceDataService.addChildResource(resource1.getId(), resource2.getId(), "3000", null, startDate, endDate);
		Assert.assertTrue(String.format("Could not add child resource: %s", response), response.isSuccess());
		
		return new Tuple<Resource, Resource>(resource1, resource2);
	}
	
	private ResourceSearchBean getCacheableSearchBean(final Resource resource) {
		final ResourceSearchBean sb = new ResourceSearchBean();
		sb.setFindInCache(true);
		sb.setDeepCopy(true);
		sb.setName(resource.getName());
		return sb;
	}
	
	@Test
	public void testSearchBeanCache() throws Exception {
		for(int j = 0; j < 2; j++) {
			final Resource resource = super.createResource();
			final ResourceSearchBean sb = getCacheableSearchBean(resource);
			try {
				searchAndAssertCacheHit(sb, resource, "resourceEntities");
			} finally {
				deleteAndAssert(resource);
				sleep(1);
				Assert.assertTrue(CollectionUtils.isEmpty(find(sb, 0, Integer.MAX_VALUE)));
			}
		}
	}
	
	@Test
	public void testAddUserToResourceCache() {
		final User user = super.createUser();
		final Resource resource = super.createResource();
		try {
			final ResourceSearchBean sb = getCacheableSearchBean(resource);
			
			/* trigger and assert cache hit */
			searchAndAssertCacheHit(sb, resource, "resourceEntities");
			
			Date now = new Date();
			resourceDataService.addUserToResource(resource.getId(), user.getId(), getRequestorId(), null, null, null);
			assertCachePurge(now, "resourceEntities", 1);
			
			/* trigger and assert cache hit */
			now = new Date();
			searchAndAssertCacheHit(sb, resource, "resourceEntities");
			resourceDataService.removeUserFromResource(resource.getId(), user.getId(), getRequestorId());
			assertCachePurge(now, "resourceEntities", 1);
		} finally {
			deleteAndAssert(resource);
			assertSuccess(userServiceClient.removeUser(user.getId()));
		}
	}
	
	@Test
	public void testAddResourceToRoleCache() {
		final Role role = super.createRole();
		final Resource resource = super.createResource();
		try {
			final ResourceSearchBean sb = getCacheableSearchBean(resource);
			
			/* trigger and assert cache hit */
			searchAndAssertCacheHit(sb, resource, "resourceEntities");
			
			Date now = new Date();
			resourceDataService.addRoleToResource(resource.getId(), role.getId(), getRequestorId(), null, null, null);
			assertCachePurge(now, "resourceEntities", 1);
			
			/* trigger and assert cache hit */
			now = new Date();
			searchAndAssertCacheHit(sb, resource, "resourceEntities");
			resourceDataService.removeRoleToResource(resource.getId(), role.getId(), getRequestorId());
			assertCachePurge(now, "resourceEntities", 1);
		} finally {
			deleteAndAssert(resource);
			assertSuccess(roleServiceClient.removeRole(role.getId(), getRequestorId()));
		}
	}
	
	@Test
	public void testAddResourceToGroupCache() {
		final Group group = super.createGroup();
		final Resource resource = super.createResource();
		try {
			final ResourceSearchBean sb = getCacheableSearchBean(resource);
			
			/* trigger and assert cache hit */
			searchAndAssertCacheHit(sb, resource, "resourceEntities");
			
			Date now = new Date();
			resourceDataService.addGroupToResource(resource.getId(), group.getId(), getRequestorId(), null, null, null);
			assertCachePurge(now, "resourceEntities", 1);
			
			/* trigger and assert cache hit */
			now = new Date();
			searchAndAssertCacheHit(sb, resource, "resourceEntities");
			resourceDataService.removeGroupToResource(resource.getId(), group.getId(), getRequestorId());
			assertCachePurge(now, "resourceEntities", 1);
		} finally {
			deleteAndAssert(resource);
			assertSuccess(groupServiceClient.deleteGroup(group.getId(), getRequestorId()));
		}
	}
	
	@Test
	public void testAddAndRemoveChildCachePurge() {
		final Resource entity1 = createResource();
		final Resource entity2 = createResource();
		
		try {
			/* trigger and assert cache hit */
			for(final Resource resource : new Resource[] {entity1, entity2}) {
				final ResourceSearchBean sb = getCacheableSearchBean(resource);
				searchAndAssertCacheHit(sb, resource, "resourceEntities");
			}
			
			Date now = new Date();
			resourceDataService.addChildResource(entity1.getId(), entity2.getId(), getRequestorId(), null, null, null);
			assertCachePurge(now, "resourceEntities", 2);
			
			/* trigger and assert cache hit */
			now = new Date();
			/* trigger and assert cache hit */
			for(final Resource resource : new Resource[] {entity1, entity2}) {
				final ResourceSearchBean sb = getCacheableSearchBean(resource);
				searchAndAssertCacheHit(sb, resource, "resourceEntities");
			}
			
			resourceDataService.deleteChildResource(entity1.getId(), entity2.getId(), getRequestorId());
			assertCachePurge(now, "resourceEntities", 2);
			
		} finally {
			for(final Resource entity : new Resource[] {entity1, entity2}) {
				deleteAndAssert(entity);
			}
		}
	}
}
