package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.base.request.URIFederationServiceRequest;
import org.openiam.base.response.URIFederationResponse;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.constants.URIFederationAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class URIFederationMetadataDispatcher extends AbstractAPIDispatcher<URIFederationServiceRequest, URIFederationResponse, URIFederationAPI> {
    @Autowired
    private URIFederationService uriFederationService;

    public URIFederationMetadataDispatcher() {
        super(URIFederationResponse.class);
    }

    @Override
    protected URIFederationResponse processingApiRequest(URIFederationAPI openIAMAPI, URIFederationServiceRequest request) throws BasicDataServiceException {

        switch (openIAMAPI){
            case FederateProxyURI:
                return uriFederationService.federateProxyURI(request.getUserId(), request.getProxyURI(), request.getMethod());
            case URIFederationMetadata:
                return uriFederationService.getMetadata(request.getProxyURI(), request.getMethod());
            default:
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + openIAMAPI.name());
        }
    }
}
