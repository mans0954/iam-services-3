package org.openiam.service.integration.entitlements;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.service.integration.AbstractServiceTest;
import org.openiam.srvc.am.OrganizationDataService;
import org.openiam.srvc.am.OrganizationTypeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OrganizationElasticSearchTest extends AbstractServiceTest {
	

	@Autowired
	@Qualifier("organizationServiceClient")
	private OrganizationDataService organizationServiceClient;
	
	@Autowired
	@Qualifier("organizationTypeClient")
	private OrganizationTypeDataService organizationTypeClient;

	
	private Organization organization = null;
	
	@BeforeClass
	protected void _setUp() throws Exception {
		organization = super.createOrganization();
		sleep(3);
	}
	
	@AfterClass
	public void _tearDown()  throws Exception {
		if(organization != null) {
			organizationServiceClient.deleteOrganization(organization.getId(), getRequestorId());
		}
	}
	
	private OrganizationSearchBean newSearchBean() {
		final OrganizationSearchBean sb = new OrganizationSearchBean();
		sb.setLanguage(getDefaultLanguage());
		sb.setUseElasticSearch(true);
		return sb;
	}
	
	@Test
	public void testInternationalizationOfOrganizationType() {
		final OrganizationSearchBean sb = newSearchBean();
		List<Organization> retval = organizationServiceClient.findBeans(sb, getRequestorId(), 0, 3);
		assertOrganizationTypePresent(retval);
		
		sb.setNameToken(new SearchParam("a", MatchType.CONTAINS));
		retval = organizationServiceClient.findBeans(sb, getRequestorId(), 0, 3);
		assertOrganizationTypePresent(retval);
	}
	
	private void assertOrganizationTypePresent(final List<Organization> retval) {
		Assert.assertTrue(CollectionUtils.isNotEmpty(retval));
		retval.forEach(e -> {
			Assert.assertTrue(StringUtils.isNotBlank(e.getOrganizationTypeName()));
		});
	}


	@Test
	public void testOrganizationSearchWithMetadataType() {
		final OrganizationSearchBean sb = newSearchBean();
		sb.setMetadataType(organization.getMdTypeId());
		final List<Organization> found = organizationServiceClient.findBeans(sb, getRequestorId(), 0, 100);
		Assert.assertTrue(CollectionUtils.isNotEmpty(found));
		Assert.assertTrue(found.stream().filter(e -> e.getId().equals(organization.getId())).count() > 0);
	}
	
	@Test
	public void testOrganizationSearchWithOrganizationType() {
		final OrganizationSearchBean sb = newSearchBean();
		sb.setOrganizationTypeId(organization.getOrganizationTypeId());
		final List<Organization> found = organizationServiceClient.findBeans(sb, getRequestorId(), 0, 100);
		Assert.assertTrue(CollectionUtils.isNotEmpty(found));
		Assert.assertTrue(found.stream().filter(e -> e.getId().equals(organization.getId())).count() > 0);
	}
}
