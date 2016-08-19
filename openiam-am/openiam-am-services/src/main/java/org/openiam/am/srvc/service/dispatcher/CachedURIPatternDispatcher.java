package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.URIPatternResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class CachedURIPatternDispatcher extends AbstractAPIDispatcher<IdServiceRequest, URIPatternResponse> {
    @Autowired
    private URIFederationService uriFederationService;

    public CachedURIPatternDispatcher() {
        super(URIPatternResponse.class);
    }

    @Override
    protected URIPatternResponse processingApiRequest(OpenIAMAPI openIAMAPI, IdServiceRequest idServiceRequest) throws BasicDataServiceException {
        URIPatternResponse uriPatternResponse = new URIPatternResponse();
        uriPatternResponse.setUriPattern(uriFederationService.getCachedURIPattern(idServiceRequest.getId()));
        return uriPatternResponse;
    }
}
