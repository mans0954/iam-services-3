package org.openiam.mq;

import org.openiam.base.TreeObjectId;
import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.response.data.BooleanResponse;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.response.data.RoleResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.response.list.RoleAttributeListResponse;
import org.openiam.base.response.list.RoleListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.RoleAPI;
import org.openiam.mq.constants.queue.am.RoleQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by alexander on 11/08/16.
 */
@Component
@RabbitListener(id="roleListener",
        queues = "#{RoleQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class RoleListener extends AbstractListener<RoleAPI> {
    @Autowired
    protected RoleDataService roleDataService;

    @Autowired
    public RoleListener(RoleQueue queue) {
        super(queue);
    }


    protected RequestProcessor<RoleAPI, EmptyServiceRequest> getEmptyRequestProcessor(){
        return null;
    }
    protected RequestProcessor<RoleAPI, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return new RequestProcessor<RoleAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(RoleAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case FindBeans:
                        response = new RoleListResponse();
                        ((RoleListResponse)response).setList(roleDataService.findBeansDto(((BaseSearchServiceRequest<RoleSearchBean>)request).getSearchBean(), request.getFrom(), request.getSize()));
                        break;
                    case CountBeans:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(roleDataService.countBeans(((BaseSearchServiceRequest<RoleSearchBean>)request).getSearchBean()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<RoleAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<RoleAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(RoleAPI api, IdServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetRoleLocalized:
                        response = new RoleResponse();
                        ((RoleResponse)response).setValue(roleDataService.getRoleDTO(request.getId()));
                        break;
                    case GetRoleAttributes:
                        response = new RoleAttributeListResponse();
                        ((RoleAttributeListResponse)response).setList(roleDataService.getRoleAttributes(request.getId()));
                        break;
                    case GetParentRoles:
                        response = new RoleListResponse();
                        ((RoleListResponse)response).setList(roleDataService.getParentRolesDto(request.getId(), ((GetParentsRequest)request).getFrom(), ((GetParentsRequest)request).getSize()));
                        break;
                    case HasChildEntities:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(roleDataService.hasChildEntities(request.getId()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<RoleAPI, BaseCrudServiceRequest> getCrudRequestProcessor(){
        return new RequestProcessor<RoleAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(RoleAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case ValidateEdit:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(roleDataService.validateEdit(((BaseCrudServiceRequest<Role>)request).getObject()));
                        break;
                    case ValidateDelete:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(roleDataService.validateDelete(request.getObject().getId()));
                        break;
                    case SaveRole:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(roleDataService.saveRole(((BaseCrudServiceRequest<Role>)request).getObject()));
                        break;
                    case RemoveRole:
                        response = new Response();
                        roleDataService.removeRole(request.getObject().getId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) RoleAPI api, IdsServiceRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<RoleAPI, IdsServiceRequest>(){
            @Override
            public Response doProcess(RoleAPI api, IdsServiceRequest request) throws BasicDataServiceException {
                TreeObjectIdListServiceResponse response = new TreeObjectIdListServiceResponse();
                List<TreeObjectId> result = roleDataService.getRolesWithSubRolesIds(request.getIds());
                response.setTreeObjectIds(result);
                return response;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) RoleAPI api, MembershipRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<RoleAPI, MembershipRequest>(){
            @Override
            public Response doProcess(RoleAPI api, MembershipRequest request) throws BasicDataServiceException {
                BooleanResponse response = new BooleanResponse();
                switch (api){
                    case AddGroupToRole:
                        roleDataService.addGroupToRole(request.getObjectId(), request.getLinkedObjectId(), request.getRightIds(),
                                request.getStartDate(), request.getEndDate());
                        break;
                    case ValidateGroup2RoleAddition:
                        roleDataService.validateGroup2RoleAddition(request.getObjectId(), request.getLinkedObjectId());
                        break;
                    case RemoveGroupFromRole:
                        roleDataService.removeGroupFromRole(request.getObjectId(), request.getLinkedObjectId());
                        break;
                    case AddUserToRole:
                        roleDataService.addUserToRole(request.getObjectId(), request.getLinkedObjectId(), request.getRightIds(), request.getStartDate(), request.getEndDate());
                        break;
                    case RemoveUserFromRole:
                        roleDataService.removeUserFromRole(request.getObjectId(), request.getLinkedObjectId());
                        break;
                    case AddChildRole:
                        roleDataService.addChildRole(request.getObjectId(), request.getLinkedObjectId(), request.getRightIds(), request.getStartDate(), request.getEndDate());
                        break;
                    case CanAddChildRole:
                        roleDataService.validateRole2RoleAddition(request.getObjectId(), request.getLinkedObjectId(), request.getRightIds(),
                                request.getStartDate(), request.getEndDate());
                        break;
                    case RemoveChildRole:
                        roleDataService.removeChildRole(request.getObjectId(), request.getLinkedObjectId());
                        break;
                    case CanAddUserToRole:
                        roleDataService.canAddUserToRole(request.getLinkedObjectId(), request.getObjectId());
                        break;
                    case CanRemoveUserFromRole:
                        roleDataService.canRemoveUserFromRole(request.getLinkedObjectId(), request.getObjectId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                response.setValue(Boolean.TRUE);
                return response;
            }
        });
    }
}
