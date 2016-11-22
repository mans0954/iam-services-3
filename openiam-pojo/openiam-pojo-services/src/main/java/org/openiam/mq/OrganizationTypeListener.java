package org.openiam.mq;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.idm.srvc.org.service.OrganizationTypeService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.OrganizationTypeAPI;
import org.openiam.mq.constants.queue.am.OrganizationTypeQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
@RabbitListener(id="organizationTypeListener",
        queues = "#{OrganizationTypeQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class OrganizationTypeListener extends AbstractListener<OrganizationTypeAPI> {

    @Autowired
    protected OrganizationTypeService organizationTypeService;

    @Autowired
    public OrganizationTypeListener(OrganizationTypeQueue queue) {
        super(queue);
    }

    protected RequestProcessor<OrganizationTypeAPI, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return new RequestProcessor<OrganizationTypeAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(OrganizationTypeAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                BaseSearchServiceRequest<OrganizationTypeSearchBean> req = ((BaseSearchServiceRequest<OrganizationTypeSearchBean>)request);
                switch (api){
                    case FindBeans:
                        response = new OrganizationTypeListResponse();
                        ((OrganizationTypeListResponse)response).setList(organizationTypeService.findBeans(req.getSearchBean(), request.getFrom(), request.getSize(), request.getLanguage()));
                        break;
                    case FindAllowedChildren:
                        response = new OrganizationTypeListResponse();
                        ((OrganizationTypeListResponse)response).setList(organizationTypeService.findAllowedChildrenByDelegationFilter(request.getRequesterId(), request.getLanguage()));
                        break;
                    case GetAllowedParents:
                        response = new OrganizationTypeListResponse();
                        final String id = (CollectionUtils.isNotEmpty((req.getSearchBean().getKeySet()))) ? req.getSearchBean().getKeySet().iterator().next() : null;
                        ((OrganizationTypeListResponse)response).setList(organizationTypeService.getAllowedParents(id, request.getRequesterId(), request.getLanguage()));
                        break;
                    case Count:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(organizationTypeService.count(req.getSearchBean()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<OrganizationTypeAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<OrganizationTypeAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(OrganizationTypeAPI api, IdServiceRequest request) throws BasicDataServiceException {
                OrganizationTypeResponse response = new OrganizationTypeResponse();
                response.setValue(organizationTypeService.findById(request.getId(), request.getLanguage()));
                return response;
            }
        };
    }

    protected RequestProcessor<OrganizationTypeAPI, BaseCrudServiceRequest> getCrudRequestProcessor(){
        return new RequestProcessor<OrganizationTypeAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(OrganizationTypeAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case Save:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(organizationTypeService.save(((BaseCrudServiceRequest<OrganizationType>)request).getObject()));
                        break;
                    case Delete:
                        response = new Response();
                        organizationTypeService.delete(request.getObject().getId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) OrganizationTypeAPI api, MembershipRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<OrganizationTypeAPI, MembershipRequest>(){
            @Override
            public Response doProcess(OrganizationTypeAPI api, MembershipRequest request) throws BasicDataServiceException {
                Response response = new Response();
                switch (api){
                    case AddChild:
                        organizationTypeService.addChild(request.getObjectId(), request.getLinkedObjectId());
                        break;
                    case RemoveChild:
                        organizationTypeService.removeChild(request.getObjectId(), request.getLinkedObjectId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
