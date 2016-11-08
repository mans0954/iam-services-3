package org.openiam.service.integration.entitlements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.service.integration.AbstractAttributeServiceTest;
import org.openiam.srvc.am.OrganizationDataService;
import org.openiam.srvc.am.OrganizationTypeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class OrganizationServiceTest extends AbstractAttributeServiceTest<Organization, OrganizationSearchBean, OrganizationAttribute> {
	
	private static final int CACHE_IMPROVEMENT_FACTOR = 2;
	
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
		organization.setOrganizationTypeId(organizationTypeClient.findBeans(new OrganizationTypeSearchBean(), 0, 1, null).get(0).getId());
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
		return organizationServiceClient.deleteOrganization(t.getId(), null);
	}

	@Override
	protected Organization get(String key) {
		return organizationServiceClient.getOrganizationLocalized(key, null, null);
	}

	@Override
	public List<Organization> find(OrganizationSearchBean searchBean, int from,
			int size) {
		searchBean.setLanguage(getDefaultLanguage());
		return organizationServiceClient.findBeans(searchBean, null, from, size);
	}

	@Test
	public void testContraintViolationWithNoManagedSystem() {
		final String name = getRandomName();
		Organization r1 = newInstance();
		Organization r2 = newInstance();
		
		r1.setName(name);
		r2.setName(name);
		Response response = organizationServiceClient.saveOrganization(r1, getRequestorId());
		assertSuccess(response);
		response = organizationServiceClient.saveOrganization(r2, getRequestorId());
		assertResponseCode(response, ResponseCode.CONSTRAINT_VIOLATION);
	}
	
	@Test
	public void testContraintViolationWithManagedSystem() {
		final String name = getRandomName();
		final String typeId = organizationTypeClient.findBeans(null, 0, 10, null).get(0).getId();
		Organization r1 = newInstance();
		Organization r2 = newInstance();
		
		r1.setName(name);
		r1.setOrganizationTypeId(typeId);
		r2.setName(name);
		r2.setOrganizationTypeId(typeId);
		Response response = organizationServiceClient.saveOrganization(r1, getRequestorId());
		assertSuccess(response);
		response = organizationServiceClient.saveOrganization(r2, getRequestorId());
		assertResponseCode(response, ResponseCode.CONSTRAINT_VIOLATION);
	}
	
	@Test
	public void testContraintViolationDifferentManagedSystem() {
		final String name = getRandomName();
		Organization r1 = newInstance();
		Organization r2 = newInstance();
		
		r1.setName(name);
		r1.setOrganizationTypeId(organizationTypeClient.findBeans(null, 0, 10, null).get(0).getId());
		r2.setName(name);
		r2.setOrganizationTypeId(organizationTypeClient.findBeans(null, 0, 10, null).get(1).getId());
		Response response = organizationServiceClient.saveOrganization(r1, getRequestorId());
		assertSuccess(response);
		response = organizationServiceClient.saveOrganization(r2, getRequestorId());
		assertSuccess(response);
	}

}

