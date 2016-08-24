package org.openiam.idm.srvc.auth.service.dispatcher;

import org.openiam.base.request.OTPServiceRequest;
import org.openiam.base.response.BooleanResponse;
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
public class OTPDispatcher  extends AbstractAPIDispatcher<OTPServiceRequest, Response, AuthenticationAPI> {
    @Autowired
    private AuthenticationServiceService authenticationServiceService;

    public OTPDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(AuthenticationAPI openIAMAPI, OTPServiceRequest otpServiceRequest) throws BasicDataServiceException {
        Response response = new Response();
        switch (openIAMAPI){
            case ClearOTPActiveStatus:
                authenticationServiceService.clearOTPActiveStatus(otpServiceRequest);
                break;
            case SendOTPToken:
                authenticationServiceService.sendOTPToken(otpServiceRequest);
                break;
            case ConfirmOTPToken:
                authenticationServiceService.confirmOTPToken(otpServiceRequest);
            default:
                break;
        }
        return response;
    }
}
