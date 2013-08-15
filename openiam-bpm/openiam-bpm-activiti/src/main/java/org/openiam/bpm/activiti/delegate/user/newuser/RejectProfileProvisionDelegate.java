package org.openiam.bpm.activiti.delegate.user.newuser;

import java.util.Date;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.domain.RequestApproverEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RejectProfileProvisionDelegate implements JavaDelegate {
	
	private static Logger log = Logger.getLogger(RejectProfileProvisionDelegate.class);
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	private static final String REJECTED_STATUS = "REJECTED";
	
	public RejectProfileProvisionDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Date currentDate = new Date();
		
		final String comment = (String)execution.getVariable(ActivitiConstants.COMMENT);
		final String lastCaller = (String)execution.getVariable(ActivitiConstants.EXECUTOR_ID);
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		provisionRequest.setStatusDate(currentDate);
		provisionRequest.setStatus(REJECTED_STATUS);
        provRequestService.updateRequest(provisionRequest);
	}
}
