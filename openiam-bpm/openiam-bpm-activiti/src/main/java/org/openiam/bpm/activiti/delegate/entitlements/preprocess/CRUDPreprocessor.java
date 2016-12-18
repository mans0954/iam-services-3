package org.openiam.bpm.activiti.delegate.entitlements.preprocess;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class CRUDPreprocessor extends AbstractEntitlementsDelegate {
	
	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private GroupDataService groupService;
	
	@Autowired
	private RoleDataService roleService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private AuthorizationManagerService authManagerService;
	
	@Value("${org.openiam.ui.admin.right.id}")
	private String adminRightId;

	public CRUDPreprocessor() {
		super();
	}
	
	@Override
	protected void doExecute(DelegateExecution execution) throws Exception {
		final AssociationType type = AssociationType.getByValue(getStringVariable(execution, ActivitiConstants.ASSOCIATION_TYPE));
		final String associationId = getStringVariable(execution, ActivitiConstants.ASSOCIATION_ID);
		final String requestorId = getRequestorId(execution);
		
		boolean isAdmin = false;
		if(type != null) {
			switch(type) {
				case RESOURCE:
					final ResourceEntity resource = resourceService.findResourceById(associationId);
					if(resource != null) {
						isAdmin = authManagerService.isEntitled(requestorId, resource.getId(), adminRightId);
					}
					break;
				case GROUP:
					final GroupEntity group = groupService.getGroup(associationId);
					if(group != null) {
						isAdmin = authManagerService.isMemberOfGroup(requestorId, group.getId(), adminRightId);
					}
					break;
				case ROLE:
					final RoleEntity role = roleService.getRole(associationId);
					if(role != null) {
						isAdmin = authManagerService.isMemberOfRole(requestorId, role.getId(), adminRightId);
					}
					break;
				case ORGANIZATION:
					final OrganizationEntity organization = organizationService.getOrganization(associationId);
					if(organization != null) {
						isAdmin = authManagerService.isMemberOfOrganization(requestorId, organization.getId(), adminRightId);
					}
					break;
				default:
					break;
			}
		}
		execution.setVariable(ActivitiConstants.IS_ADMIN.getName(), isAdmin);
	}
}
