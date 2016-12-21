package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.LoginListResponse;
import org.openiam.base.response.LoginResponse;
import org.openiam.base.response.data.BooleanResponse;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.user.LoginAPI;
import org.openiam.mq.constants.queue.user.LoginQueue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.openiam.mq.listener.AbstractListener;

import java.util.List;

/**
 * Created by Alexander Dukkardt on 2016-12-20.
 */
@Component
@RabbitListener(id = "LoginQueueListener",
        queues = "#{LoginQueue.name}",
        containerFactory = "userRabbitListenerContainerFactory")
public class LoginQueueListener extends AbstractListener<LoginAPI> {
    @Autowired
    private LoginDataService loginDS;

    @Autowired
    public LoginQueueListener(LoginQueue queue) {
        super(queue);
    }

    @Override
    protected RequestProcessor<LoginAPI, EmptyServiceRequest> getEmptyRequestProcessor() {
        return new RequestProcessor<LoginAPI, EmptyServiceRequest>() {
            @Override
            public Response doProcess(LoginAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                IntResponse response=new IntResponse();
                response.setValue(loginDS.bulkResetPasswordChangeCount());
                return response;
            }
        };
    }

    @Override
    protected RequestProcessor<LoginAPI, BaseSearchServiceRequest> getSearchRequestProcessor() {
        return new RequestProcessor<LoginAPI, BaseSearchServiceRequest>() {
            @Override
            public Response doProcess(LoginAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                LoginSearchBean searchBean = (LoginSearchBean)request.getSearchBean();
                Response response;
                switch (api){
                    case LoginExists:
                        response=new BooleanResponse();
                        ((BooleanResponse)response).setValue(loginDS.loginExists(searchBean.getLoginMatchToken().getValue(), searchBean.getManagedSysId()));
                        break;
                    case GetPrimaryIdentity:
                        response=new LoginResponse();
                        ((LoginResponse)response).setPrincipal(loginDS.getPrimaryIdentityDto(searchBean.getUserId()));
                        break;
                    case FindBeans:
                        response=new LoginListResponse();
                        ((LoginListResponse)response).setPrincipalList(loginDS.findBeans(searchBean,request.getFrom(), request.getSize()));
                        break;
                    case Count:
                        response=new IntResponse();
                        ((IntResponse)response).setValue(loginDS.count(searchBean));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @Override
    protected RequestProcessor<LoginAPI, IdServiceRequest> getGetRequestProcessor() {
        return new RequestProcessor<LoginAPI, IdServiceRequest>() {
            @Override
            public Response doProcess(LoginAPI api, IdServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case FindById:
                        response=new LoginResponse();
                        ((LoginResponse)response).setPrincipal(loginDS.getLoginDTO(request.getId()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @Override
    protected RequestProcessor<LoginAPI, BaseCrudServiceRequest> getCrudRequestProcessor() {
        return new RequestProcessor<LoginAPI, BaseCrudServiceRequest>() {
            @Override
            public Response doProcess(LoginAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Login dto = (Login)request.getObject();
                Response response;
                switch (api){
                    case Validate:
                        response=new Response();
                        loginDS.validateLogin(dto);
                        break;
                    case Save:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(loginDS.saveLogin(dto));
                        break;
                    case LockLogin:
                        response=new Response();
                        loginDS.lockLogin(dto.getLogin(), dto.getManagedSysId());
                        break;
                    case ActivateLogin:
                        response=new Response();
                        loginDS.activateDeactivateLogin(dto.getId(), LoginStatusEnum.ACTIVE);
                        break;
                    case DeActivateLogin:
                        response=new Response();
                        loginDS.activateDeactivateLogin(dto.getId(), LoginStatusEnum.INACTIVE);
                        break;
                    case RemoveLogin:
                        response=new Response();
                        loginDS.removeLogin(dto.getLogin(), dto.getManagedSysId());
                        break;
                    case IsPasswordEq:
                        response=new Response();
                        loginDS.isPasswordEq(dto.getLogin(), dto.getManagedSysId(), dto.getPassword());
                        break;
                    case DeleteLogin:
                        response=new Response();
                        loginDS.deleteLogin(dto.getId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) LoginAPI api, DataEncryptionRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<LoginAPI, DataEncryptionRequest>() {
            @Override
            public Response doProcess(LoginAPI api, DataEncryptionRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case DecryptPassword:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(loginDS.decryptPassword(request.getUserId(), request.getData()));
                        break;
                    case EncryptPassword:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(loginDS.encryptPassword(request.getUserId(), request.getData()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) LoginAPI api, UnlockRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<LoginAPI, UnlockRequest>() {
            @Override
            public Response doProcess(LoginAPI api, UnlockRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case BulkUnLock:
                        response=new Response();
                        loginDS.bulkUnLock(request.getStatus());
                        break;
                    case UnLockLogin:
                        response=new Response();
                        loginDS.unLockLogin(request.getPrincipal(), request.getManagedSysId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) LoginAPI api, ResetPasswordRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<LoginAPI, ResetPasswordRequest>() {
            @Override
            public Response doProcess(LoginAPI api, ResetPasswordRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case ResetPassword:
                        response=new Response();
                        loginDS.resetPasswordAndNotifyUser(request.getPrincipal(), request.getManagedSysId(), request.getContentProviderId(), request.getPassword(), request.isNotifyUserViaEmail());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) LoginAPI api, PrincipalRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<LoginAPI, PrincipalRequest>() {
            @Override
            public Response doProcess(LoginAPI api, PrincipalRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetLockedUserSince:
                        response=new LoginListResponse();
                        ((LoginListResponse)response).setPrincipalList(loginDS.getLockedUserSince(request.getLastExecTime()));
                        break;
                    case GetInactiveUsers:
                        response=new LoginListResponse();
                        ((LoginListResponse)response).setPrincipalList(loginDS.getInactiveUsers(request.getStartDays(), request.getEndDays()));
                        break;
                    case GetUserNearPswdExpiration:
                        response=new LoginListResponse();
                        List<Login> lgList = null;
                        if(request.getStartDays()!=null) {
                            lgList = loginDS.getUserNearPswdExpiration(request.getStartDays());
                        } else {
                            lgList = loginDS.getUsersNearPswdExpiration();
                        }
                        ((LoginListResponse)response).setPrincipalList(lgList);
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) LoginAPI api, StringDataRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<LoginAPI, StringDataRequest>() {
            @Override
            public Response doProcess(LoginAPI api, StringDataRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case ForgotUsername:
                        response=new Response();
                        loginDS.forgotUsername(request.getData());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
