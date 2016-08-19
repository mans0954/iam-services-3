package org.openiam.idm.srvc.auth.service.dispatcher;

import org.openiam.base.request.OTPServiceRequest;
import org.openiam.base.request.RenewTokenRequest;
import org.openiam.base.response.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.service.AuthenticationServiceService;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
public class RenewTokenDispatcher extends AbstractAPIDispatcher<RenewTokenRequest, Response> {
    @Autowired
    private AuthenticationServiceService authenticationServiceService;

    public RenewTokenDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(OpenIAMAPI openIAMAPI, RenewTokenRequest request) throws BasicDataServiceException {
        return authenticationServiceService.renewToken(request.getPrincipal(), request.getToken(), request.getTokenType(), request.getPatternId());
    }
}
