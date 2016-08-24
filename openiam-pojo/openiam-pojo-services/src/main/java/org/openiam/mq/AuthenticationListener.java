package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.idm.srvc.auth.service.dispatcher.*;
import org.openiam.mq.constants.AuthenticationAPI;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
public class AuthenticationListener extends AbstractRabbitMQListener<AuthenticationAPI> {
    @Autowired
    private LogoutRequestDispatcher logoutRequestDispatcher;
    @Autowired
    private AuthenticationDispatcher authenticationDispatcher;
    @Autowired
    private OTPDispatcher otpDispatcher;
    @Autowired
    private CheckOTPDispatcher checkOTPDispatcher;
    @Autowired
    private GetOTPTokenDispatcher getOTPTokenDispatcher;
    @Autowired
    private RenewTokenDispatcher renewTokenDispatcher;
    @Autowired
    private FindAuthStateDispatcher findAuthStateDispatcher;
    @Autowired
    private SaveAuthStateDispatcher saveAuthStateDispatcher;

    public AuthenticationListener() {
        super(OpenIAMQueue.AuthenticationQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, AuthenticationAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        AuthenticationAPI apiName = message.getRequestApi();
        switch (apiName){
            case GlobalLogoutRequest:
                addTask(logoutRequestDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Authenticate:
                addTask(authenticationDispatcher, correlationId, message, apiName, isAsync);
                break;
            case ClearOTPActiveStatus:
            case SendOTPToken:
            case ConfirmOTPToken:
                addTask(otpDispatcher, correlationId, message, apiName, isAsync);
                break;
            case IsOTPActive:
                addTask(checkOTPDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetOTPSecretKey:
                addTask(getOTPTokenDispatcher, correlationId, message, apiName, isAsync);
                break;
            case RenewToken:
                addTask(renewTokenDispatcher, correlationId, message, apiName, isAsync);
                break;
            case FindAuthState:
                addTask(findAuthStateDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveAuthState:
                addTask(saveAuthStateDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
