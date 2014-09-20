package org.openiam.service.integration.entitlements;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.service.integration.AbstractEntitlementsTest;

public class Organization2OrganizationEntitlementsTest extends AbstractEntitlementsTest<Organization, Organization> {

	@Override
	protected Organization createParent() {
		return super.createOrganization();
	}

	@Override
	protected Organization createChild() {
		return super.createOrganization();
	}

	@Override
	protected Response addChildToParent(Organization parent, Organization child) {
		return organizationServiceClient.addChildOrganization(parent.getId(), child.getId());
	}

	@Override
	protected Response removeChildFromParent(Organization parent,
			Organization child) {
		return organizationServiceClient.removeChildOrganization(parent.getId(), child.getId());
	}

	@Override
	protected Response deleteParent(Organization parent) {
		return organizationServiceClient.deleteOrganization(parent.getId());
	}

	@Override
	protected Response deleteChild(Organization child) {
		return organizationServiceClient.deleteOrganization(child.getId());
	}

	@Override
	protected boolean isChildInParent(Organization parent, Organization child) {
		final OrganizationSearchBean searchBean = new OrganizationSearchBean();
		searchBean.addChildId(child.getId());
		final List<Organization> parents = organizationServiceClient.findBeansLocalized(searchBean, null, 0, 100, getDefaultLanguage());
		return (CollectionUtils.isNotEmpty(parents)) ? parents.contains(parent) : false;
	}

	@Override
	protected boolean parentHasChild(Organization parent, Organization child) {
		final OrganizationSearchBean searchBean = new OrganizationSearchBean();
		searchBean.addParentId(parent.getId());
		final List<Organization> children = organizationServiceClient.findBeansLocalized(searchBean, null, 0, 100, getDefaultLanguage());
		return (CollectionUtils.isNotEmpty(children)) ? children.contains(child) : false;
	}

}
