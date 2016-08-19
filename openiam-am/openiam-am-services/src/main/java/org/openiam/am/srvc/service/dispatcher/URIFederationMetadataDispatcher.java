package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.base.request.URIFederationServiceRequest;
import org.openiam.base.response.URIFederationResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class URIFederationMetadataDispatcher extends AbstractAPIDispatcher<URIFederationServiceRequest, URIFederationResponse> {
    @Autowired
    private URIFederationService uriFederationService;

    public URIFederationMetadataDispatcher() {
        super(URIFederationResponse.class);
    }

    @Override
    protected URIFederationResponse processingApiRequest(OpenIAMAPI openIAMAPI, URIFederationServiceRequest uriFederationServiceRequest) throws BasicDataServiceException {
        return uriFederationService.getMetadata(uriFederationServiceRequest.getProxyURI(), uriFederationServiceRequest.getMethod());
    }
}
