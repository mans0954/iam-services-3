package org.openiam.service.integration.meta;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

public class MetadataElementTest extends AbstractKeyNameServiceTest<MetadataElement, MetadataElementSearchBean> {
	
	@Override
	protected MetadataElement newInstance() {
		final MetadataElement element = new MetadataElement();
		element.setAttributeName(getRandomName());
		element.setMetadataTypeId(metadataServiceClient.findTypeBeans(null, 0, 1, null).get(0).getId());
		return element;
	}

	@Override
	protected MetadataElementSearchBean newSearchBean() {
		return new MetadataElementSearchBean();
	}

	@Override
	protected Response save(MetadataElement t) {
		return metadataServiceClient.saveMetadataEntity(t);
	}

	@Override
	protected Response delete(MetadataElement t) {
		return metadataServiceClient.deleteMetadataElement(t.getId());
	}

	@Override
	protected MetadataElement get(String key) {
		final MetadataElementSearchBean searchBean = new MetadataElementSearchBean();
		searchBean.setKey(key);
		searchBean.setDeepCopy(true);
		final List<MetadataElement> results = find(searchBean, 0, 1);
		return (CollectionUtils.isNotEmpty(results)) ? results.get(0) : null;
	}

	@Override
	public List<MetadataElement> find(MetadataElementSearchBean searchBean,
			int from, int size) {
		return metadataServiceClient.findElementBeans(searchBean, from, size, null);
	}

	@Test
	public void testSaveWithMessagingEnabled() {
		final MetadataElement e = newInstance();
		final MetadataTypeSearchBean sb = new MetadataTypeSearchBean();
		sb.setGrouping(MetadataTypeGrouping.GROUP_TYPE);
		e.setMetadataTypeId(metadataServiceClient.findTypeBeans(sb, 0, 1, getDefaultLanguage()).get(0).getId());
		e.setRequired(true);
		assertSuccess(save(e));
	}
}
