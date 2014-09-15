package org.openiam.service.integration.entitlements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.org.service.OrganizationTypeDataService;
import org.openiam.service.integration.AbstractAttributeServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class OrganizationServiceTest extends AbstractAttributeServiceTest<Organization, OrganizationSearchBean, OrganizationAttribute> {
	
	@Autowired
	@Qualifier("organizationServiceClient")
	private OrganizationDataService organizationServiceClient;
	
	@Autowired
	@Qualifier("organizationTypeClient")
	private OrganizationTypeDataService organizationTypeClient;

	@Override
	protected OrganizationAttribute createAttribute(Organization t) {
		final OrganizationAttribute attribute = new OrganizationAttribute();
		attribute.setName(getRandomName());
		attribute.setValue(getRandomName());
		attribute.setOrganizationId(t.getId());
		return attribute;
	}

	@Override
	protected Set<OrganizationAttribute> createAttributeSet() {
		return new HashSet<>();
	}

	@Override
	protected void setAttributes(Organization t, Set<OrganizationAttribute> attributes) {
		t.setAttributes(attributes);
	}

	@Override
	protected Set<OrganizationAttribute> getAttributes(Organization t) {
		return t.getAttributes();
	}

	@Override
	protected Organization newInstance() {
		final Organization organization = new Organization();
		organization.setOrganizationTypeId(organizationTypeClient.findBeans(null, 0, 1, null).get(0).getId());
		return organization;
	}

	@Override
	protected OrganizationSearchBean newSearchBean() {
		return new OrganizationSearchBean();
	}

	@Override
	protected Response save(Organization t) {
		return organizationServiceClient.saveOrganization(t, null);
	}

	@Override
	protected Response delete(Organization t) {
		return organizationServiceClient.deleteOrganization(t.getId());
	}

	@Override
	protected Organization get(String key) {
		return organizationServiceClient.getOrganizationLocalized(key, null, null);
	}

	@Override
	public List<Organization> find(OrganizationSearchBean searchBean, int from,
			int size) {
		return organizationServiceClient.findBeansLocalized(searchBean, null, from, size, null);
	}

}
