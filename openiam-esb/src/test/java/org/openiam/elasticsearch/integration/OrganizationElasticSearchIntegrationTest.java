package org.openiam.elasticsearch.integration;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.model.OrganizationDoc;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.dto.Organization;
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
		organization.setOrganizationTypeId(organizationTypeClient.findBeans(null, 0, 1).get(0).getId());
		final Response response = organizationServiceClient.saveOrganization(organization);
		assertSuccess(response);
		organization = organizationServiceClient.getOrganization((String)response.getResponseValue());
		Assert.assertNotNull(organization);
		
		final String organizationId = organization.getId();
		
		parentOrganizationId = organizationServiceClient.findBeans(null, 0, 10)
				   .stream()
				   .filter(e -> !e.getId().equals(organizationId))
				   .findAny()
				   .get()
				   .getId();
		assertSuccess(organizationServiceClient.addChildOrganization(parentOrganizationId, organization.getId(), null, null, null));
		sleep(5);
		return organization;
	}



	private OrganizationSearchBean newSearchBean() {
		final OrganizationSearchBean sb = new OrganizationSearchBean();
		sb.setUseElasticSearch(true);
		return sb;
	}
	
	/* there is really no good way to test this, as timing will be differnet (scheduled RabbitMQ messages) per ENV */
	@Test(enabled=false)
	public void testWithDelayedStartAndEndDate() {
		Organization child = null;
		Organization parent = null;
		try {
			final Date now = new Date();
			child = super.createOrganization();
			parent = super.createOrganization();
			
			final OrganizationSearchBean sb = newSearchBean();
			sb.addParentId(parent.getId());
			
			/* start in 15 s, end in 30 s */
			assertSuccess(organizationServiceClient.addChildOrganization(parent.getId(), child.getId(), null, DateUtils.addSeconds(now, 15), DateUtils.addSeconds(now, 30)));
			sleep(5000);
			assertNotFound(sb);
			
			/* start date */
			sleep(10000);
			assertFindBeans(sb);
			
			/* end date */
			sleep(20000);
			assertNotFound(sb);
		} finally {
			if(child != null) {
				organizationServiceClient.deleteOrganization(child.getId());
			}
			if(parent != null) {
				organizationServiceClient.deleteOrganization(parent.getId());
			}
		}
	}
	
	@Test
	public void testInternationalizationOfOrganizationType() {
		final OrganizationSearchBean sb = newSearchBean();
		List<Organization> retval = organizationServiceClient.findBeans(sb, 0, 3);
		assertOrganizationTypePresent(retval);
		
		sb.setNameToken(new SearchParam("a", MatchType.CONTAINS));
		retval = organizationServiceClient.findBeans(sb, 0, 3);
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
		sb.setNameToken(new SearchParam(getDTO().getName(), MatchType.EXACT)); /* further qualifier */
		sb.setOrganizationTypeId(getDTO().getOrganizationTypeId());
		assertFindBeans(sb);
	}
	
	@Test
	public void testSearchWithParentOrganizationId() {
		final OrganizationSearchBean sb = newSearchBean();
		sb.addParentId(parentOrganizationId);
		sb.setNameToken(new SearchParam(getDTO().getName(), MatchType.EXACT)); /* further qualifier */
		assertFindBeans(sb);
	}
	
	/*
	@Test
	public void testSearchWithParentOrganizationTypeId() {
		final OrganizationSearchBean sb = newSearchBean();
		sb.setValidParentTypeId(organizationServiceClient.getOrganizationLocalized(parentOrganizationId, getRequestorId(), getDefaultLanguage()).getOrganizationTypeId());
		sb.setNameToken(new SearchParam(getDTO().getName(), MatchType.EXACT));
		assertFindBeans(sb);
	}
	*/

	@Override
	protected void delete(Organization dto) {
		assertSuccess(organizationServiceClient.deleteOrganization(dto.getId()));
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
		return organizationServiceClient.findBeans(searchBean, 0, 10);
	}



	@Override
	protected boolean isIndexed(String id) {
		return organizationServiceClient.isIndexed(id);
	}
}
