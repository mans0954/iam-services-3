package org.openiam.mq;

import org.openiam.base.request.BaseCrudServiceRequest;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.response.data.MetadataElementResponse;
import org.openiam.base.response.data.MetadataTypeResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.response.list.MetadataElementListResponse;
import org.openiam.base.response.list.MetadataTypeListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.mq.constants.api.common.MetadataAPI;
import org.openiam.mq.constants.queue.common.MetadataQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 05/12/16.
 */
@RabbitListener(id="metadataQueueListener",
        queues = "#{MetadataQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
@Component
public class MetadataQueueListener extends AbstractListener<MetadataAPI> {
    @Autowired
    private MetadataService metadataService;
    @Autowired
    public MetadataQueueListener(MetadataQueue queue) {
        super(queue);
    }

    protected RequestProcessor<MetadataAPI, EmptyServiceRequest> getEmptyRequestProcessor(){
        return null;
    }
    protected RequestProcessor<MetadataAPI, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return new RequestProcessor<MetadataAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(MetadataAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case FindElementBeans:
                        response = new MetadataElementListResponse();
                        ((MetadataElementListResponse)response).setList(metadataService.findBeans(((MetadataElementSearchBean)request.getSearchBean()),
                                request.getFrom(), request.getSize(), request.getLanguage()));
                        break;
                    case CountElementBeans:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(metadataService.count(((MetadataElementSearchBean)request.getSearchBean())));
                        break;
                    case FindTypeBeans:
                        response = new MetadataTypeListResponse();
                        ((MetadataTypeListResponse)response).setList(metadataService.findBeans(((MetadataTypeSearchBean)request.getSearchBean()),
                                request.getFrom(), request.getSize(), request.getLanguage()));
                        break;
                    case CountTypeBeans:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(metadataService.count(((MetadataTypeSearchBean)request.getSearchBean())));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<MetadataAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<MetadataAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(MetadataAPI api, IdServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetMetadataElement:
                        response = new MetadataElementResponse();
                        ((MetadataElementResponse)response).setValue(metadataService.findElementById(request.getId(), request.getLanguage()));
                        break;
                    case GetMetadataType:
                        response = new MetadataTypeResponse();
                        ((MetadataTypeResponse)response).setValue(metadataService.findById(request.getId()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<MetadataAPI, BaseCrudServiceRequest> getCrudRequestProcessor(){
        return new RequestProcessor<MetadataAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(MetadataAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case SaveMetadataType:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(metadataService.save(((MetadataType)request.getObject())));
                        break;
                    case SaveMetadataElement:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(metadataService.save(((MetadataElement)request.getObject())));
                        break;
                    case DeleteMetadataType:
                        response = new Response();
                        metadataService.deleteMetdataType(request.getObject().getId());
                        break;
                    case DeleteMetadataElement:
                        response = new Response();
                        metadataService.deleteMetdataElement(request.getObject().getId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
}
