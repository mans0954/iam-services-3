package org.openiam.am.srvc.mq;


import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.base.request.*;
import org.openiam.base.request.model.CertificateLoginServiceRequest;
import org.openiam.base.response.ContentProviderResponse;
import org.openiam.base.response.URIPatternResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.URIFederationAPI;
import org.openiam.mq.constants.queue.am.URIFederationQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
@RabbitListener(id="uriFederationListener",
        queues = "#{URIFederationQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class URIFederationListener extends AbstractListener<URIFederationAPI>  {

    @Autowired
    private URIFederationService uriFederationService;
    @Autowired
    public URIFederationListener(URIFederationQueue queue) {
        super(queue);
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) URIFederationAPI api, CertificateLoginServiceRequest request)  throws BasicDataServiceException {
        return this.processRequest(api, request, new AbstractListener.RequestProcessor<URIFederationAPI, CertificateLoginServiceRequest>(){
            @Override
            public Response doProcess(URIFederationAPI uriFederationAPI, CertificateLoginServiceRequest request) throws BasicDataServiceException {
                return uriFederationService.getIdentityFromCert(request.getProxyURI(), request.getMethod(), request.getCertContents());
            }
        });
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) URIFederationAPI api, URIFederationServiceRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new AbstractListener.RequestProcessor<URIFederationAPI, URIFederationServiceRequest>(){
            @Override
            public Response doProcess(URIFederationAPI uriFederationAPI, URIFederationServiceRequest request) throws BasicDataServiceException {
                switch (api){
                    case FederateProxyURI:
                        return uriFederationService.federateProxyURI(request.getUserId(), request.getProxyURI(), request.getMethod());
                    case URIFederationMetadata:
                        return uriFederationService.getMetadata(request.getProxyURI(), request.getMethod());
                    case GetCookieFromProxyURIAndPrincipal:
                        return uriFederationService.getCookieFromProxyURIAndPrincipal(request.getProxyURI(), request.getMethod(), request.getPrincipal());
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        });
    }

    @Override
    protected RequestProcessor<URIFederationAPI, IdServiceRequest> getGetRequestProcessor() {
        return new RequestProcessor<URIFederationAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(URIFederationAPI api, IdServiceRequest request) throws BasicDataServiceException {
                switch (api){
                    case CachedContentProviderGet:
                        ContentProviderResponse contentProviderResponse = new ContentProviderResponse();
                        contentProviderResponse.setValue(uriFederationService.getCachedContentProvider(request.getId()));
                        return contentProviderResponse;
                    case CachedURIPatternGet:
                        URIPatternResponse uriPatternResponse = new URIPatternResponse();
                        uriPatternResponse.setValue(uriFederationService.getCachedURIPattern(request.getId()));
                        return uriPatternResponse;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }
}
