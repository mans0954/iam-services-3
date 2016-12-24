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
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

public class Group2GroupEntitlementsTest extends AbstractCircularEntitlementTest<Group> {
	
	@Override
	protected Group createParent() {
		return super.createGroup();
	}

	@Override
	protected Group createChild() {
		return super.createGroup();
	}

	@Override
	protected Response addChildToParent(final Group parent, 
										final Group child, 
										final String requestorId,
										final Set<String> rights,
										final Date startDate, 
										final Date endDate) {
		return groupServiceClient.addChildGroup(parent.getId(), child.getId(), rights, startDate, endDate);
	}

	@Override
	protected Response removeChildFromParent(Group parent, Group child, final String requestorId) {
		return groupServiceClient.removeChildGroup(parent.getId(), child.getId());
	}

	@Override
	protected Response deleteParent(Group parent, final String requestorId) {
		return groupServiceClient.deleteGroup(parent.getId());
	}

	@Override
	protected Response deleteChild(Group child, final String requestorId) {
		return groupServiceClient.deleteGroup(child.getId());
	}

	@Override
	protected boolean isChildInParent(Group parent, Group child, final Set<String> rights) {
		GroupSearchBean searchBean = new GroupSearchBean();
		searchBean.addChildId(child.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setLanguage(getDefaultLanguage());
		final List<Group> groups = groupServiceClient.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(groups)) {
			final Optional<Group> optional = groups.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find child resource"), optional.isPresent());
			final Group grp = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(grp.getAccessRightIds()));
			} else {
				Assert.assertEquals(grp.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean parentHasChild(Group parent, Group child, final Set<String> rights) {
		GroupSearchBean searchBean = new GroupSearchBean();
		searchBean.addParentId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setLanguage(getDefaultLanguage());
		final List<Group> groups = groupServiceClient.findBeans(searchBean, 0, 100);
		if(CollectionUtils.isNotEmpty(groups)) {
			final Optional<Group> optional = groups.stream().filter(e -> e.getId().equals(child.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent resource"), optional.isPresent());
			final Group grp = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(grp.getAccessRightIds()));
			} else {
				Assert.assertEquals(grp.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Test
	public void foo() {}

	@Override
	protected Group getParentById(Group parent) {
		return groupServiceClient.getGroup(parent.getId());
	}

	@Override
	protected Group getChildById(Group child) {
		return groupServiceClient.getGroup(child.getId());
	}
}
