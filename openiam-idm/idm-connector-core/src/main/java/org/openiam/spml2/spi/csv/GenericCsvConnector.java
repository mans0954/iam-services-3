package org.openiam.spml2.spi.csv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.base.AbstractConnectorService;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.constants.ConnectorType;
import org.openiam.spml2.interf.ConnectorService;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.password.*;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;

@Service("genericCsvConnector")
@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector", portName = "CSVConnectorServicePort", serviceName = "CSVConnectorService")
public class GenericCsvConnector extends AbstractConnectorService {

    protected void initConnectorType(){
        this.connectorType= ConnectorType.CSV;
    }

    @Override
    public ResponseType reconcileResource(@WebParam(name = "config", targetNamespace = "") ReconciliationConfig config) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResponseType testConnection(@WebParam(name = "managedSys", targetNamespace = "") ManagedSysDto managedSys) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected  <Response extends ResponseType> Response manageRequest(CommandType commandType, RequestType requestType, Class<Response> responseClass){
        try {
            return responseClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    @Override
    public AddResponseType add(@WebParam(name = "reqType", targetNamespace = "") AddRequestType<? extends GenericProvisionObject> reqType) {
        return manageRequest(CommandType.ADD, reqType, AddResponseType.class);
    }

    @Override
    public ModifyResponseType modify(@WebParam(name = "reqType", targetNamespace = "") ModifyRequestType<? extends GenericProvisionObject> reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResponseType delete(@WebParam(name = "reqType", targetNamespace = "") DeleteRequestType<? extends GenericProvisionObject> reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LookupResponseType lookup(@WebParam(name = "reqType", targetNamespace = "") LookupRequestType<? extends GenericProvisionObject> reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LookupAttributeResponseType lookupAttributeNames(@WebParam(name = "reqType", targetNamespace = "") LookupAttributeRequestType<? extends GenericProvisionObject> reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResponseType setPassword(@WebParam(name = "request", targetNamespace = "") SetPasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResponseType expirePassword(@WebParam(name = "request", targetNamespace = "") ExpirePasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResetPasswordResponseType resetPassword(@WebParam(name = "request", targetNamespace = "") ResetPasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ValidatePasswordResponseType validatePassword(@WebParam(name = "request", targetNamespace = "") ValidatePasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResponseType suspend(@WebParam(name = "request", targetNamespace = "") SuspendRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResponseType resume(@WebParam(name = "request", targetNamespace = "") ResumeRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
