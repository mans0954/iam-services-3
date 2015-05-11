package org.openiam.service.integration.meta;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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

	@Override
	protected String getId(MetadataElement bean) {
		return bean.getId();
	}

	@Override
	protected void setId(MetadataElement bean, String id) {
		bean.setId(id);
	}

	@Override
	protected void setName(MetadataElement bean, String name) {
		bean.setAttributeName(name);
	}

	@Override
	protected String getName(MetadataElement bean) {
		return bean.getAttributeName();
	}

	@Override
	protected void setNameForSearch(MetadataElementSearchBean searchBean, String name) {
		searchBean.setAttributeName(name);
	}

}
