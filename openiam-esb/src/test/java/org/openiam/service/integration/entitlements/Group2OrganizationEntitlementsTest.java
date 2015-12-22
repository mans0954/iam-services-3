package org.openiam.service.integration.entitlements;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

public class Group2OrganizationEntitlementsTest extends AbstractEntitlementsTest<Organization, Group> {

	@Override
	protected Organization createParent() {
		return super.createOrganization();
	}

	@Override
	protected Group createChild() {
		return super.createGroup();
	}

	@Override
	protected Response addChildToParent(final Organization parent, final Group child, final Set<String> rights, final Date startDate, final Date endDate) {
		return organizationServiceClient.addGroupToOrganization(parent.getId(), child.getId(), rights, startDate, endDate);
	}

	@Override
	protected Response removeChildFromParent(Organization parent, Group child) {
		return organizationServiceClient.removeGroupFromOrganization(parent.getId(), child.getId());
	}

	@Override
	protected Response deleteParent(Organization parent) {
		return organizationServiceClient.deleteOrganization(parent.getId());
	}

	@Override
	protected Response deleteChild(Group child) {
		return groupServiceClient.deleteGroup(child.getId(), "3000");
	}

	@Override
	protected boolean isChildInParent(Organization parent, Group child,
			Set<String> rights) {
		final OrganizationSearchBean searchBean = new OrganizationSearchBean();
		searchBean.addGroupId(child.getId());
		searchBean.setIncludeAccessRights(true);
		final List<Organization> dtos = organizationServiceClient.findBeansLocalized(searchBean, null, 0, 100, getDefaultLanguage());
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Organization> optional = dtos.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find child"), optional.isPresent());
			final Organization dto = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(dto.getAccessRightIds()));
			} else {
				Assert.assertEquals(dto.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean parentHasChild(Organization parent, Group child,
			Set<String> rights) {
		final GroupSearchBean searchBean = new GroupSearchBean();
		searchBean.addOrganizationId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		final List<Group> dtos = groupServiceClient.findBeansLocalize(searchBean, "3000", 0, 100, getDefaultLanguage());
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Group> optional = dtos.stream().filter(e -> e.getId().equals(child.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent"), optional.isPresent());
			final Group group = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(group.getAccessRightIds()));
			} else {
				Assert.assertEquals(group.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}

	@Test
	public void foo(){}

	@Override
	protected Organization getParentById(Organization parent) {
		return organizationServiceClient.getOrganizationLocalized(parent.getId(), "3000", getDefaultLanguage());
	}

	@Override
	protected Group getChildById(Group child) {
		return groupServiceClient.getGroupLocalize(child.getId(), "3000", getDefaultLanguage());
	}
}
