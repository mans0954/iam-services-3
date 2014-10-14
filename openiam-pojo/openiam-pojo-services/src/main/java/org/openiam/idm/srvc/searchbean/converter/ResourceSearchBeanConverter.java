package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
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
        resource.setURL(searchBean.getURL());
		resource.setCoorelatedName(searchBean.getCoorelatedName());
		if(StringUtils.isNotBlank(searchBean.getResourceTypeId())) {
			final ResourceTypeEntity type = new ResourceTypeEntity();
			type.setId(searchBean.getResourceTypeId());
			resource.setResourceType(type);
		}
		if(StringUtils.isNotBlank(searchBean.getAdminResourceId())) {
			final ResourceEntity adminResource = new ResourceEntity();
			adminResource.setId(searchBean.getAdminResourceId());
			resource.setAdminResource(adminResource);
		}
		return resource;
	}
}
