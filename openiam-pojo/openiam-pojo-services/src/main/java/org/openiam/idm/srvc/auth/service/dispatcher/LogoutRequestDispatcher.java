package org.openiam.idm.srvc.auth.service.dispatcher;

import org.openiam.base.request.LogoutRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.service.AuthenticationServiceService;
import org.openiam.mq.constants.AuthenticationAPI;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
public class LogoutRequestDispatcher extends AbstractAPIDispatcher<LogoutRequest, Response, AuthenticationAPI> {
    @Autowired
    private AuthenticationServiceService authenticationServiceService;

    public LogoutRequestDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(AuthenticationAPI openIAMAPI, LogoutRequest logoutRequest) throws BasicDataServiceException {
        authenticationServiceService.globalLogoutRequest(logoutRequest);
        return new Response();
    }
}
