package org.openiam.spml2.spi.mysql;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.request.*;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.base.AbstractSpml2Complete;
import org.openiam.connector.ConnectorService;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Implementation object for the MySQL Connector
 * User: suneetshah
 * Date: 3/21/12
 * Time: 10:07 PM
 */
//@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService",
//        targetNamespace = "http://www.openiam.org/service/connector",
//        portName = "MySQLConnectorPort",
//        serviceName = "MySQLConnector")
@Deprecated
public class MySQLConnectorImpl extends AbstractSpml2Complete implements ConnectorService {

    protected MySQLAddCommand addCommand;

    public ObjectResponse add(CrudRequest reqType) {

        return addCommand.add(reqType);

    }

    public ResponseType reconcileResource(@WebParam(name = "config", targetNamespace = "") ReconciliationConfig config) {
        ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.FAILURE);
        response.setError(ErrorCode.UNSUPPORTED_OPERATION);
        return response;
    }

    @Override
    public ResponseType testConnection(@WebParam(name = "reqType", targetNamespace = "") RequestType<? extends ExtensibleObject> reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType testConnection(@WebParam(name = "managedSys", targetNamespace = "") ManagedSysDto managedSys) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ObjectResponse modify(@WebParam(name = "reqType", targetNamespace = "") CrudRequest reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ObjectResponse delete(@WebParam(name = "reqType", targetNamespace = "") CrudRequest reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SearchResponse lookup(@WebParam(name = "reqType", targetNamespace = "") LookupRequest reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest searchRequest) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    /*
* (non-Javadoc)
*
* @see org.openiam.spml2.interf.SpmlCore#lookupAttributeNames(org.openiam.spml2.msg.
* LookupAttributeRequestType)
*/
    public LookupAttributeResponse lookupAttributeNames(LookupRequest reqType){
        LookupAttributeResponse respType = new LookupAttributeResponse();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }

    public ResponseType setPassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType expirePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType resetPassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType validatePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType suspend(@WebParam(name = "request", targetNamespace = "") SuspendResumeRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType resume(@WebParam(name = "request", targetNamespace = "") SuspendResumeRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public MySQLAddCommand getAddCommand() {
        return addCommand;
    }

    public void setAddCommand(MySQLAddCommand addCommand) {
        this.addCommand = addCommand;
    }
}
