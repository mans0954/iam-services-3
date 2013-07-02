package org.openiam.spml2.spi.mysql;

import org.openiam.connector.type.SearchRequest;
import org.openiam.connector.type.SearchResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.spml2.base.AbstractSpml2Complete;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.password.*;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.msg.suspend.SuspendRequestType;

import javax.jws.WebParam;

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
public class MySQLConnectorImpl extends AbstractSpml2Complete  {

    protected MySQLAddCommand addCommand;

    public AddResponseType add(AddRequestType reqType) {

        return addCommand.add(reqType);

    }

    public ResponseType reconcileResource(@WebParam(name = "config", targetNamespace = "") ReconciliationConfig config) {
        ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.FAILURE);
        response.setError(ErrorCode.UNSUPPORTED_OPERATION);
        return response;
    }

    public ResponseType testConnection(@WebParam(name = "managedSys", targetNamespace = "") ManagedSysDto managedSys) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    public ModifyResponseType modify(@WebParam(name = "reqType", targetNamespace = "") ModifyRequestType reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType delete(@WebParam(name = "reqType", targetNamespace = "") DeleteRequestType reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public LookupResponseType lookup(@WebParam(name = "reqType", targetNamespace = "") LookupRequestType reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

//    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest searchRequest) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    /*
* (non-Javadoc)
*
* @see org.openiam.spml2.interf.SpmlCore#lookupAttributeNames(org.openiam.spml2.msg.
* LookupAttributeRequestType)
*/
    public LookupAttributeResponseType lookupAttributeNames(LookupAttributeRequestType reqType){
        LookupAttributeResponseType respType = new LookupAttributeResponseType();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }

    public ResponseType setPassword(@WebParam(name = "request", targetNamespace = "") SetPasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType expirePassword(@WebParam(name = "request", targetNamespace = "") ExpirePasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResetPasswordResponseType resetPassword(@WebParam(name = "request", targetNamespace = "") ResetPasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ValidatePasswordResponseType validatePassword(@WebParam(name = "request", targetNamespace = "") ValidatePasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType suspend(@WebParam(name = "request", targetNamespace = "") SuspendRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType resume(@WebParam(name = "request", targetNamespace = "") ResumeRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public MySQLAddCommand getAddCommand() {
        return addCommand;
    }

    public void setAddCommand(MySQLAddCommand addCommand) {
        this.addCommand = addCommand;
    }
}
