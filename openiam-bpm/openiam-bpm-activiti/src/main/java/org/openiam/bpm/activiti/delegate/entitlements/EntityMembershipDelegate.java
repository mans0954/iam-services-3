package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.SysConfiguration;
import org.openiam.base.TreeNode;
import org.openiam.base.request.AccessReviewRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.constants.AccessReviewConstant;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.model.AccessViewBean;
import org.openiam.model.AccessViewFilterBean;
import org.openiam.model.AccessViewResponse;
import org.openiam.mq.constants.api.AccessReviewAPI;
import org.openiam.mq.constants.queue.am.AccessReviewQueue;
import org.openiam.mq.utils.RabbitMQSender;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;

public class EntityMembershipDelegate extends AbstractEntitlementsDelegate {
    @Autowired
    protected RabbitMQSender rabbitMQSender;
    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    protected AccessReviewQueue accessReviewQueue;

    public EntityMembershipDelegate() {
        super();
    }

    @Override
    protected void doExecute(DelegateExecution execution) throws Exception {
        Response response = null;
        final String associationId = getAssociationId(execution);
        final String memberAssociationId = getMemberAssociationId(execution);
        final Set<String> rights = getAccessRights(execution);
        final Date startDate = getStartDate(execution);
        final Date endDate = getEndDate(execution);
        final String userNotes = getUserNotes(execution);


        Group group = null;
        Role role = null;
        User user = null;
        Resource resource = null;
        Organization organization = null;

        boolean provisioningEnabled = isProvisioningEnabled(execution);

        AuditAction action = null;

        final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        try {
            final ActivitiRequestType requestType = getRequestType(execution);
            if (requestType != null) {
                switch (requestType) {
                    case ADD_GROUP_TO_GROUP:
                        action = AuditAction.ADD_CHILD_GROUP;
                        response = groupDataService.addChildGroup(associationId, memberAssociationId, systemUserId, rights, startDate, endDate);
                        break;
                    case REMOVE_GROUP_FROM_GROUP:
                        action = AuditAction.REMOVE_CHILD_GROUP;
                        response = groupDataService.removeChildGroup(associationId, memberAssociationId, systemUserId);
                        break;
                    case ADD_ROLE_TO_GROUP:
                        action = AuditAction.ADD_ROLE_TO_GROUP;
                        roleDataService.addGroupToRole(associationId, memberAssociationId, rights, startDate, endDate);
                        response = new Response(ResponseStatus.SUCCESS);
                        break;
                    case REMOVE_ROLE_FROM_GROUP:
                        action = AuditAction.REMOVE_ROLE_FROM_GROUP;
                        roleDataService.removeGroupFromRole(associationId, memberAssociationId);
                        response = new Response(ResponseStatus.SUCCESS);
                        break;
                    case ENTITLE_RESOURCE_TO_GROUP:
                        action = AuditAction.ADD_GROUP_TO_RESOURCE;
                        response = resourceDataService.addGroupToResource(associationId, memberAssociationId, systemUserId, rights, startDate, endDate);
                        break;
                    case DISENTITLE_RESOURCE_FROM_GROUP:
                        action = AuditAction.REMOVE_GROUP_FROM_RESOURCE;
                        response = resourceDataService.removeGroupToResource(associationId, memberAssociationId, systemUserId);
                        break;
                    case ADD_ROLE_TO_ROLE:
                        action = AuditAction.ADD_CHILD_ROLE;
                        roleDataService.addChildRole(associationId, memberAssociationId, rights, startDate, endDate);
                        response = new Response(ResponseStatus.SUCCESS);
                        break;
                    case REMOVE_ROLE_FROM_ROLE:
                        action = AuditAction.REMOVE_CHILD_ROLE;
                        roleDataService.removeChildRole(associationId, memberAssociationId);
                        response = new Response(ResponseStatus.SUCCESS);
                        break;
                    case ENTITLE_RESOURCE_TO_ROLE:
                        action = AuditAction.ADD_ROLE_TO_RESOURCE;
                        response = resourceDataService.addRoleToResource(associationId, memberAssociationId, systemUserId, rights, startDate, endDate);
                        break;
                    case DISENTITLE_RESOURCE_FROM_ROLE:
                        action = AuditAction.REMOVE_ROLE_FROM_RESOURCE;
                        response = resourceDataService.removeRoleToResource(associationId, memberAssociationId, systemUserId);
                        break;
                    case ADD_RESOURCE_TO_RESOURCE:
                        action = AuditAction.ADD_CHILD_RESOURCE;
                        response = resourceDataService.addChildResource(associationId, memberAssociationId, systemUserId, rights, startDate, endDate);
                        break;
                    case REMOVE_RESOURCE_FROM_RESOURCE:
                        action = AuditAction.REMOVE_CHILD_RESOURCE;
                        response = resourceDataService.deleteChildResource(associationId, memberAssociationId, systemUserId);
                        break;
                    case ENTITLE_USER_TO_RESOURCE:
                        action = AuditAction.ADD_USER_TO_RESOURCE;
                        if (provisioningEnabled) {
                            resource = getResource(associationId);
                            user = getUser(memberAssociationId);
                            if (resource != null && user != null) {
                                final ProvisionUser pUser = new ProvisionUser(user);
                                resource.setOperation(AttributeOperationEnum.ADD);
                                pUser.addResource(resource, rights, startDate, endDate);
                                response = provisionService.modifyUser(pUser);
                            }
                        } else {
                            response = resourceDataService.addUserToResource(associationId, memberAssociationId, systemUserId, rights, startDate, endDate);
                        }
                        break;
                    case RESOURCE_CERTIFICATION:
                    case DISENTITLE_USR_FROM_RESOURCE:
                        action = AuditAction.REMOVE_USER_FROM_RESOURCE;
                        resource = getResource(associationId);
                        user = getUser(memberAssociationId);
                        if (resource != null) {
                            List<String> resourceToDelete = new ArrayList<>();
                            List<String> groupToDelete = new ArrayList<>();
                            List<String> roleToDelete = new ArrayList<>();

                            if (ResourceSearchBean.TYPE_MANAGED_SYS.equals(resource.getResourceType().getId())) {
                                // delete whole access subtree
                                AccessViewFilterBean filterBean = new AccessViewFilterBean();
                                filterBean.setUserId(memberAssociationId);

                                AccessReviewRequest request = new AccessReviewRequest();
                                request.setParentId(associationId);
                                request.setParentBeanType(AccessReviewConstant.RESOURCE_TYPE);
                                request.setRootOnly(false);
                                request.setFilterBean(filterBean);
                                request.setViewType(AccessReviewConstant.RESOURCE_VIEW);
                                request.setDate(null);
                                request.setLanguage(null);
                                AccessViewResponse accessViewResponse = rabbitMQSender.sendAndReceive(accessReviewQueue, AccessReviewAPI.AccessReviewSubTree, request, AccessViewResponse.class);

                                if (accessViewResponse != null && CollectionUtils.isNotEmpty(accessViewResponse.getBeans())) {
                                    // look through the tree for direct entitlements
                                    List<TreeNode<AccessViewBean>> treeNodes = accessViewResponse.getBeans();
                                    for (int i = 0; i < treeNodes.size(); i++) {
                                        TreeNode<AccessViewBean> node = treeNodes.get(i);
                                        AccessViewBean data = node.getData();

                                        if (CollectionUtils.isNotEmpty(node.getChildren())) {
                                            treeNodes.addAll(node.getChildren());
                                        }

                                        if (node.getIsDeletable()) {
                                            switch (data.getBeanType()) {
                                                case AccessReviewConstant.RESOURCE_TYPE:
                                                    resourceToDelete.add(data.getId());
                                                    break;
                                                case AccessReviewConstant.GROUP_TYPE:
                                                    groupToDelete.add(data.getId());
                                                    break;
                                                case AccessReviewConstant.ROLE_TYPE:
                                                    roleToDelete.add(data.getId());
                                                    break;
                                            }
                                        }
                                    }
                                }
                                // try to update user
                                if (provisioningEnabled) {
                                    if (resource != null && user != null) {
                                        final ProvisionUser pUser = new ProvisionUser(user);
                                        pUser.removeResource(resource);

                                        if (CollectionUtils.isNotEmpty(resourceToDelete)) {
                                            for (String id : resourceToDelete) {
                                                pUser.removeResource(resource);
                                            }
                                        }
                                        if (CollectionUtils.isNotEmpty(groupToDelete)) {
                                            for (String id : groupToDelete) {
                                                pUser.removeGroup(id);
                                            }
                                        }
                                        if (CollectionUtils.isNotEmpty(roleToDelete)) {
                                            for (String id : roleToDelete) {
                                                pUser.markRoleAsDeleted(id);
                                            }
                                        }
                                        response = provisionService.modifyUser(pUser);
                                    }
                                } else {
                                    response = resourceDataService.removeUserFromResource(associationId, memberAssociationId, systemUserId);
                                    if (CollectionUtils.isNotEmpty(resourceToDelete)) {
                                        for (String id : resourceToDelete) {
                                            response = resourceDataService.removeUserFromResource(id, memberAssociationId, systemUserId);
                                        }
                                    }
                                    if (CollectionUtils.isNotEmpty(groupToDelete)) {
                                        for (String id : groupToDelete) {
                                            response = groupDataService.removeUserFromGroup(id, memberAssociationId, systemUserId);
                                        }
                                    }
                                    if (CollectionUtils.isNotEmpty(roleToDelete)) {
                                        for (String id : roleToDelete) {
                                            roleDataService.removeUserFromRole(id, memberAssociationId);
                                            response = new Response(ResponseStatus.SUCCESS);
                                        }
                                    }
                                }
                            } else {
                                if (provisioningEnabled) {
                                    if (resource != null && user != null) {
                                        final ProvisionUser pUser = new ProvisionUser(user);
                                        pUser.removeResource(resource);
                                        response = provisionService.modifyUser(pUser);
                                    }
                                } else {
                                    response = resourceDataService.removeUserFromResource(associationId, memberAssociationId, systemUserId);
                                }
                            }
                        }
                        break;
                    case ADD_USER_TO_GROUP:
                        action = AuditAction.ADD_USER_TO_GROUP;
                        if (provisioningEnabled) {
                            group = getGroup(associationId);
                            user = getUser(memberAssociationId);
                            if (group != null && user != null) {
                                group.setOperation(AttributeOperationEnum.ADD);
                                final ProvisionUser pUser = new ProvisionUser(user);
                                pUser.addGroup(group, rights, startDate, endDate);
                                response = provisionService.modifyUser(pUser);
                            }
                        } else {
                            response = groupDataService.addUserToGroup(associationId, memberAssociationId, systemUserId, rights, startDate, endDate);
                        }
                        break;
                    case REMOVE_USER_FROM_GROUP:
                        //TODO:
                        action = AuditAction.REMOVE_USER_FROM_GROUP;
                        if (provisioningEnabled) {
                            group = getGroup(associationId);
                            user = getUser(memberAssociationId);
                            if (group != null && user != null) {
                                final ProvisionUser pUser = new ProvisionUser(user);
                                pUser.removeGroup(group);
                                response = provisionService.modifyUser(pUser);
                            }
                        } else {
                            response = groupDataService.removeUserFromGroup(associationId, memberAssociationId, systemUserId);
                        }
                        break;
                    case ADD_USER_TO_ROLE:
                        action = AuditAction.ADD_USER_TO_ROLE;
                        if (provisioningEnabled) {
                            role = getRole(associationId);
                            user = getUser(memberAssociationId);
                            if (role != null && user != null) {
                                final ProvisionUser pUser = new ProvisionUser(user);
                                pUser.addRole(role, rights, startDate, endDate);
                                response = provisionService.modifyUser(pUser);
                            }
                        } else {
                            roleDataService.addUserToRole(associationId, memberAssociationId, rights, startDate, endDate);
                            response = new Response(ResponseStatus.SUCCESS);
                        }
                        break;
                    case REMOVE_USER_FROM_ROLE:
                        //TODO:
                        action = AuditAction.REMOVE_USER_FROM_ROLE;
                        if (provisioningEnabled) {
                            role = getRole(associationId);
                            user = getUser(memberAssociationId);
                            if (role != null && user != null) {
                                final ProvisionUser pUser = new ProvisionUser(user);
                                pUser.removeRole(role.getId());
                                response = provisionService.modifyUser(pUser);
                            }
                        } else {
                            roleDataService.removeUserFromRole(associationId, memberAssociationId);
                            response = new Response(ResponseStatus.SUCCESS);
                        }
                        break;
                    case ADD_USER_TO_ORG:
                        action = AuditAction.ADD_USER_TO_ORG;
                        if (provisioningEnabled) {
                            organization = getOrganization(associationId);
                            user = getUser(memberAssociationId);
                            if (organization != null && user != null) {
                                organization.setOperation(AttributeOperationEnum.ADD);
                                final ProvisionUser pUser = new ProvisionUser(user);
                                pUser.addAffiliation(organization, rights, startDate, endDate);
                                response = provisionService.modifyUser(pUser);
                            }
                        } else {
                            response = organizationDataService.addUserToOrg(associationId, memberAssociationId, systemUserId, rights, startDate, endDate);
                        }
                        break;
                    case REMOVE_USER_FROM_ORG:
                        action = AuditAction.REMOVE_USER_FROM_ORG;
                        if (provisioningEnabled) {
                            organization = getOrganization(associationId);
                            user = getUser(memberAssociationId);
                            if (organization != null && user != null) {
                                final ProvisionUser pUser = new ProvisionUser(user);
                                pUser.removeAffiliation(organization.getId());
                                response = provisionService.modifyUser(pUser);
                            }
                        } else {
                            response = organizationDataService.removeUserFromOrg(associationId, memberAssociationId, systemUserId);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Request type is invalid");
                }
            }

            if (response == null || response.isFailure()) {
                throw new ActivitiException(String.format("Operation returned 'failure', or response was null: %s, or request type '%s' is invalid", response, requestType));
            }
            idmAuditLog.succeed();
            //TODO:  validate
        } catch (Throwable e) {
            idmAuditLog.setException(e);
            idmAuditLog.fail();
            throw new RuntimeException(e);
        } finally {
            if (action != null) {
                idmAuditLog.setAction(action.value());
            }
            addAuditLogChild(execution, idmAuditLog);
        }
    }
}
