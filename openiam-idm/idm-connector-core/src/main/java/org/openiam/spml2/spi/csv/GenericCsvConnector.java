package org.openiam.spml2.spi.csv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.ConnectorCommandFactory;
import org.openiam.spml2.base.AbstractConnectorService;
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
        // TODO: need to be refactored
        //        log.debug("reconcile resource called in CSVConnector");
//
//        Resource res = resourceDataService.getResource(config.getResourceId());
//        String managedSysId = res.getManagedSysId();
//
//        Map<String, ReconciliationCommand> situations = new HashMap<String, ReconciliationCommand>();
//        for (ReconciliationSituation situation : config.getSituationSet()) {
//            situations.put(situation.getSituation().trim(),
//                    ReconciliationCommandFactory.createCommand(
//                            situation.getSituationResp(), situation,
//                            managedSysId));
//            log.debug("Created Command for: " + situation.getSituation());
//        }
//        ResponseType response = reconCommand.reconcile(config);
//
//        return response; // To change body of implemented methods use File |
//                         // Settings | File Templates.
        return null;
    }

    @Override
    public ResponseType testConnection(@WebParam(name = "managedSys", targetNamespace = "") ManagedSysDto managedSys) {
        //TODO: need to be refactored
//        return testCommand.test(managedSysDozerConverter.convertToEntity(
//                managedSys, true));
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
}
