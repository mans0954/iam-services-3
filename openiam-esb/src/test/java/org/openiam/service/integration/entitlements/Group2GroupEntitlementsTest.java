package org.openiam.service.integration.entitlements;

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
	protected Response addChildToParent(Group parent, Group child, final Set<String> rights) {
		return groupServiceClient.addChildGroup(parent.getId(), child.getId(), null, rights);
	}

	@Override
	protected Response removeChildFromParent(Group parent, Group child) {
		return groupServiceClient.removeChildGroup(parent.getId(), child.getId(), null);
	}

	@Override
	protected Response deleteParent(Group parent) {
		return groupServiceClient.deleteGroup(parent.getId(), null);
	}

	@Override
	protected Response deleteChild(Group child) {
		return groupServiceClient.deleteGroup(child.getId(), null);
	}

	@Override
	protected boolean isChildInParent(Group parent, Group child, final Set<String> rights) {
		GroupSearchBean searchBean = new GroupSearchBean();
		searchBean.addChildId(child.getId());
		//searchBean.setIncludeAccessRights(true);
		final List<Group> groups = groupServiceClient.findBeansLocalize(searchBean, "3000", 0, 100, getDefaultLanguage());
		if(CollectionUtils.isNotEmpty(groups)) {
			final Optional<Group> optional = groups.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find child resource"), optional.isPresent());
			//final Group grp = optional.get();
			//if(CollectionUtils.isEmpty(rights)) {
			//	Assert.assertTrue(CollectionUtils.isEmpty(grp.getAccessRightIds()));
			//} else {
			//	Assert.assertEquals(grp.getAccessRightIds(), rights);
			//}
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
		final List<Group> groups = groupServiceClient.findBeansLocalize(searchBean, "3000", 0, 100, getDefaultLanguage());
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
}
