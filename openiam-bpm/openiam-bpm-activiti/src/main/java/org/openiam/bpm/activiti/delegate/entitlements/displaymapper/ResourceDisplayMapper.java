package org.openiam.bpm.activiti.delegate.entitlements.displaymapper;

import java.util.LinkedHashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class ResourceDisplayMapper extends AbstractActivitiJob {
	
	@Autowired
	private ResourceTypeDAO resourceTypeDAO;
	
	public ResourceDisplayMapper() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final LinkedHashMap<String, String> metadataMap = new LinkedHashMap<String, String>();
		final Resource resource = getObjectVariable(execution, ActivitiConstants.RESOURCE, Resource.class);
		if(StringUtils.isNotBlank(resource.getName())) {
			metadataMap.put("Name", resource.getName());
		}
		
		if(StringUtils.isNotBlank(resource.getDescription())) {
			metadataMap.put("Description", resource.getDescription());
		}
		
		if(StringUtils.isNotBlank(resource.getURL())) {
			metadataMap.put("URL", resource.getURL());
		}
		
		if(resource.getResourceType() != null && StringUtils.isNotBlank(resource.getResourceType().getId())) {
			final ResourceTypeEntity type = resourceTypeDAO.findById(resource.getResourceType().getId());
			if(type != null) {
				metadataMap.put("Resource Type", type.getDescription());
			}
		}
		
		if(CollectionUtils.isNotEmpty(resource.getResourceProps())) {
			for(final ResourceProp prop : resource.getResourceProps()) {
				if(StringUtils.isNotBlank(prop.getName()) && StringUtils.isNotBlank(prop.getPropValue())) {
					metadataMap.put(prop.getName(), prop.getPropValue());
				}
			}
		}
		
		execution.setVariable(ActivitiConstants.REQUEST_METADATA_MAP.getName(), metadataMap);
	}
}
