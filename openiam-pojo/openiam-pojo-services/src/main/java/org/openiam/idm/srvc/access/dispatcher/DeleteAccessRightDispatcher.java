package org.openiam.idm.srvc.access.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.access.service.AccessRightService;
import org.openiam.mq.constants.AccessRightAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 08/09/16.
 */
@Component
public class DeleteAccessRightDispatcher extends AbstractAPIDispatcher<IdServiceRequest, Response, AccessRightAPI> {
    @Autowired
    private AccessRightService accessRightService;

    public DeleteAccessRightDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(AccessRightAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        Response response = new Response();
        accessRightService.delete(request.getId());
        return response;
    }
}
