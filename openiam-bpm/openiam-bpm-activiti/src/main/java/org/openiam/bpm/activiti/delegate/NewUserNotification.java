package org.openiam.bpm.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.log4j.Logger;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class NewUserNotification implements JavaDelegate {

	private static Logger log = Logger.getLogger(NewUserNotification.class);
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private LoginDataService loginService;
	
	@Autowired
	private KeyManagementService keyManagementService;
	
	@Autowired
	private PasswordService passwordService;
	
	public NewUserNotification() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String userId  = (String)execution.getVariable(ActivitiConstants.NEW_USER_ID);
		final LoginEntity loginEntity = loginService.getPrimaryIdentity(userId);
		final String password = loginService.decryptPassword(userId, loginEntity.getPassword());
		
		final NotificationRequest notificationRequest = new NotificationRequest();
		notificationRequest.setUserId(userId);
		notificationRequest.setNotificationType("NEW_USER_EMAIL");
		notificationRequest.getParamList().add(new NotificationParam("IDENTITY", loginEntity.getLogin()));
		notificationRequest.getParamList().add(new NotificationParam("PSWD", password));
		notificationRequest.getParamList().add(new NotificationParam("USER_ID", userId));
		final boolean sendEmailResult = mailService.sendNotification(notificationRequest);
	}
}
