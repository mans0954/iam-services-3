package org.openiam.service.integration.entitlements;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

public class Resource2OrganizationEntitlementsTest extends AbstractEntitlementsTest<Organization, Resource> {

	@Override
	protected Organization getParentById(Organization parent) {
		return organizationServiceClient.getOrganization(parent.getId());
	}

	@Override
	protected Resource getChildById(Resource child) {
		return resourceDataService.getResource(child.getId());
	}

	@Override
	protected Organization createParent() {
		return super.createOrganization();
	}

	@Override
	protected Resource createChild() {
		return super.createResource();
	}

	@Override
	protected Response addChildToParent(final Organization parent, final Resource child, final String requestorId, final Set<String> rights, final Date startDate, final Date endDate) {
		return organizationServiceClient.addResourceToOrganization(parent.getId(), child.getId(), rights, startDate, endDate);
	}

	@Override
	protected Response removeChildFromParent(Organization parent, Resource child, final String requestorId) {
		return organizationServiceClient.removeResourceFromOrganization(parent.getId(), child.getId());
	}

	@Override
	protected Response deleteParent(Organization parent, final String requestorId) {
		return organizationServiceClient.deleteOrganization(parent.getId());
	}

	@Override
	protected Response deleteChild(Resource child, final String requestorId) {
		return resourceDataService.deleteResource(child.getId());
	}

	@Override
	protected boolean isChildInParent(Organization parent, Resource child,
			Set<String> rights) {
		final OrganizationSearchBean searchBean = new OrganizationSearchBean();
		searchBean.addResourceId(child.getId());
		searchBean.setIncludeAccessRights(true);
		searchBean.setDeepCopy(false);
		final List<Organization> dtos = organizationServiceClient.findBeans(searchBean, 0, 100);
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
	protected boolean parentHasChild(Organization parent, Resource child,
			Set<String> rights) {
		final ResourceSearchBean searchBean = new ResourceSearchBean();
		searchBean.addOrganizationId(parent.getId());
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
				Assert.assertEquals(e.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}

	@Test
	public void foo(){}
}
