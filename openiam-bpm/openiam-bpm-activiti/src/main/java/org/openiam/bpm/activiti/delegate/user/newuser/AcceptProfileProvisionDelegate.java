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
import org.openiam.bpm.activiti.delegate.core.AbstractNotificationDelegate;
import org.openiam.bpm.activiti.delegate.core.ActivitiHelper;
import org.openiam.bpm.activiti.delegate.entitlements.AcceptEntitlementsNotifierDelegate;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
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

public class AcceptProfileProvisionDelegate extends AcceptEntitlementsNotifierDelegate {

    @Autowired
    private LoginDataService loginDS;
	
	public AcceptProfileProvisionDelegate() {
		super();
	}
	
	@Override
    public void execute(final DelegateExecution execution) throws Exception {
		final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.NOTIFICATION.value());
		try {
			final String reqeustorId = getRequestorId(execution);
			final String newUserId = getStringVariable(execution, ActivitiConstants.NEW_USER_ID);
	            
			final UserEntity newUser = getUserEntity(newUserId);
			final UserEntity requestor = getUserEntity(reqeustorId);    
			
			/* notify the approvers */
			final Set<String> userIds = new HashSet<String>();
			final Set<String> emails = new HashSet<String>();
	        
			userIds.addAll(activitiHelper.getOnAcceptUserIds(execution, newUserId, getSupervisorsForUser(newUser)));
			userIds.remove(newUserId); /* don't send to target user just quite yet */
			
			sendEmails(execution, requestor, newUser, userIds, emails, null, null);
			
			String identity = null;
			String password = null;
	
			final LoginEntity login = loginDS.getPrimaryIdentity(newUserId);
			if (login != null) {
	            identity = login.getLogin();
	            password = loginDS.decryptPassword(login.getUserId(),login.getPassword());
			}
			sendEmail(execution, requestor, newUser, newUser.getId(), null, identity, password);
			idmAuditLog.succeed();
		} catch(Throwable e) {
			idmAuditLog.setException(e);
			idmAuditLog.fail();
			throw new RuntimeException(e);
		} finally {
			addAuditLogChild(execution, idmAuditLog);
		}
    }
    
    private void sendEmails(final DelegateExecution execution,
    						final UserEntity requestor, 
    						final UserEntity newUser, 
    						final Set<String> userIds, 
    						final Set<String> emailAddresses, 
    						final String identity, 
    						final String password) {
            if(CollectionUtils.isNotEmpty(userIds)) {
                    for(final String userId : userIds) {
                            sendEmail(execution, requestor, newUser, userId, null, identity, password);
                    }
            }
            
            if(CollectionUtils.isNotEmpty(emailAddresses)) {
                    for(final String email : emailAddresses) {
                            sendEmail(execution, requestor, newUser, null, email, identity, password);
                    }
            }
    }
    
    private void sendEmail(final DelegateExecution execution,
    					   final UserEntity requestor, 
    					   final UserEntity newUser, 
    					   final String userId, 
    					   final String email, 
    					   final String identity, 
    					   final String password) {
    	final NotificationRequest request = new NotificationRequest();
	    request.setUserId(userId);
	    request.setNotificationType(getNotificationType(execution));
	    request.setTo(email);
	    request.getParamList().add(new NotificationParam("COMMENT", getComment(execution)));
	    request.getParamList().add(new NotificationParam("REQUEST_REASON", getTaskDescription(execution)));
	    request.getParamList().add(new NotificationParam("TARGET_USER", newUser.getDisplayName()));
	    request.getParamList().add(new NotificationParam("IDENTITY", identity));
	    request.getParamList().add(new NotificationParam("PSWD", password));
	    if(requestor != null) {
	    	request.getParamList().add(new NotificationParam("REQUESTOR", requestor.getDisplayName()));
	     }


	    mailService.sendNotification(request);
    }
}
