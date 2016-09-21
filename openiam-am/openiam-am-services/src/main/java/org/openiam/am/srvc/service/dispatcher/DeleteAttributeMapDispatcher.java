package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.am.srvc.service.AuthResourceAttributeService;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthResourceAttributeAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 20/09/16.
 */
@Component
public class DeleteAttributeMapDispatcher extends AbstractAPIDispatcher<IdServiceRequest, Response, AuthResourceAttributeAPI> {
    @Autowired
    private AuthResourceAttributeService authResourceAttributeService;

    public DeleteAttributeMapDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(AuthResourceAttributeAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        Response response = new Response();
        authResourceAttributeService.removeAttributeMap(request.getId());
        return response;
    }
}
