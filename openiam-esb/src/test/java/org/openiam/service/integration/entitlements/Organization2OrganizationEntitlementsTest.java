package org.openiam.service.integration.entitlements;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.annotations.Test;

public class Organization2OrganizationEntitlementsTest extends AbstractCircularEntitlementTest<Organization> {

	@Override
	protected Organization createParent() {
		return super.createOrganization();
	}

	@Override
	protected Organization createChild() {
		return super.createOrganization();
	}

	@Override
	protected Response addChildToParent(final Organization parent, final Organization child, final String requestorId, final Set<String> rights, final Date startDate, final Date endDate) {
		return organizationServiceClient.addChildOrganization(parent.getId(), child.getId(), requestorId, rights, startDate, endDate);
	}

	@Override
	protected Response removeChildFromParent(Organization parent, Organization child, final String requestorId) {
		return organizationServiceClient.removeChildOrganization(parent.getId(), child.getId(), requestorId);
	}

	@Override
	protected Response deleteParent(Organization parent, final String requestorId) {
		return organizationServiceClient.deleteOrganization(parent.getId(), requestorId);
	}

	@Override
	protected Response deleteChild(Organization child, final String requestorId) {
		return organizationServiceClient.deleteOrganization(child.getId(), requestorId);
	}

	@Override
	protected boolean isChildInParent(Organization parent, Organization child, final Set<String> rights) {
		final OrganizationSearchBean searchBean = new OrganizationSearchBean();
		searchBean.addChildId(child.getId());
		searchBean.setIncludeAccessRights(true);
		final List<Organization> dtos = organizationServiceClient.findBeansLocalized(searchBean, null, 0, 100, getDefaultLanguage());
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Organization> optional = dtos.stream().filter(e -> e.getId().equals(parent.getId())).findAny();
			Assert.assertTrue(String.format("Can't find child org"), optional.isPresent());
			final Organization organization = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(organization.getAccessRightIds()));
			} else {
				Assert.assertEquals(organization.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean parentHasChild(Organization parent, Organization child, final Set<String> rights) {
		final OrganizationSearchBean searchBean = new OrganizationSearchBean();
		searchBean.addParentId(parent.getId());
		searchBean.setIncludeAccessRights(true);
		final List<Organization> dtos = organizationServiceClient.findBeansLocalized(searchBean, null, 0, 100, getDefaultLanguage());
		if(CollectionUtils.isNotEmpty(dtos)) {
			final Optional<Organization> optional = dtos.stream().filter(e -> e.getId().equals(child.getId())).findAny();
			Assert.assertTrue(String.format("Can't find parent organization"), optional.isPresent());
			final Organization organization = optional.get();
			if(CollectionUtils.isEmpty(rights)) {
				Assert.assertTrue(CollectionUtils.isEmpty(organization.getAccessRightIds()));
			} else {
				Assert.assertEquals(organization.getAccessRightIds(), rights);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected Organization getParentById(Organization parent) {
		return organizationServiceClient.getOrganizationLocalized(parent.getId(), "3000", getDefaultLanguage());
	}

	@Override
	protected Organization getChildById(Organization child) {
		return organizationServiceClient.getOrganizationLocalized(child.getId(), "3000", getDefaultLanguage());
	}
}
