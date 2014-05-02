package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.FixedValue;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;

public class EntityMembershipDelegate extends AbstractEntitlementsDelegate {
	
	public EntityMembershipDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Response response = null;
		final String associationId = getAssociationId(execution);
		final String memberAssociationId = getMemberAssociationId(execution);
		
		Group group = null;
		Role role = null;
		User user = null;
		Resource resource = null;
		Organization organization = null;
		
		boolean provisioningEnabled = isProvisioningEnabled(execution);
		
		final ActivitiRequestType requestType = getRequestType(execution); 
		if(requestType != null) {
			switch(requestType) {
				case ADD_GROUP_TO_GROUP:
					response = groupDataService.addChildGroup(associationId, memberAssociationId, systemUserId);
					break;
				case REMOVE_GROUP_FROM_GROUP:
					response = groupDataService.removeChildGroup(associationId, memberAssociationId, systemUserId);
					break;
				case ADD_ROLE_TO_GROUP:
					response = roleDataService.addGroupToRole(memberAssociationId, associationId, systemUserId);
					break;
				case REMOVE_ROLE_FROM_GROUP:
					response = roleDataService.removeGroupFromRole(memberAssociationId, associationId, systemUserId);
					break;
				case ENTITLE_RESOURCE_TO_GROUP:
					response = resourceDataService.addGroupToResource(associationId, memberAssociationId, systemUserId);
					break;
				case DISENTITLE_RESOURCE_FROM_GROUP:
					response = resourceDataService.removeGroupToResource(associationId, memberAssociationId, systemUserId);
					break;
				case ADD_ROLE_TO_ROLE:
					response = roleDataService.addChildRole(associationId, memberAssociationId, systemUserId);
					break;
				case REMOVE_ROLE_FROM_ROLE:
					response = roleDataService.removeChildRole(associationId, memberAssociationId, systemUserId);
					break;
				case ENTITLE_RESOURCE_TO_ROLE:
					response = resourceDataService.addRoleToResource(associationId, memberAssociationId, systemUserId);
					break;
				case DISENTITLE_RESOURCE_FROM_ROLE:
					response = resourceDataService.removeRoleToResource(associationId, memberAssociationId, systemUserId);
					break;
				case ADD_RESOURCE_TO_RESOURCE:
					response = resourceDataService.addChildResource(associationId, memberAssociationId, systemUserId);
					break;
				case REMOVE_RESOURCE_FROM_RESOURCE:
					response = resourceDataService.deleteChildResource(associationId, memberAssociationId, systemUserId);
					break;
				case ENTITLE_USER_TO_RESOURCE:
					if(provisioningEnabled) {
						resource = getResource(associationId);
						user = getUser(memberAssociationId);
						if(resource != null && user != null) {
							final ProvisionUser pUser = new ProvisionUser(user);
							resource.setOperation(AttributeOperationEnum.ADD);
							pUser.addResource(resource);
							response = provisionService.modifyUser(pUser);
						}
					} else {
						response = resourceDataService.addUserToResource(associationId, memberAssociationId, systemUserId);
					}
					break;
				case DISENTITLE_USR_FROM_RESOURCE:
					if(provisioningEnabled) {
						resource = getResource(associationId);
						user = getUser(memberAssociationId);
						if(resource != null && user != null) {
							 final ProvisionUser pUser = new ProvisionUser(user);
							 pUser.markResourceAsDeleted(resource.getId());
							 response = provisionService.modifyUser(pUser);
						}
					} else {
						response = resourceDataService.removeUserFromResource(associationId, memberAssociationId, systemUserId);
					}
					break;
				case ADD_USER_TO_GROUP:
					if(provisioningEnabled) {
						group = getGroup(associationId);
						user = getUser(memberAssociationId);
						if(group != null && user != null) {
							group.setOperation(AttributeOperationEnum.ADD);
							final ProvisionUser pUser = new ProvisionUser(user);
							pUser.addGroup(group);
							response = provisionService.modifyUser(pUser);
						}
					} else {
						response = groupDataService.addUserToGroup(associationId, memberAssociationId, systemUserId);
					}
					break;
				case REMOVE_USER_FROM_GROUP:
					if(provisioningEnabled) {
						group = getGroup(associationId);
						user = getUser(memberAssociationId);
						if(group != null && user != null) {
							final ProvisionUser pUser = new ProvisionUser(user);
							pUser.markGroupAsDeleted(group.getId());
							response = provisionService.modifyUser(pUser);
						}
					} else {	
						response = groupDataService.removeUserFromGroup(associationId, memberAssociationId, systemUserId);
					}
					break;
				case ADD_USER_TO_ROLE:
					if(provisioningEnabled) {
						role = getRole(associationId);
						user = getUser(memberAssociationId);
						if(role != null && user != null) {
							final ProvisionUser pUser = new ProvisionUser(user);
							role.setOperation(AttributeOperationEnum.ADD);
				            pUser.addRole(role);
				            response = provisionService.modifyUser(pUser);
						}
					} else {	
						response = roleDataService.addUserToRole(associationId, memberAssociationId, systemUserId);
					}
					break;
				case REMOVE_USER_FROM_ROLE:
					if(provisioningEnabled) {
						role = getRole(associationId);
						user = getUser(memberAssociationId);
						if(role != null && user != null) {
							final ProvisionUser pUser = new ProvisionUser(user);
							pUser.markRoleAsDeleted(role.getId());
							response = provisionService.modifyUser(pUser);
						}
					} else {	
						response = roleDataService.removeUserFromRole(associationId, memberAssociationId, systemUserId);
					}
					break;
				case ADD_USER_TO_ORG:
					if(provisioningEnabled) {
						organization = getOrganization(associationId);
						user = getUser(memberAssociationId);
						if(organization != null && user != null) {
							organization.setOperation(AttributeOperationEnum.ADD);
							final ProvisionUser pUser = new ProvisionUser(user);
							pUser.addAffiliation(organization);
							response = provisionService.modifyUser(pUser);
						}
					} else {	
						response = organizationDataService.addUserToOrg(associationId, memberAssociationId);
					}
					break;
				case REMOVE_USER_FROM_ORG:
					if(provisioningEnabled) {
						organization = getOrganization(associationId);
						user = getUser(memberAssociationId);
						if(organization != null && user != null) {
							final ProvisionUser pUser = new ProvisionUser(user);
							pUser.markAffiliateAsDeleted(organization.getId());
							response = provisionService.modifyUser(pUser);
						}
					} else {	
						response = organizationDataService.removeUserFromOrg(associationId, memberAssociationId);
					}
					break;
				default:
					throw new IllegalArgumentException("Request type is invalid");
			}
		}
		
		if(response == null || response.isFailure()) {
			throw new ActivitiException(String.format("Operation returned 'failure', or response was null: %s, or request type '%s' is invalid", response, requestType));
		}
		//TODO:  validate
	}
}
