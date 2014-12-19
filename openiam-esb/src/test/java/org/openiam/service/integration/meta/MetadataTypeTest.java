package org.openiam.service.integration.meta;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.ws.MetadataWebService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class MetadataTypeTest extends AbstractKeyNameServiceTest<MetadataType, MetadataTypeSearchBean> {

	@Override
	protected MetadataType newInstance() {
		final MetadataType type = new MetadataType();
		type.setDisplayNameMap(generateRandomLanguageMapping());
		return type;
	}

	@Override
	protected MetadataTypeSearchBean newSearchBean() {
		return new MetadataTypeSearchBean();
	}

	@Override
	protected Response save(MetadataType t) {
		return metadataServiceClient.saveMetadataType(t);
	}

	@Override
	protected Response delete(MetadataType t) {
		return metadataServiceClient.deleteMetadataType(t.getId());
	}

	@Override
	protected MetadataType get(String key) {
		final MetadataTypeSearchBean searchBean = newSearchBean();
		searchBean.setKey(key);
		searchBean.setDeepCopy(true);
		final List<MetadataType> results = metadataServiceClient.findTypeBeans(searchBean, 0, 1, null);
		return (CollectionUtils.isNotEmpty(results)) ? results.get(0) : null;
	}

	@Override
	public List<MetadataType> find(MetadataTypeSearchBean searchBean, int from,
			int size) {
		searchBean.setDeepCopy(true);
		return metadataServiceClient.findTypeBeans(searchBean, from, size, getDefaultLanguage());
	}

}
