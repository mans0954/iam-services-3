package org.openiam.bpm.activiti.delegate.user.newuser;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.grp.service.UserGroupDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.domain.RequestApproverEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.role.service.UserRoleDAO;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class AcceptProfileProvisionNotifierDelegate implements JavaDelegate {
	
	private static Logger log = Logger.getLogger(AcceptProfileProvisionNotifierDelegate.class);

	@Autowired
	@Qualifier("mailService")	
	private MailService mailService;
	
	@Autowired
	@Qualifier("approverAssociationDAO")
	private ApproverAssociationDAO approverAssociationDao;
	
	@Autowired
	private UserDataService userManager;
	
	@Autowired
	private LoginDataService loginDS;
	
	@Autowired
	private UserRoleDAO userRoleDAO;
	
	@Autowired
	private UserGroupDAO userGroupDAO;
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	public AcceptProfileProvisionNotifierDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String lastCaller = (String)execution.getVariable(ActivitiConstants.EXECUTOR_ID);
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		final String newUserId = (String)execution.getVariable(ActivitiConstants.NEW_USER_ID);
		final List<String> approverAssociationIds = (List<String>)execution.getVariable(ActivitiConstants.APPROVER_ASSOCIATION_IDS);
		
		final UserEntity newUser = userManager.getUser(newUserId);
		
        /* notify the approvers */
        final Set<String> userIds = new HashSet<String>();
        final Set<String> emails = new HashSet<String>();
        
        userIds.add(newUserId);
		
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final String requestorId = provisionRequest.getRequestorId();
        
        final List<ApproverAssociationEntity> approverAssociationList = approverAssociationDao.findByIds(approverAssociationIds);
        
        for (final ApproverAssociationEntity approverAssociation : approverAssociationList) {
        	AssociationType typeOfUserToNotify = approverAssociation.getOnApproveEntityType();
        	final String notifyId = approverAssociation.getOnApproveEntityId();
        	if(StringUtils.isNotBlank(notifyId)) {
	            if (typeOfUserToNotify == null) {
	            	typeOfUserToNotify = AssociationType.TARGET_USER;
	            }
	            
	            switch(typeOfUserToNotify) {
	            	case GROUP:
	            		final List<String> usersInGroup = userGroupDAO.getUserIdsInGroup(notifyId);
						if(CollectionUtils.isNotEmpty(usersInGroup)) {
							userIds.addAll(usersInGroup);
						}
	            		break;
	            	case ROLE:
	            		final List<String> usersInRole = userRoleDAO.getUserIdsInRole(notifyId);
						if(CollectionUtils.isNotEmpty(usersInRole)) {
							userIds.addAll(usersInRole);
						}
	            		break;
	            	case SUPERVISOR:
	            		final List<SupervisorEntity> supervisors = userManager.getSupervisors(newUserId);
	                    if (CollectionUtils.isNotEmpty(supervisors)) {
	                    	for(final SupervisorEntity supervisorEntity : supervisors) {
	                    		if(supervisorEntity != null && supervisorEntity.getSupervisor() != null) {
	                    			final String notifyUserId = supervisorEntity.getSupervisor().getUserId();
	                    			if(notifyUserId != null) {
	                    				userIds.add(notifyUserId);
	                    			}
	                    		}
	                    	}
	                    }
	            		break;
	            	case TARGET_USER:
	            		userIds.add(newUserId);
	            		break;
	            	case USER:
	            		userIds.add(notifyId);
	            		break;
	            	default:
	            		break;
	            }
        	}
        }
        
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
