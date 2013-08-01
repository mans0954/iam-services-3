package org.openiam.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.*;
import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.ConnectorCommandFactory;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.constants.ConnectorType;
import org.openiam.connector.common.command.ConnectorCommand;
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
    public ResponseType testConnection(@WebParam(name = "requestType", targetNamespace = "") RequestType<? extends ExtensibleObject> requestType) {
        return manageRequest(CommandType.TEST, requestType, ResponseType.class);
    }

    @Override
    public ObjectResponse add(@WebParam(name = "reqType", targetNamespace = "") CrudRequest<? extends ExtensibleObject> reqType) {
        return manageRequest(CommandType.ADD, reqType, ObjectResponse.class);
    }

    @Override
    public ObjectResponse modify(@WebParam(name = "reqType", targetNamespace = "") CrudRequest<? extends ExtensibleObject> reqType) {
        return manageRequest(CommandType.MODIFY, reqType, ObjectResponse.class);
    }

    @Override
    public ObjectResponse delete(@WebParam(name = "reqType", targetNamespace = "") CrudRequest<? extends ExtensibleObject> reqType) {
        return manageRequest(CommandType.DELETE, reqType, ObjectResponse.class);
    }

    @Override
    public SearchResponse lookup(@WebParam(name = "reqType", targetNamespace = "") LookupRequest<? extends ExtensibleObject> reqType) {
        return manageRequest(CommandType.LOOKUP, reqType, SearchResponse.class);
    }

    @Override
    public LookupAttributeResponse lookupAttributeNames(@WebParam(name = "reqType", targetNamespace = "") LookupRequest<? extends ExtensibleObject> reqType) {
        return manageRequest(CommandType.LOOKUP_ATTRIBUTE_NAME, reqType, LookupAttributeResponse.class);
    }

    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest<? extends ExtensibleObject> searchRequest){
        return manageRequest(CommandType.SEARCH, searchRequest, SearchResponse.class);
    }

    @Override
    public ResponseType setPassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return manageRequest(CommandType.SET_PASSWORD, request, ResponseType.class);
    }

    @Override
    public ResponseType expirePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return manageRequest(CommandType.EXPIRE_PASSWORD, request, ResponseType.class);
    }

    @Override
    public ResponseType resetPassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return manageRequest(CommandType.RESET_PASSWORD, request, ResponseType.class);
    }

    @Override
    public ResponseType validatePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return manageRequest(CommandType.VALIDATE_PASSWORD, request, ResponseType.class);
    }

    @Override
    public ResponseType suspend(@WebParam(name = "request", targetNamespace = "") SuspendResumeRequest request) {
        return manageRequest(CommandType.SUSPEND, request, ResponseType.class);
    }

    @Override
    public ResponseType resume(@WebParam(name = "request", targetNamespace = "") SuspendResumeRequest request) {
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
            log.debug(String.format("%s request proceed in %s connector for %s object", commandType, connectorType, requestType.getExtensibleObject().getExtensibleObjectType()));
            try {
                ConnectorCommand cmd = connectorCommandFactory.getConnectorCommand(commandType, requestType.getExtensibleObject().getExtensibleObjectType(), this.connectorType);
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
