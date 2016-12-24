package org.openiam.service.integration.entitlements;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

public class Group2ResourceEntitlementsTest extends AbstractEntitlementsTest<Group, Resource> {

	@Override
	protected Group createParent() {
		return super.createGroup();
	}

	@Override
	protected Resource createChild() {
		return super.createResource();
	}

	@Override
	protected Response addChildToParent(final Group parent, final Resource child, final String requestorId, final Set<String> rights, final Date startDate, final Date endDate) {
		return resourceDataService.addGroupToResource(child.getId(), parent.getId(), rights, startDate, endDate);
	}

	@Override
	protected Response removeChildFromParent(Group parent, Resource child, final String requestorId) {
		return resourceDataService.removeGroupToResource(child.getId(), parent.getId());
	}

	@Override
	protected Response deleteParent(Group parent, final String requestorId) {
		return groupServiceClient.deleteGroup(parent.getId());
	}

	@Override
	protected Response deleteChild(Resource child, final String requestorId) {
		return resourceDataService.deleteResource(child.getId());
	}

	@Override
	protected boolean isChildInParent(Group parent, Resource child, final Set<String> rights) {
		final GroupSearchBean searchBean = new GroupSearchBean();
		searchBean.addResourceId(child.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		searchBean.setLanguage(getDefaultLanguage());
		final List<Group> dtos = groupServiceClient.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Group> optional = dtos.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find child"), optional.isPresent());
			final Group e = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(e.getAccessRightIds()));
			} else {
				Assert.assertEquals(e.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean parentHasChild(Group parent, Resource child, final Set<String> rights) {
		final ResourceSearchBean searchBean = new ResourceSearchBean();
		searchBean.addGroupId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<Resource> dtos = resourceDataService.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Resource> optional = dtos.stream().filter(e -> e.getId().equals(child.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent"), optional.isPresent());
			final Resource e = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(e.getAccessRightIds()));
			} else {
				Assert.assertEquals(rights, e.getAccessRightIds());
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected Group getParentById(Group parent) {
		return groupServiceClient.getGroupLocalize(parent.getId(), getDefaultLanguage());
	}

	@Override
	protected Resource getChildById(Resource child) {
		return resourceDataService.getResource(child.getId());
	}

	@Test
	public void foo() {}
}
