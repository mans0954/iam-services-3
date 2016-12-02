package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.dto.AuthLevelAttribute;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbean.URIPatternSearchBean;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.*;
import org.openiam.base.response.data.*;
import org.openiam.base.response.list.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.api.ContentProviderAPI;
import org.openiam.mq.constants.queue.am.ContentProviderQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
@RabbitListener(id="contentProviderListener",
        queues = "#{ContentProviderQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class ContentProviderListener extends AbstractListener<ContentProviderAPI> {

    @Autowired
    private ContentProviderService contentProviderService;

    @Autowired
    public ContentProviderListener(ContentProviderQueue queue) {
        super(queue);
    }

    @Override
    protected RequestProcessor<ContentProviderAPI, EmptyServiceRequest> getEmptyRequestProcessor() {
        return new RequestProcessor<ContentProviderAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(ContentProviderAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetAllMetaType:
                        response = new URIPatternMetaTypeListResponse();
                        ((URIPatternMetaTypeListResponse)response).setList(contentProviderService.getAllMetaType());
                        return response;
                    case GetAuthLevelGroupingList:
                        response = new AuthLevelGroupingListResponse();
                        ((AuthLevelGroupingListResponse)response).setList(contentProviderService.getAuthLevelGroupingList());
                        return response;
                    case GetAuthLevelList:
                        response = new AuthLevelListResponse();
                        ((AuthLevelListResponse)response).setList(contentProviderService.getAuthLevelList());
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }

    @Override
    protected RequestProcessor<ContentProviderAPI, BaseSearchServiceRequest> getSearchRequestProcessor() {
        return new RequestProcessor<ContentProviderAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(ContentProviderAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetNumOfContentProviders:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(contentProviderService.getNumOfContentProviders(((BaseSearchServiceRequest<ContentProviderSearchBean>)request).getSearchBean()));
                        return response;
                    case GetNumOfUriPatterns:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(contentProviderService.getNumOfUriPatterns(((BaseSearchServiceRequest<URIPatternSearchBean>)request).getSearchBean()));
                        return response;
                    case FindContentProviderBeans:
                        response = new ContentProviderListResponse();
                        ((ContentProviderListResponse)response).setList(contentProviderService.findBeans(((BaseSearchServiceRequest<ContentProviderSearchBean>)request).getSearchBean(), request.getFrom(), request.getSize()));
                        return response;
                    case FindUriPatterns:
                        response = new URIPatternListResponse();
                        ((URIPatternListResponse)response).setList(contentProviderService.getUriPatternsList(((BaseSearchServiceRequest<URIPatternSearchBean>)request).getSearchBean(), request.getFrom(), request.getSize()));
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }

    @Override
    protected RequestProcessor<ContentProviderAPI, IdServiceRequest> getGetRequestProcessor() {
        return new RequestProcessor<ContentProviderAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(ContentProviderAPI api, IdServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetAuthLevelAttribute:
                        response = new AuthLevelAttributeResponse();
                        ((AuthLevelAttributeResponse)response).setValue(contentProviderService.getAuthLevelAttribute(request.getId()));
                        return response;
                    case GetAuthLevelGrouping:
                        response = new AuthLevelGroupingResponse();
                        ((AuthLevelGroupingResponse)response).setValue(contentProviderService.getAuthLevelGrouping(request.getId()));
                        return response;
                    case GetContentProvider:
                        response = new ContentProviderResponse();
                        ((ContentProviderResponse)response).setValue(contentProviderService.getContentProvider(request.getId()));
                        return response;
                    case GetURIPattern:
                        response = new URIPatternResponse();
                        ((URIPatternResponse)response).setValue(contentProviderService.getURIPattern(request.getId()));
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }

    @Override
    protected RequestProcessor<ContentProviderAPI, BaseCrudServiceRequest> getCrudRequestProcessor() {
        return new RequestProcessor<ContentProviderAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(ContentProviderAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                StringResponse response = new StringResponse();
                switch (api){
                    case SaveAuthLevelAttribute:
                        response.setValue(contentProviderService.saveAuthLevelAttibute(((BaseCrudServiceRequest<AuthLevelAttribute>)request).getObject()));
                        break;
                    case SaveAuthLevelGrouping:
                        response.setValue(contentProviderService.saveAuthLevelGrouping(((BaseCrudServiceRequest<AuthLevelGrouping>)request).getObject()));
                        break;
                    case SaveContentProvider:
                        response.setValue(contentProviderService.saveContentProvider(((BaseCrudServiceRequest<ContentProvider>)request).getObject()));
                        break;
                    case SetupApplication:
                        response.setValue(contentProviderService.setupApplication(((BaseCrudServiceRequest<ContentProvider>)request).getObject()));
                        break;
                    case SaveURIPattern:
                        response.setValue(contentProviderService.saveURIPattern(((BaseCrudServiceRequest<URIPattern>)request).getObject()));
                        break;
                    case CreateDefaultURIPatterns:
                        contentProviderService.createDefaultURIPatterns(request.getObject().getId());
                        return new Response();
                    case DeleteAuthLevelAttribute:
                        contentProviderService.deleteAuthLevelAttribute(request.getObject().getId());
                        return new Response();
                    case DeleteAuthLevelGrouping:
                        contentProviderService.deleteAuthLevelGrouping(request.getObject().getId());
                        return new Response();
                    case DeleteContentProvider:
                        contentProviderService.deleteContentProvider(request.getObject().getId());
                        return new Response();
                    case DeleteProviderPattern:
                        contentProviderService.deleteProviderPattern(request.getObject().getId());
                        return new Response();
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
}
