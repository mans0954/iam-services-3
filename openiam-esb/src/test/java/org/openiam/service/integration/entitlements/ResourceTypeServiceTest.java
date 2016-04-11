package org.openiam.service.integration.entitlements;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ResourceTypeServiceTest extends AbstractKeyServiceTest<ResourceType, ResourceTypeSearchBean> {
	
	@Autowired
	@Qualifier("resourceServiceClient")
    protected ResourceDataService resourceDataService;

	@Override
	protected ResourceType newInstance() {
		final ResourceType type = new ResourceType();
		return type;
	}

	@Override
	protected ResourceTypeSearchBean newSearchBean() {
		return new ResourceTypeSearchBean();
	}

	@Override
	protected Response save(ResourceType t) {
		return resourceDataService.saveResourceType(t, null);
	}

	@Override
	protected Response delete(ResourceType t) {
		return resourceDataService.deleteResourceType(t.getId(), null);
	}

	@Override
	protected ResourceType get(String key) {
		final ResourceTypeSearchBean searchBean = newSearchBean();
		searchBean.setKey(key);
		final List<ResourceType> types = find(searchBean, 0, 1);
		return (CollectionUtils.isNotEmpty(types)) ? types.get(0) : null;
	}

	@Override
	public List<ResourceType> find(ResourceTypeSearchBean searchBean, int from,
			int size) {
		return resourceDataService.findResourceTypes(searchBean, from, size, getDefaultLanguage());
	}

/*	@Override
	protected String getId(ResourceType bean) {
		return bean.getId();
	}

	@Override
	protected void setId(ResourceType bean, String id) {
		bean.setId(id);
	}*/

}
