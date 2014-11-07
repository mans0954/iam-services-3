package org.openiam.bpm.activiti.delegate.user.newuser;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.prov.request.domain.RequestApproverEntity;
import org.openiam.idm.srvc.prov.request.dto.RequestUser;
import org.openiam.idm.srvc.role.dto.Role;
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
			final NewUserProfileRequestModel request = getObjectVariable(execution, ActivitiConstants.REQUEST, NewUserProfileRequestModel.class); 
			final String reqeustorId = getRequestorId(execution);
			final String newUserId = getStringVariable(execution, ActivitiConstants.NEW_USER_ID);
	            
			final UserEntity newUser = getUserEntity(newUserId);
			final UserEntity requestor = getUserEntity(reqeustorId);    
			
			/* notify the approvers */
			final Set<String> userIds = new HashSet<String>();
			final Set<String> emails = new HashSet<String>();
	        
			userIds.addAll(activitiHelper.getOnAcceptUserIds(execution, newUserId, getSupervisorsForUser(newUser)));
			userIds.remove(newUserId); /* don't send to target user just quite yet */
			
			sendEmails(execution, requestor, newUser, userIds, emails, null, null, request);
			
			String identity = null;
			String password = null;
	
			final LoginEntity login = loginDS.getPrimaryIdentity(newUserId);
			if (login != null) {
	            identity = login.getLogin();
	            password = loginDS.decryptPassword(login.getUserId(),login.getPassword());
			}
			sendEmail(execution, requestor, newUser, newUser.getId(), null, identity, password, request);
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
    						final String password,
    						final NewUserProfileRequestModel request) {
            if(CollectionUtils.isNotEmpty(userIds)) {
                    for(final String userId : userIds) {
                            sendEmail(execution, requestor, newUser, userId, null, identity, password, request);
                    }
            }
            
            if(CollectionUtils.isNotEmpty(emailAddresses)) {
                    for(final String email : emailAddresses) {
                            sendEmail(execution, requestor, newUser, null, email, identity, password, request);
                    }
            }
    }
    
    private void sendEmail(final DelegateExecution execution,
    					   final UserEntity requestor, 
    					   final UserEntity newUser, 
    					   final String userId, 
    					   final String email, 
    					   final String identity, 
    					   final String password,
    					   final NewUserProfileRequestModel profileRequestModel) {
    	final NotificationRequest request = new NotificationRequest();
	    request.setUserId(userId);
	    request.setNotificationType(getNotificationType(execution));
	    request.setTo(email);
	    
	    /*
	    final List<Organization> organizations = new LinkedList<>();
	    if(CollectionUtils.isNotEmpty(profileRequestModel.getOrganizationIds())) {
	    	for(final String id : profileRequestModel.getOrganizationIds()) {
	    		final Organization entity = getOrganization(id);
	    		if(entity != null) {
	    			organizations.add(entity);
	    		}
	    	}
	    }
	    
	    final List<Role> roles = new LinkedList<>();
	    if(CollectionUtils.isNotEmpty(profileRequestModel.getRoleIds())) {
	    	for(final String id : profileRequestModel.getRoleIds()) {
	    		final Role entity = getRole(id);
	    		if(entity != null) {
	    			roles.add(entity);
	    		}
	    	}
	    }
	    
	    final List<Group> groups = new LinkedList<>();
	    if(CollectionUtils.isNotEmpty(profileRequestModel.getGroupIds())) {
	    	for(final String id : profileRequestModel.getGroupIds()) {
	    		final Group entity = getGroup(id);
	    		if(entity != null) {
	    			groups.add(entity);
	    		}
	    	}
	    }
	    
	    final List<User> supervisors = new LinkedList<>();
	    if(CollectionUtils.isNotEmpty(profileRequestModel.getSupervisorIds())) {
	    	for(final String id : profileRequestModel.getSupervisorIds()) {
	    		final User entity = getUser(id);
	    		if(entity != null) {
	    			supervisors.add(entity);
	    		}
	    	}
	    }
	   
	    request.getParamList().add(new NotificationParam("ORGANIZATIONS", organizations));
	    request.getParamList().add(new NotificationParam("ROLES", roles));
	    request.getParamList().add(new NotificationParam("GROUPS", groups));
	    request.getParamList().add(new NotificationParam("SUPERVISORS", supervisors));
	    */
	    request.getParamList().add(new NotificationParam("REQUEST_MODEL", profileRequestModel));
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
