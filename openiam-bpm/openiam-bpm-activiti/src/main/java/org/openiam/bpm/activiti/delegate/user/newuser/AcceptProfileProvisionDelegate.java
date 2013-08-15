package org.openiam.bpm.activiti.delegate.user.newuser;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.request.RequestorInformation;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.domain.RequestApproverEntity;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestUser;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class AcceptProfileProvisionDelegate implements JavaDelegate {

	private static Logger log = Logger.getLogger(AcceptProfileProvisionDelegate.class);
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	public AcceptProfileProvisionDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	public static final String APPROVE_STATUS = "APPROVED";
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String comment = (String)execution.getVariable(ActivitiConstants.COMMENT);
		final String lastCaller = (String)execution.getVariable(ActivitiConstants.EXECUTOR_ID);
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final Date currentDate = new Date();
		provisionRequest.setStatusDate(currentDate);
		provisionRequest.setStatus(APPROVE_STATUS);

		final NewUserProfileRequestModel profileModel = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		profileModel.getUser().setUserId(null);
		profileModel.getUser().setStatus(UserStatusEnum.PENDING_INITIAL_LOGIN);
		provisionRequest.setRequestXML(new XStream().toXML(profileModel));
		provRequestService.updateRequest(provisionRequest);
	}

}
