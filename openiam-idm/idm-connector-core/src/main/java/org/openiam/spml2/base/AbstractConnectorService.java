package org.openiam.spml2.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.ConnectorCommandFactory;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.constants.ConnectorType;
import org.openiam.spml2.interf.ConnectorService;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.password.*;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import javax.jws.WebParam;

public abstract class AbstractConnectorService implements ConnectorService,ApplicationContextAware {
    protected final Log log = LogFactory.getLog(this.getClass());

    protected ConnectorType connectorType;
    protected ApplicationContext applicationContext;

    @Autowired
    private ConnectorCommandFactory connectorCommandFactory;

    @PostConstruct
    public void init() {
        this.initConnectorType();
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public ResponseType testConnection(@WebParam(name = "requestType", targetNamespace = "") TestRequestType<? extends GenericProvisionObject> requestType) {
        return manageRequest(CommandType.TEST, requestType, ResponseType.class);
    }

    @Override
    public AddResponseType add(@WebParam(name = "reqType", targetNamespace = "") AddRequestType<? extends GenericProvisionObject> reqType) {
        return manageRequest(CommandType.ADD, reqType, AddResponseType.class);
    }

    @Override
    public ModifyResponseType modify(@WebParam(name = "reqType", targetNamespace = "") ModifyRequestType<? extends GenericProvisionObject> reqType) {
        return manageRequest(CommandType.MODIFY, reqType, ModifyResponseType.class);
    }

    @Override
    public ResponseType delete(@WebParam(name = "reqType", targetNamespace = "") DeleteRequestType<? extends GenericProvisionObject> reqType) {
        return manageRequest(CommandType.DELETE, reqType, ResponseType.class);
    }

    @Override
    public LookupResponseType lookup(@WebParam(name = "reqType", targetNamespace = "") LookupRequestType<? extends GenericProvisionObject> reqType) {
        return manageRequest(CommandType.LOOKUP, reqType, LookupResponseType.class);
    }

    @Override
    public LookupAttributeResponseType lookupAttributeNames(@WebParam(name = "reqType", targetNamespace = "") LookupAttributeRequestType<? extends GenericProvisionObject> reqType) {
        return manageRequest(CommandType.LOOKUP_ATTRIBUTE_NAME, reqType, LookupAttributeResponseType.class);
    }

    @Override
    public ResponseType setPassword(@WebParam(name = "request", targetNamespace = "") SetPasswordRequestType request) {
        return manageRequest(CommandType.SET_PASSWORD, request, ResponseType.class);
    }

    @Override
    public ResponseType expirePassword(@WebParam(name = "request", targetNamespace = "") ExpirePasswordRequestType request) {
        return manageRequest(CommandType.EXPIRE_PASSWORD, request, ResponseType.class);
    }

    @Override
    public ResetPasswordResponseType resetPassword(@WebParam(name = "request", targetNamespace = "") ResetPasswordRequestType request) {
        return manageRequest(CommandType.RESET_PASSWORD, request, ResetPasswordResponseType.class);
    }

    @Override
    public ValidatePasswordResponseType validatePassword(@WebParam(name = "request", targetNamespace = "") ValidatePasswordRequestType request) {
        return manageRequest(CommandType.VALIDATE_PASSWORD, request, ValidatePasswordResponseType.class);
    }

    @Override
    public ResponseType suspend(@WebParam(name = "request", targetNamespace = "") SuspendRequestType request) {
        return manageRequest(CommandType.SUSPEND, request, ResponseType.class);
    }

    @Override
    public ResponseType resume(@WebParam(name = "request", targetNamespace = "") ResumeRequestType request) {
        return manageRequest(CommandType.RESUME, request, ResponseType.class);
    }

    private  <Response extends ResponseType> Response manageRequest(CommandType commandType, RequestType requestType, Class<Response> responseClass){
        Response response = null;
        try {
            response = responseClass.newInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if(response!=null){
            log.debug(String.format("%s request proceed in %s connector for %s object", commandType, connectorType, requestType.getProvisionObject().getProvisionObjectType()));
            try {
                ConnectorCommand cmd = connectorCommandFactory.getConnectorCommand(commandType, requestType.getProvisionObject().getProvisionObjectType(), this.connectorType);
                response = (Response)cmd.execute(requestType);

            } catch (ConnectorDataException e) {
                log.error(e.getMessage(), e);
                response.setStatus(StatusCodeType.FAILURE);
                response.setError(e.getCode());
                response.addErrorMessage(e.getMessage());
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                response.setStatus(StatusCodeType.FAILURE);
                response.setError(ErrorCode.CONNECTOR_ERROR);
                response.addErrorMessage(t.getMessage());
            }
        }
        return response;
    }

    protected abstract void initConnectorType();
}
