package org.openiam.elasticsearch.integration;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.model.OrganizationDoc;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.service.integration.AbstractServiceTest;
import org.openiam.srvc.am.OrganizationDataService;
import org.openiam.srvc.am.OrganizationTypeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OrganizationElasticSearchIntegrationTest extends AbstractMetdataTypeElasticSearchIntegrationTest<OrganizationDoc, OrganizationSearchBean, Organization> {


	private String parentOrganizationId;

	@Override
	protected Organization createDTO() {
		// TODO Auto-generated method stub
		Organization organization = super.createDTO();
		organization.setAbbreviation(getRandomName());
		organization.setAlias(getRandomName());
		organization.setClassification(getRandomName());
		organization.setDescription(getRandomName());
		organization.setDomainName(getRandomName());
		organization.setLdapStr(getRandomName());
		organization.setOrganizationTypeId(organizationTypeClient.findBeans(null, 0, 1, getDefaultLanguage()).get(0).getId());
		final Response response = organizationServiceClient.saveOrganization(organization, getRequestorId());
		assertSuccess(response);
		organization = organizationServiceClient.getOrganizationLocalized((String)response.getResponseValue(), getRequestorId(), getDefaultLanguage());
		Assert.assertNotNull(organization);
		
		final String organizationId = organization.getId();
		
		parentOrganizationId = organizationServiceClient.findBeans(null, getRequestorId(), 0, 10)
				   .stream()
				   .filter(e -> !e.getId().equals(organizationId))
				   .findAny()
				   .get()
				   .getId();
		assertSuccess(organizationServiceClient.addChildOrganization(parentOrganizationId, organization.getId(), getRequestorId(), null, null, null));
		sleep(5);
		return organization;
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
	public void testOrganizationSearchWithOrganizationType() {
		final OrganizationSearchBean sb = newSearchBean();
		sb.setOrganizationTypeId(getDTO().getOrganizationTypeId());
		assertFindBeans(sb);
	}
	
	@Test
	public void testSearchWithParentOrganizationId() {
		final OrganizationSearchBean sb = newSearchBean();
		sb.addParentId(parentOrganizationId);
		assertFindBeans(sb);
	}
	
	@Test
	public void testSearchWithParentOrganizationTypeId() {
		final OrganizationSearchBean sb = newSearchBean();
		sb.setValidParentTypeId(organizationServiceClient.getOrganizationLocalized(parentOrganizationId, getRequestorId(), getDefaultLanguage()).getOrganizationTypeId());
		assertFindBeans(sb);
	}

	@Override
	protected void delete(Organization dto) {
		assertSuccess(organizationServiceClient.deleteOrganization(dto.getId(), getRequestorId()));
	}

	@Override
	protected Class<OrganizationSearchBean> getSearchBeanClass() {
		return OrganizationSearchBean.class;
	}

	@Override
	protected Class<OrganizationDoc> getDocumentClass() {
		return OrganizationDoc.class;
	}

	@Override
	protected Class<Organization> getDTOClass() {
		return Organization.class;
	}

	@Override
	protected List<Organization> findBeans(OrganizationSearchBean searchBean) {
		return organizationServiceClient.findBeans(searchBean, getRequestorId(), 0, 10);
	}
}
