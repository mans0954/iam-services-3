package org.openiam.service.integration.entitlements;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

public class Resource2ResourceEntitlementsTest extends AbstractCircularEntitlementTest<Resource> {
	
	@Override
	protected Resource createParent() {
		return super.createResource();
	}

	@Override
	protected Resource createChild() {
		return super.createResource();
	}

	@Override
	protected Response addChildToParent(final Resource parent, final Resource child, final String requestorId, final Set<String> rights, final Date startDate, final Date endDate) {
		return resourceDataService.addChildResource(parent.getId(), child.getId(), rights, startDate, endDate);
	}

	@Override
	protected Response removeChildFromParent(Resource parent, Resource child, final String requestorId) {
		return resourceDataService.deleteChildResource(parent.getId(), child.getId());
	}

	@Override
	protected Response deleteParent(Resource parent, final String requestorId) {
		return resourceDataService.deleteResource(parent.getId());
	}

	@Override
	protected Response deleteChild(Resource child, final String requestorId) {
		return resourceDataService.deleteResource(child.getId());
	}

	@Override
	protected boolean isChildInParent(Resource parent, Resource child, final Set<String> rights) {
		ResourceSearchBean searchBean = new ResourceSearchBean();
		searchBean.addChildId(child.getId());
		searchBean.setIncludeAccessRights(true);
		final List<Resource> resources = resourceDataService.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(resources)) {
			final Optional<Resource> optional = resources.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find child resource"), optional.isPresent());
			final Resource res = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(res.getAccessRightIds()));
			} else {
				Assert.assertEquals(res.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean parentHasChild(Resource parent, Resource child, final Set<String> rights) {
		ResourceSearchBean searchBean = new ResourceSearchBean();
		searchBean.addParentId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		final List<Resource> resources = resourceDataService.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(resources)) {
			final Optional<Resource> optional = resources.stream().filter(e -> e.getId().equals(child.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent resource"), optional.isPresent());
			final Resource res = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(res.getAccessRightIds()));
			} else {
				Assert.assertEquals(res.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}

	@Test
	public void foo(){}

	@Override
	protected Resource getParentById(Resource parent) {
		return resourceDataService.getResource(parent.getId());
	}

	@Override
	protected Resource getChildById(Resource child) {
		return resourceDataService.getResource(child.getId());
	}
}
