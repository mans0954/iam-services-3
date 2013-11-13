package org.openiam.bpm.activiti.delegate.user.newuser;

import java.util.Date;
import java.util.HashSet;
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
import org.openiam.bpm.activiti.delegate.core.ActivitiHelper;
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

    @Autowired
    @Qualifier("mailService")
    private MailService mailService;
    
    @Autowired
    private LoginDataService loginDS;
    
    @Autowired
    private UserDataService userManager;
    
    @Autowired
    @Qualifier("provRequestService")
    private RequestDataService provRequestService;
    
    @Autowired
    @Qualifier("userDAO")
    private UserDAO userDAO;
    
    @Autowired
    private ActivitiHelper activitiHelper;
	
	public AcceptProfileProvisionDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
    public void execute(DelegateExecution execution) throws Exception {
		final String lastCaller = (String)execution.getVariable(ActivitiConstants.EXECUTOR_ID.getName());
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID.getName());
		final String newUserId = (String)execution.getVariable(ActivitiConstants.NEW_USER_ID.getName());
            
		final UserEntity newUser = userManager.getUser(newUserId);
            
		/* notify the approvers */
		final Set<String> userIds = new HashSet<String>();
		final Set<String> emails = new HashSet<String>();
    
		userIds.add(newUserId);
            
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		
		userIds.addAll(activitiHelper.getOnAcceptUserIds(execution, newUserId, null));
    
		/* if there's no approver to notify, send it to the original user */
		if(CollectionUtils.isEmpty(userIds)) {
            userIds.add(newUserId);
		}
    
		String identity = null;
		String password = null;

		final UserEntity approver = userManager.getUser(lastCaller);

		final LoginEntity login = loginDS.getPrimaryIdentity(newUserId);
		if (login != null) {
            identity = login.getLogin();
            password = loginDS.decryptPassword(login.getUserId(),login.getPassword());
		}
		sendEmails(approver, provisionRequest, newUser, userIds, emails, identity, password);
    }
    
    private void sendEmails(final UserEntity approver, final ProvisionRequestEntity provisionRequest, final UserEntity newUser, 
                                                    final Set<String> userIds, final Set<String> emailAddresses, final String identity, final String password) {
            if(CollectionUtils.isNotEmpty(userIds)) {
                    for(final String userId : userIds) {
                            sendEmail(approver, provisionRequest, newUser, userId, null, identity, password);
                    }
            }
            
            if(CollectionUtils.isNotEmpty(emailAddresses)) {
                    for(final String email : emailAddresses) {
                            sendEmail(approver, provisionRequest, newUser, null, email, identity, password);
                    }
            }
    }
    
    private void sendEmail(final UserEntity approver, final ProvisionRequestEntity provisionRequest, final UserEntity newUser, 
                                               final String userId, final String email, final String identity, final String password) {
            final NotificationRequest request = new NotificationRequest();
	    request.setUserId(userId);
	    request.setNotificationType("REQUEST_APPROVED");
	    request.setTo(email);
	    request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getId()));
	    request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
	    request.getParamList().add(new NotificationParam("REQUESTOR", String.format("%s %s", approver.getFirstName(), approver.getLastName())));
	    request.getParamList().add(new NotificationParam("TARGET_USER", String.format("%s %s", newUser.getFirstName(), newUser.getLastName())));
	    request.getParamList().add(new NotificationParam("IDENTITY", identity));
	    request.getParamList().add(new NotificationParam("PSWD", password));


	    mailService.sendNotification(request);
    }
}
