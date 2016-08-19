package org.openiam.idm.srvc.auth.service.dispatcher;

import org.openiam.base.request.AuthenticationRequest;
import org.openiam.base.response.AuthenticationResponse;
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
public class AuthenticationDispatcher extends AbstractAPIDispatcher<AuthenticationRequest, AuthenticationResponse> {
    @Autowired
    private AuthenticationServiceService authenticationServiceService;

    public AuthenticationDispatcher() {
        super(AuthenticationResponse.class);
    }

    @Override
    protected AuthenticationResponse processingApiRequest(OpenIAMAPI openIAMAPI, AuthenticationRequest authenticationRequest) throws BasicDataServiceException {
        return authenticationServiceService.login(authenticationRequest);
    }
}
