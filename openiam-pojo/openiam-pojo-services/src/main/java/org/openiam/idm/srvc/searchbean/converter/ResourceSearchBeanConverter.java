package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("resourceSearchBeanConverter")
public class ResourceSearchBeanConverter implements SearchBeanConverter<Resource, ResourceSearchBean> {

	@Override
	public Resource convert(ResourceSearchBean searchBean) {
		final Resource resource = new Resource();
		resource.setResourceId(searchBean.getKey());
		resource.setName(searchBean.getName());
		
		if(searchBean.getResourceTypeId() != null && searchBean.getResourceTypeId().trim().length() > 0) {
			final ResourceType type = new ResourceType();
			type.setResourceTypeId(searchBean.getResourceTypeId());
			resource.setResourceType(type);
		}
		return resource;
	}
}
