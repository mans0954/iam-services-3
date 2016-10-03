package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMCacheAPI;
import org.openiam.mq.constants.AMManagerAPI;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 14/09/16.
 */
@Component
public class SweepManagerDispatcher extends AbstractAPIDispatcher<BaseServiceRequest, Response, AMCacheAPI> {

    @Autowired
    private AuthorizationManagerService authorizationManagerService;

    public SweepManagerDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(AMCacheAPI openIAMAPI, BaseServiceRequest request) throws BasicDataServiceException {
        ((Sweepable)authorizationManagerService).sweep();
        return new Response();
    }
}
