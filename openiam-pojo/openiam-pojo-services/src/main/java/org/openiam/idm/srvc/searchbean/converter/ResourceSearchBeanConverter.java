package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("resourceSearchBeanConverter")
public class ResourceSearchBeanConverter implements SearchBeanConverter<ResourceEntity, ResourceSearchBean> {

	@Override
	public ResourceEntity convert(ResourceSearchBean searchBean) {
		final ResourceEntity resource = new ResourceEntity();
		resource.setId(searchBean.getKey());
		resource.setName(searchBean.getName());
		
		if(searchBean.getResourceTypeId() != null && searchBean.getResourceTypeId().trim().length() > 0) {
			final ResourceTypeEntity type = new ResourceTypeEntity();
			type.setId(searchBean.getResourceTypeId());
			resource.setResourceType(type);
		}
		return resource;
	}
}
