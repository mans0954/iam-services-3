package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.springframework.stereotype.Component;

@Component("resourceSearchBeanConverter")
public class ResourceSearchBeanConverter implements SearchBeanConverter<ResourceEntity, ResourceSearchBean> {

	@Override
	public ResourceEntity convert(ResourceSearchBean searchBean) {
		final ResourceEntity resource = new ResourceEntity();
		resource.setId(searchBean.getKey());
		resource.setName(searchBean.getName());
        resource.setRisk(searchBean.getRisk());
		
		if(searchBean.getResourceTypeId() != null && searchBean.getResourceTypeId().trim().length() > 0) {
			final ResourceTypeEntity type = new ResourceTypeEntity();
			type.setId(searchBean.getResourceTypeId());
			resource.setResourceType(type);
		}
		return resource;
	}
}
