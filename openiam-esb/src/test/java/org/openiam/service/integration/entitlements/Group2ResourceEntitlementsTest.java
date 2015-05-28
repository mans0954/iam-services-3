package org.openiam.service.integration.entitlements;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.service.integration.AbstractEntitlementsTest;

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
	protected Response addChildToParent(Group parent, Resource child, final Set<String> rights) {
		return resourceDataService.addGroupToResource(child.getId(), parent.getId(), null);
	}

	@Override
	protected Response removeChildFromParent(Group parent, Resource child) {
		return resourceDataService.removeGroupToResource(child.getId(), parent.getId(), null);
	}

	@Override
	protected Response deleteParent(Group parent) {
		return groupServiceClient.deleteGroup(parent.getId(), null);
	}

	@Override
	protected Response deleteChild(Resource child) {
		return resourceDataService.deleteResource(child.getId(), null);
	}

	@Override
	protected boolean isChildInParent(Group parent, Resource child, final Set<String> rights) {
		final ResourceSearchBean searchBean = new ResourceSearchBean();
        searchBean.addGroupId(parent.getId());
        final List<Resource> resources = resourceDataService.findBeans(searchBean, 0, 100, getDefaultLanguage());
        return (CollectionUtils.isNotEmpty(resources)) ? resources.contains(child) : false;
	}

	@Override
	protected boolean parentHasChild(Group parent, Resource child, final Set<String> rights) {
		final GroupSearchBean searchBean = new GroupSearchBean();
        searchBean.addResourceId(child.getId());
        final List<Group> groups = groupServiceClient.findBeansLocalize(searchBean, null, 0, 100, getDefaultLanguage());
        return (CollectionUtils.isNotEmpty(groups)) ? groups.contains(parent) : false;
	}

	@Override
	protected Group getParentById(Group parent) {
		return groupServiceClient.getGroupLocalize(parent.getId(), "3000", getDefaultLanguage());
	}

	@Override
	protected Resource getChildById(Resource child) {
		return resourceDataService.getResource(child.getId(), getDefaultLanguage());
	}

}
