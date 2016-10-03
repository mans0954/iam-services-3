package org.openiam.authmanager.service.dispatcher;

import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.MenuEntitlementsRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AMCacheAPI;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 14/09/16.
 */
@Component
public class SweepMenuDispatcher extends AbstractAPIDispatcher<BaseServiceRequest, Response, AMCacheAPI> {

    @Autowired
    private AuthorizationManagerMenuService menuService;

    public SweepMenuDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(AMCacheAPI openIAMAPI, BaseServiceRequest request) throws BasicDataServiceException {
        menuService.sweep();
        return new Response();
    }
}
