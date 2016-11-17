package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationServiceService;
import org.openiam.mq.constants.api.AuthenticationAPI;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.queue.am.AuthenticationQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
@RabbitListener(id="authenticationListener",
        queues = "#{AuthenticationQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class AuthenticationListener extends AbstractListener<AuthenticationAPI> {
    @Autowired
    private AuthenticationServiceService authenticationServiceService;

    @Autowired
    public AuthenticationListener(AuthenticationQueue queue) {
        super(queue);
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AuthenticationAPI api, LogoutRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AuthenticationAPI, LogoutRequest>(){
            @Override
            public Response doProcess(AuthenticationAPI api, LogoutRequest request) throws BasicDataServiceException {
                authenticationServiceService.globalLogoutRequest(request);
                return new Response();
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AuthenticationAPI api, AuthenticationRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AuthenticationAPI, AuthenticationRequest>(){
            @Override
            public Response doProcess(AuthenticationAPI api, AuthenticationRequest request) throws BasicDataServiceException {
                AuthenticationResponse response = new AuthenticationResponse();
                Subject authSubject =  authenticationServiceService.login(request);
                response.setSubject(authSubject);
                return response;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AuthenticationAPI api, OTPServiceRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AuthenticationAPI, OTPServiceRequest>(){
            @Override
            public Response doProcess(AuthenticationAPI api, OTPServiceRequest request) throws BasicDataServiceException {
                Response response = new Response();
                switch (api){
                    case ClearOTPActiveStatus:
                        authenticationServiceService.clearOTPActiveStatus(request);
                        break;
                    case SendOTPToken:
                        authenticationServiceService.sendOTPToken(request);
                        break;
                    case ConfirmOTPToken:
                        authenticationServiceService.confirmOTPToken(request);
                        break;
                    case IsOTPActive:
                        response = new BooleanResponse();
                        ((BooleanResponse)response).setValue(authenticationServiceService.isOTPActive(request));
                        break;
                    case GetOTPSecretKey:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(authenticationServiceService.getOTPSecretKey(request));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AuthenticationAPI api, RenewTokenRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AuthenticationAPI, RenewTokenRequest>(){
            @Override
            public Response doProcess(AuthenticationAPI api, RenewTokenRequest request) throws BasicDataServiceException {
                final SSOTokenResponse resp = new SSOTokenResponse();
                resp.setSsoToken(authenticationServiceService.renewToken(request.getPrincipal(), request.getToken(), request.getTokenType(), request.getPatternId()));
                return resp;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AuthenticationAPI api, AuthStateCrudServiceRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AuthenticationAPI, AuthStateCrudServiceRequest>(){
            @Override
            public Response doProcess(AuthenticationAPI api, AuthStateCrudServiceRequest request) throws BasicDataServiceException {
                authenticationServiceService.save(request.getAuthStateEntity());
                return new Response();
            }
        });
    }

    protected RequestProcessor<AuthenticationAPI, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return new RequestProcessor<AuthenticationAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(AuthenticationAPI authenticationAPI, BaseSearchServiceRequest request) throws BasicDataServiceException {
                AuthStateListResponse resp = new AuthStateListResponse();
                resp.setAuthStateList(authenticationServiceService.findBeans(((BaseSearchServiceRequest<AuthStateSearchBean>)request).getSearchBean(), request.getFrom(), request.getSize()));
                return resp;
            }
        };
    }
}
