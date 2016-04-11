package org.openiam.bpm.activiti.delegate.entitlements.displaymapper;

import java.util.LinkedHashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.openiam.idm.srvc.grp.dto.GroupRequestModel;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.GroupToOrgMembershipXref;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupDisplayMapper extends AbstractActivitiJob {

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private ManagedSystemService managedSysService;
	
	public GroupDisplayMapper() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final LinkedHashMap<String, String> metadataMap = new LinkedHashMap<String, String>();
		final GroupRequestModel groupRequestModel = getObjectVariable(execution, ActivitiConstants.GROUP, GroupRequestModel.class);
		if(StringUtils.isNotBlank(groupRequestModel.getTargetObject().getName())) {
			metadataMap.put("Name", groupRequestModel.getTargetObject().getName());
		}
		
		if(StringUtils.isNotBlank(groupRequestModel.getTargetObject().getDescription())) {
			metadataMap.put("Description", groupRequestModel.getTargetObject().getDescription());
		}
		
		if(CollectionUtils.isNotEmpty(groupRequestModel.getTargetObject().getOrganizations())) {
			StringBuilder nameBuilder = new StringBuilder();
			for(GroupToOrgMembershipXref org : groupRequestModel.getTargetObject().getOrganizations()){
				final OrganizationEntity entity = organizationService.getOrganization(org.getId(), null);
				if(entity != null) {
					if(nameBuilder.length()>0)
						nameBuilder.append(", ");
					nameBuilder.append(entity.getName());
				}
			}
			if(nameBuilder.length()>0) {
				metadataMap.put("Organization", nameBuilder.toString());
			}
		}
		
		if(StringUtils.isNotBlank(groupRequestModel.getTargetObject().getManagedSysId())) {
			final ManagedSysEntity entity = managedSysService.getManagedSysById(groupRequestModel.getTargetObject().getManagedSysId());
			if(entity != null) {
				metadataMap.put("Managed System", entity.getName());
			}
		}
		
		if(CollectionUtils.isNotEmpty(groupRequestModel.getTargetObject().getAttributes())) {
			for(final GroupAttribute prop : groupRequestModel.getTargetObject().getAttributes()) {
				if(StringUtils.isNotBlank(prop.getName()) && StringUtils.isNotBlank(prop.getValue())) {
					metadataMap.put(prop.getName(), prop.getValue());
				}
			}
		}
		
		setDisplayMap(execution, metadataMap);
	}
}
