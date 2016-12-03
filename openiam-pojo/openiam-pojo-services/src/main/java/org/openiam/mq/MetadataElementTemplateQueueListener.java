package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.data.*;
import org.openiam.base.response.list.MetadataElementPageTemplateListResponse;
import org.openiam.base.response.list.MetadataTemplateTypeFieldListResponse;
import org.openiam.base.response.list.MetadataTemplateTypeListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.openiam.idm.srvc.meta.dto.TemplateRequest;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.common.TemplateAPI;
import org.openiam.mq.constants.queue.common.MetadataElementTemplateQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 01/12/16.
 */
@RabbitListener(id="metadataElementTemplateQueueListener",
        queues = "#{MetadataElementTemplateQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
@Component
public class MetadataElementTemplateQueueListener extends AbstractListener<TemplateAPI> {
    @Autowired
    private MetadataElementTemplateService templateService;

    @Autowired
    public MetadataElementTemplateQueueListener(MetadataElementTemplateQueue queue) {
        super(queue);
    }


    protected RequestProcessor<TemplateAPI, EmptyServiceRequest> getEmptyRequestProcessor(){
        return null;
    }
    protected RequestProcessor<TemplateAPI, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return new RequestProcessor<TemplateAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(TemplateAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case FindBeans:
                        response = new MetadataElementPageTemplateListResponse();
                        ((MetadataElementPageTemplateListResponse)response).setList(templateService.findBeans((MetadataElementPageTemplateSearchBean)(request.getSearchBean()),
                                                                                                                request.getFrom(), request.getSize()));
                        break;
                    case Count:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(templateService.count((MetadataElementPageTemplateSearchBean)(request.getSearchBean())));
                        break;
                    case FindTemplateType:
                        response = new MetadataTemplateTypeListResponse();
                        ((MetadataTemplateTypeListResponse)response).setList(templateService.findTemplateTypes((MetadataTemplateTypeSearchBean)(request.getSearchBean()),
                                request.getFrom(), request.getSize()));
                        break;
                    case FindUIFIelds:
                        response = new MetadataTemplateTypeFieldListResponse();
                        ((MetadataTemplateTypeFieldListResponse)response).setList(templateService.findUIFields((MetadataTemplateTypeFieldSearchBean)(request.getSearchBean()),
                                request.getFrom(), request.getSize()));
                        break;
                    case CountUIFields:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(templateService.countUIFields((MetadataTemplateTypeFieldSearchBean)(request.getSearchBean())));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<TemplateAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<TemplateAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(TemplateAPI templateAPI, IdServiceRequest request) throws BasicDataServiceException {
                MetadataTemplateTypeResponse response = new MetadataTemplateTypeResponse();
                response.setValue(templateService.getTemplateType(request.getId()));
                return response;
            }
        };
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) TemplateAPI api, TemplateRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<TemplateAPI, TemplateRequest>(){
            @Override
            public Response doProcess(TemplateAPI api, TemplateRequest request) throws BasicDataServiceException {
                PageTempateResponse response = new PageTempateResponse();
                response.setValue(templateService.getTemplate(request));
                return response;
            }
        });
    }
    protected RequestProcessor<TemplateAPI, BaseCrudServiceRequest> getCrudRequestProcessor(){
        return new RequestProcessor<TemplateAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(TemplateAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case Save:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(templateService.save((MetadataElementPageTemplate)(request.getObject())));
                        break;
                    case Delete:
                        response = new Response();
                        templateService.delete(request.getObject().getId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
}
