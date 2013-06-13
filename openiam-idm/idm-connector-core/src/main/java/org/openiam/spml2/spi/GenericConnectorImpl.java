package org.openiam.spml2.spi;

import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.spml2.base.AbstractSpml2Complete;
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

@Service("genericConnector")
@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
            portName = "CSVConnectorServicePort", serviceName = "GenericConnectorService")
public class GenericConnectorImpl extends AbstractSpml2Complete implements  ConnectorService, ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext = applicationContext;
    }

    @Override
    public ResponseType reconcileResource(@WebParam(name = "config", targetNamespace = "") ReconciliationConfig config) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResponseType testConnection(@WebParam(name = "managedSys", targetNamespace = "") ManagedSysDto managedSys) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AddResponseType add(@WebParam(name = "reqType", targetNamespace = "") AddRequestType reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ModifyResponseType modify(@WebParam(name = "reqType", targetNamespace = "") ModifyRequestType reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResponseType delete(@WebParam(name = "reqType", targetNamespace = "") DeleteRequestType reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LookupResponseType lookup(@WebParam(name = "reqType", targetNamespace = "") LookupRequestType reqType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LookupAttributeResponseType lookupAttributeNames(@WebParam(name = "reqType", targetNamespace = "") LookupAttributeRequestType reqType) {
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
