package org.openiam.service.integration.entitlements;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.service.integration.AbstractEntitlementsTest;

public class Group2GroupEntitlementsTest extends AbstractEntitlementsTest<Group, Group> {

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
		return groupServiceClient.addChildGroup(parent.getId(), child.getId(), null);
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
		final GroupSearchBean searchBean = new GroupSearchBean();
		searchBean.addParentId(parent.getId());
		final List<Group> children = groupServiceClient.findBeansLocalize(searchBean, null, 0, 100, getDefaultLanguage());
		return (CollectionUtils.isNotEmpty(children)) ? children.contains(child) : false;
	}

	@Override
	protected boolean parentHasChild(Group parent, Group child, final Set<String> rights) {
		final GroupSearchBean searchBean = new GroupSearchBean();
		searchBean.addChildId(child.getId());
		final List<Group> parents = groupServiceClient.findBeansLocalize(searchBean, null, 0, 100, getDefaultLanguage());
		return (CollectionUtils.isNotEmpty(parents)) ? parents.contains(parent) : false;
	}

}
