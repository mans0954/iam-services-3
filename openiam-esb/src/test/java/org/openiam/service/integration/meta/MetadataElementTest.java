package org.openiam.service.integration.meta;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MetadataElementTest extends AbstractKeyNameServiceTest<MetadataElement, MetadataElementSearchBean> {
	
	@Override
	protected MetadataElement newInstance() {
		final MetadataElement element = new MetadataElement();
		element.setAttributeName(getRandomName());
		element.setMetadataTypeId(metadataServiceClient.findTypeBeans(new MetadataTypeSearchBean(), 0, 1).get(0).getId());
		return element;
	}
	
	@Test
	public void clusterTest() throws Exception {
		final ClusterKey<MetadataElement, MetadataElementSearchBean> key = doClusterTest();
		final MetadataElement instance = key.getDto();
		if(instance != null && instance.getId() != null) {
			deleteAndAssert(instance);
    	}
	}

	@Override
	protected MetadataElementSearchBean newSearchBean() {
		return new MetadataElementSearchBean();
	}

	@Override
	protected Response save(MetadataElement t) {
		return metadataServiceClient.saveMetadataElement(t);
	}

	@Override
	protected Response delete(MetadataElement t) {
		return metadataServiceClient.deleteMetadataElement(t.getId());
	}

	@Override
	protected MetadataElement get(String key) {
		final MetadataElementSearchBean searchBean = new MetadataElementSearchBean();
		searchBean.addKey(key);
		searchBean.setDeepCopy(true);
		final List<MetadataElement> results = find(searchBean, 0, 1);
		return (CollectionUtils.isNotEmpty(results)) ? results.get(0) : null;
	}

	@Override
	public List<MetadataElement> find(MetadataElementSearchBean searchBean,
			int from, int size) {
		return metadataServiceClient.findElementBeans(searchBean, from, size);
	}

	@Test
	public void testSaveWithMessagingEnabled() {
		final MetadataElement e = newInstance();
		final MetadataTypeSearchBean sb = new MetadataTypeSearchBean();
		sb.setGrouping(MetadataTypeGrouping.GROUP_TYPE);
		e.setMetadataTypeId(metadataServiceClient.findTypeBeans(sb, 0, 1).get(0).getId());
		e.setRequired(true);
		assertSuccess(save(e));
	}

	private MetadataElementSearchBean getCacheableSearchBean(final MetadataElement entity) {
		final MetadataElementSearchBean sb = new MetadataElementSearchBean();
		sb.setFindInCache(true);
		sb.setDeepCopy(true);
		sb.setNameToken(new SearchParam(entity.getName(), MatchType.EXACT));
		return sb;
	}
	
	private MetadataElement createAndSave() {
		MetadataElement entity = createBean();
		final Response response = metadataServiceClient.saveMetadataElement(entity);
		assertSuccess(response);
		entity = get((String)response.getResponseValue());
		Assert.assertNotNull(entity);
		return entity;
	}
	
	@Test
	public void testCreateAndDelete() throws Exception {
		for(int j = 0; j < 2; j++) {
			final MetadataElement entity = createAndSave();
			Assert.assertNotNull(get(entity.getId()));
			deleteAndAssert(entity);
			Assert.assertNull(get(entity.getId()));
		}
	}
}
