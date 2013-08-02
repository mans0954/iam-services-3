package org.openiam.spml2.spi.csv;

import java.util.HashMap;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.*;
import org.openiam.connector.type.ResponseType;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.command.ReconciliationCommandFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.spml2.base.AbstractSpml2Complete;
import org.openiam.connector.ConnectorService;
import org.openiam.spml2.spi.common.LookupAttributeNamesCommand;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@WebService(endpointInterface = "org.openiam.connector.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector", portName = "CSVConnectorServicePort", serviceName = "CSVConnectorService")
public class CSVConnectorImpl extends AbstractSpml2Complete implements
        ConnectorService, ApplicationContextAware {
    private static final Log log = LogFactory.getLog(CSVConnectorImpl.class);
    @Autowired
    private AddCSVCommand addCommand;
    @Autowired
    private TestCSVCommand testCommand;
    @Autowired
    private LookupCSVCommand lookupCommand;
    @Autowired
    private ModifyCSVCommand modifyCommand;
    @Autowired
    private ReconcileCSVCommand reconCommand;
    @Autowired
    @Qualifier("lookupCSVAttributeNamesCommand")
    private LookupAttributeNamesCommand lookupAttributeNamesCommand;

    @Autowired
    private ResourceDataService resourceDataService;
    @Autowired
    private ManagedSysDozerConverter managedSysDozerConverter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        // TODO Auto-generated method stub

    }

    @Override
    @WebMethod
    public ResponseType reconcileResource(
            @WebParam(name = "config", targetNamespace = "") ReconciliationConfig config) {
        log.debug("reconcile resource called in CSVConnector");

        Resource res = resourceDataService.getResource(config.getResourceId());
        String managedSysId = res.getManagedSysId();

        Map<String, ReconciliationCommand> situations = new HashMap<String, ReconciliationCommand>();
        for (ReconciliationSituation situation : config.getSituationSet()) {
            situations.put(situation.getSituation().trim(),
                    ReconciliationCommandFactory.createCommand(
                            situation.getSituationResp(), situation,
                            managedSysId));
            log.debug("Created Command for: " + situation.getSituation());
        }
        ResponseType response = reconCommand.reconcile(config);

        return response; // To change body of implemented methods use File |
                         // Settings | File Templates.
    }

    @Override
    @WebMethod
    public ResponseType testConnection(
            @WebParam(name = "managedSys", targetNamespace = "") ManagedSysDto managedSys) {
        return testCommand.test(managedSysDozerConverter.convertToEntity(
                managedSys, true));
    }

    public void setTestCommand(TestCSVCommand testCommand) {
        this.testCommand = testCommand;
    }

    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/add")
    public UserResponse add(
            @WebParam(name = "reqType", targetNamespace = "") UserRequest reqType) {
        return addCommand.add(reqType);
    }

    public void setLookupCommand(LookupCSVCommand lookupCommand) {
        this.lookupCommand = lookupCommand;
    }

    public void setModifyCommand(ModifyCSVCommand modifyCommand) {
        this.modifyCommand = modifyCommand;
    }

    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/modify")
    public UserResponse modify(
            @WebParam(name = "reqType", targetNamespace = "") UserRequest reqType) {
        return modifyCommand.modify(reqType);
    }

    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/delete")
    public UserResponse delete(
            @WebParam(name = "reqType", targetNamespace = "") UserRequest reqType) {
        return modifyCommand.delete(reqType);
    }

    public void setAddCommand(AddCSVCommand addCommand) {
        this.addCommand = addCommand;
    }

    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/lookup")
    public SearchResponse lookup(
            @WebParam(name = "reqType", targetNamespace = "") LookupRequest reqType) {
        return lookupCommand.lookup(reqType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.spml2.interf.SpmlCore#lookupAttributeNames(org.openiam.spml2
     * .msg. LookupAttributeRequestType)
     */
    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/lookupAttributeNames")
    public LookupAttributeResponse lookupAttributeNames(
            LookupRequest reqType) {
        return lookupAttributeNamesCommand.lookupAttributeNames(reqType);
    }

    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/setPassword")
    public ResponseType setPassword(
            @WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/expirePassword")
    public ResponseType expirePassword(
            @WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/resetPassword")
    public ResponseType resetPassword(
            @WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest searchRequest) {
        throw new UnsupportedOperationException("Not supportable.");
    }

    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/validatePassword")
    public ResponseType validatePassword(
            @WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/suspend")
    public ResponseType suspend(
            @WebParam(name = "request", targetNamespace = "") SuspendRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/resume")
    public ResponseType resume(
            @WebParam(name = "request", targetNamespace = "") ResumeRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

}
