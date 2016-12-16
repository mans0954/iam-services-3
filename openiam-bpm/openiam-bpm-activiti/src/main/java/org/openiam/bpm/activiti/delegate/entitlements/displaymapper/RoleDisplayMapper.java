package org.openiam.bpm.activiti.delegate.entitlements.displaymapper;

import java.util.LinkedHashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.springframework.beans.factory.annotation.Autowired;

public class RoleDisplayMapper extends AbstractActivitiJob {

	@Autowired
	private ManagedSystemService managedSysService;
	
	public RoleDisplayMapper() {
		super();
	}
	
	@Override
	protected void doExecute(DelegateExecution execution) throws Exception {
		final LinkedHashMap<String, String> metadataMap = new LinkedHashMap<String, String>();
		final Role role = getObjectVariable(execution, ActivitiConstants.ROLE, Role.class);
		if(StringUtils.isNotBlank(role.getName())) {
			metadataMap.put("Name", role.getName());
		}
		
		if(StringUtils.isNotBlank(role.getDescription())) {
			metadataMap.put("Description", role.getDescription());
		}
		
		if(StringUtils.isNotBlank(role.getManagedSysId())) {
			final ManagedSysEntity entity = managedSysService.getManagedSysById(role.getManagedSysId());
			if(entity != null) {
				metadataMap.put("Managed System", entity.getName());
			}
		}
		
		if(CollectionUtils.isNotEmpty(role.getRoleAttributes())) {
			for(final RoleAttribute prop : role.getRoleAttributes()) {
				if(StringUtils.isNotBlank(prop.getName()) && StringUtils.isNotBlank(prop.getValue())) {
					metadataMap.put(prop.getName(), prop.getValue());
				}
			}
		}
		
		setDisplayMap(execution, metadataMap);
	}
}
