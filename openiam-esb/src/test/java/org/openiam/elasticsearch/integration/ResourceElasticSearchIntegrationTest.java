package org.openiam.elasticsearch.integration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.SearchParam;
import org.openiam.elasticsearch.model.ResourceDoc;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.dto.ResourceRisk;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ResourceElasticSearchIntegrationTest extends AbstractMetdataTypeElasticSearchIntegrationTest<ResourceDoc, ResourceSearchBean, Resource> {

	@Test
	public void testFindByResoruceType() {
		final ResourceSearchBean sb = super.createSearchBean();
		sb.setResourceTypeId(getDTO().getResourceType().getId());
		/* additional qualifier - result list can be too big to have req'd item */
		sb.setNameToken(new SearchParam(getDTO().getName(), MatchType.EXACT));
		assertFindBeans(sb);
	}
	
	@Test
	public void testFindByRisk() {
		final ResourceSearchBean sb = super.createSearchBean();
		sb.setRisk(getDTO().getRisk());
		/* additional qualifier - result list can be too big to have req'd item */
		sb.setNameToken(new SearchParam(getDTO().getName(), MatchType.EXACT));
		assertFindBeans(sb);
	}
	
	/*
	@Test
	public void testFindByAttribute() {
		final ResourceProp prop = getDTO().getResourceProps().iterator().next();
		final ResourceSearchBean sb = super.createSearchBean();
		sb.addAttribute(prop.getName(), prop.getValue());;
		assertFindBeans(sb);
	}
	*/
	
	@Test
	public void testExcludeResourceType() {
		final Set<String> excludeIds = resourceDataService.findResourceTypes(null, 0, Integer.MAX_VALUE, getDefaultLanguage())
						   .stream()
						   .filter(e -> !e.getId().equals(getDTO().getResourceType().getId()))
						   .map(e -> e.getId())
						   .collect(Collectors.toSet());
		
		final ResourceSearchBean sb = super.createSearchBean();
		sb.setExcludeResourceTypes(excludeIds);
		/* additional qualifier - result list can be too big to have req'd item */
		sb.setNameToken(new SearchParam(getDTO().getName(), MatchType.EXACT));
		assertFindBeans(sb);
	}

	@Override
	protected Class<ResourceSearchBean> getSearchBeanClass() {
		return ResourceSearchBean.class;
	}

	@Override
	protected Class<ResourceDoc> getDocumentClass() {
		return ResourceDoc.class;
	}

	@Override
	protected Class<Resource> getDTOClass() {
		return Resource.class;
	}

	@Override
	protected List<Resource> findBeans(ResourceSearchBean searchBean) {
		return resourceDataService.findBeans(searchBean, 0, 10, getDefaultLanguage());
	}

	@Override
	protected Resource createDTO() {
		final Resource resource = super.createDTO();
		resource.setDescription(getRandomName());
		resource.setDisplayOrder(1);
		resource.setRisk(ResourceRisk.HIGH);
		resource.setResourceType(resourceDataService.findResourceTypes(null, 0, 1, null).get(0));
		//resource.addResourceProp(generateResourceProp());
		final Response response = resourceDataService.saveResource(resource, getRequestorId());
		assertSuccess(response);
		final Resource dto = resourceDataService.getResource((String)response.getResponseValue(), getDefaultLanguage());
		Assert.assertNotNull(dto);
		return dto;
	}
	
	private ResourceProp generateResourceProp() {
		final ResourceProp prop = new ResourceProp();
		prop.setName(getRandomName());
		prop.setValue(getRandomName());
		return prop;
	}

	@Override
	protected void delete(Resource dto) {
		resourceDataService.deleteResource(dto.getId(), getRequestorId());
	}

	@Override
	protected boolean isIndexed(String id) {
		return resourceDataService.isIndexed(id);
	}
}
