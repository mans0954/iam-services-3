package org.openiam.spml2.spi.csv;

import java.util.HashMap;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.command.ReconciliationCommandFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.spml2.base.AbstractSpml2Complete;
import org.openiam.spml2.interf.ConnectorService;
import org.openiam.spml2.msg.AddRequestType;
import org.openiam.spml2.msg.AddResponseType;
import org.openiam.spml2.msg.DeleteRequestType;
import org.openiam.spml2.msg.LookupRequestType;
import org.openiam.spml2.msg.LookupResponseType;
import org.openiam.spml2.msg.ModifyRequestType;
import org.openiam.spml2.msg.ModifyResponseType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.password.ExpirePasswordRequestType;
import org.openiam.spml2.msg.password.ResetPasswordRequestType;
import org.openiam.spml2.msg.password.ResetPasswordResponseType;
import org.openiam.spml2.msg.password.SetPasswordRequestType;
import org.openiam.spml2.msg.password.ValidatePasswordRequestType;
import org.openiam.spml2.msg.password.ValidatePasswordResponseType;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector", portName = "CSVConnectorServicePort", serviceName = "CSVConnectorService")
public class CSVConnectorImpl extends AbstractSpml2Complete implements
		ConnectorService, ApplicationContextAware {
	private static final Log log = LogFactory.getLog(CSVConnectorImpl.class);
	@Autowired
	private AddCSVCommand addCommand;
	// @Autowired
	private TestCSVCommand testCommand;
	// @Autowired
	private LookupCSVCommand lookupCommand;
	// @Autowired
	private ModifyCSVCommand modifyCommand;
	// @Autowired
	private ReconcileCSVCommand reconCommand;
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
	public AddResponseType add(
			@WebParam(name = "reqType", targetNamespace = "") AddRequestType reqType) {
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
	public ModifyResponseType modify(
			@WebParam(name = "reqType", targetNamespace = "") ModifyRequestType reqType) {
		return modifyCommand.modify(reqType);
	}

	@Override
	@WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/delete")
	public ResponseType delete(
			@WebParam(name = "reqType", targetNamespace = "") DeleteRequestType reqType) {
		return modifyCommand.delete(reqType);
	}

	public void setAddCommand(AddCSVCommand addCommand) {
		this.addCommand = addCommand;
	}

	@Override
	@WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/lookup")
	public LookupResponseType lookup(
			@WebParam(name = "reqType", targetNamespace = "") LookupRequestType reqType) {
		return lookupCommand.lookup(reqType);
	}

	@Override
	@WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/setPassword")
	public ResponseType setPassword(
			@WebParam(name = "request", targetNamespace = "") SetPasswordRequestType request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/expirePassword")
	public ResponseType expirePassword(
			@WebParam(name = "request", targetNamespace = "") ExpirePasswordRequestType request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/resetPassword")
	public ResetPasswordResponseType resetPassword(
			@WebParam(name = "request", targetNamespace = "") ResetPasswordRequestType request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/validatePassword")
	public ValidatePasswordResponseType validatePassword(
			@WebParam(name = "request", targetNamespace = "") ValidatePasswordRequestType request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/suspend")
	public ResponseType suspend(
			@WebParam(name = "request", targetNamespace = "") SuspendRequestType request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/resume")
	public ResponseType resume(
			@WebParam(name = "request", targetNamespace = "") ResumeRequestType request) {
		// TODO Auto-generated method stub
		return null;
	}

}
