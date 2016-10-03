package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.URIFederationAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 03/10/16.
 */
@Component
public class RefreshUriFederationCacheDispatcher extends AbstractAPIDispatcher<BaseServiceRequest, Response, URIFederationAPI> {
    @Autowired
    private URIFederationService uriFederationService;

    public RefreshUriFederationCacheDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(URIFederationAPI openIAMAPI, BaseServiceRequest request) throws BasicDataServiceException {
        uriFederationService.sweep();
        return new Response();
    }
}
