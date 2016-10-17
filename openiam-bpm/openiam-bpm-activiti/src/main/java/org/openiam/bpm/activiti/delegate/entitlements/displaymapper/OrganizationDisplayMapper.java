package org.openiam.bpm.activiti.delegate.entitlements.displaymapper;

import java.util.LinkedHashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.idm.srvc.org.service.OrganizationTypeService;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.springframework.beans.factory.annotation.Autowired;

public class OrganizationDisplayMapper extends AbstractActivitiJob {
	
	@Autowired
	private OrganizationTypeService organizationTypeService;

	public OrganizationDisplayMapper() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final LinkedHashMap<String, String> metadataMap = new LinkedHashMap<String, String>();
		final Organization organization = getObjectVariable(execution, ActivitiConstants.ORGANIZATION, Organization.class);
		
		if(StringUtils.isNotBlank(organization.getName())) {
			metadataMap.put("Name", organization.getName());
		}
		
		if(StringUtils.isNotBlank(organization.getAbbreviation())) {
			metadataMap.put("Abbreviation", organization.getAbbreviation());
		}
		
		if(StringUtils.isNotBlank(organization.getSymbol())) {
			metadataMap.put("Symbol", organization.getSymbol());
		}
		
		if(StringUtils.isNotBlank(organization.getDescription())) {
			metadataMap.put("Description", organization.getDescription());
		}
		
		if(StringUtils.isNotBlank(organization.getAlias())) {
			metadataMap.put("Alias", organization.getAlias());
		}
		
		if(StringUtils.isNotBlank(organization.getDomainName())) {
			metadataMap.put("Domain Name", organization.getDomainName());
		}
		
		if(StringUtils.isNotBlank(organization.getStatus())) {
			metadataMap.put("Status", organization.getStatus());
		}
		
		if(StringUtils.isNotBlank(organization.getLdapStr())) {
			metadataMap.put("LDAP String", organization.getLdapStr());
		}
		
		metadataMap.put("Is Selectable", Boolean.valueOf(organization.isSelectable()).toString());
		
		if(StringUtils.isNotBlank(organization.getOrganizationTypeId())) {
			final OrganizationType organizationType = organizationTypeService.findById(organization.getOrganizationTypeId(), null);
			if(organizationType != null) {
				metadataMap.put("Organization Type", organizationType.getName());
			}
		}
		
		if(CollectionUtils.isNotEmpty(organization.getAttributes())) {
			for(final OrganizationAttribute prop : organization.getAttributes()) {
				if(StringUtils.isNotBlank(prop.getName()) && StringUtils.isNotBlank(prop.getValue())) {
					metadataMap.put(prop.getName(), prop.getValue());
				}
			}
		}
		
		setDisplayMap(execution, metadataMap);
	}
}
