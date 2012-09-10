package org.openiam.bpm.activiti.delegate;

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
import org.openiam.bpm.activiti.util.ActivitiConstants;
import org.openiam.bpm.request.NewHireRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestUser;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AcceptNewHireDelegate implements JavaDelegate {

	private static Logger log = Logger.getLogger(AcceptNewHireDelegate.class);
	
	@Autowired
	@Qualifier("mailService")
	private MailService mailService;
	
	@Autowired
	@Qualifier("approverAssociationDao")
	private ApproverAssociationDAO approverAssociationDao;
	
	@Autowired
	private UserDataService userManager;
	
	@Autowired
	private LoginDataService loginDS;
	
	public AcceptNewHireDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Accepted new hire");
		
		final Object newHireRequestObj = execution.getVariable(ActivitiConstants.NEW_HIRE_BPM_VAR);
		if(newHireRequestObj == null || !(newHireRequestObj instanceof NewHireRequest)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", ActivitiConstants.NEW_HIRE_BPM_VAR));
		}
		
		final Object newProvisionedUserObj = execution.getVariable(ActivitiConstants.NEW_PROVISIONED_USER);
		if(newProvisionedUserObj == null || !(newProvisionedUserObj instanceof User)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", ActivitiConstants.NEW_PROVISIONED_USER));
		}
		
		final User newProvisionedUser = (User)newProvisionedUserObj;
		final NewHireRequest newHireRequest = (NewHireRequest)newHireRequestObj;
		final ProvisionRequest provisionRequest = newHireRequest.getProvisionRequest();
		final ProvisionUser provisionUser = newHireRequest.getProvisionUser();
		final String approverId = newHireRequest.getRequestorInformation().getCallerUserId();
        
        final String requestType = provisionRequest.getRequestType();
        final List<ApproverAssociation> approverAssociationList = approverAssociationDao.findApproversByRequestType(requestType, 1);

        /* notify the approvers */
        for (final ApproverAssociation approverAssociation : approverAssociationList) {
            String typeOfUserToNotify = approverAssociation.getApproveNotificationUserType();
            if (StringUtils.isBlank(typeOfUserToNotify)) {
                typeOfUserToNotify = "USER";
            }
            String notifyUserId = null;
            if (StringUtils.equalsIgnoreCase(typeOfUserToNotify, "user")) {
                notifyUserId = approverAssociation.getNotifyUserOnApprove();
            } else {
            	if(StringUtils.equalsIgnoreCase(typeOfUserToNotify, "supervisor")) {
                   final Supervisor supVisor = provisionUser.getSupervisor();
                    if (supVisor != null) {
                        notifyUserId = supVisor.getSupervisor().getUserId();
                    }
                } else {
                    if (provisionUser.getEmailAddress() != null) {
                        notifyUserId = newProvisionedUser.getUserId();
                    }
                }
            }

            if (StringUtils.isNotBlank(notifyUserId)) {
                String identity = null;
                String password = null;

                final User approver = userManager.getUserWithDependent(approverId, false);

                // get the target user
                String targetUserName = null;
                final Set<RequestUser> reqUserSet = provisionRequest.getRequestUsers();
                if (CollectionUtils.isNotEmpty(reqUserSet)) {
                    final Iterator<RequestUser> userIt = reqUserSet.iterator();
                    if (userIt.hasNext()) {
                        final RequestUser targetUser = userIt.next();
                        targetUserName = String.format("%s %s", targetUser.getFirstName(), targetUser.getLastName());
                    }
                }

                final Login login = loginDS.getPrimaryIdentity(newProvisionedUser.getUserId());
                if (login != null) {
                    identity = login.getId().getLogin();
                    password = loginDS.decryptPassword(login.getPassword());
                }


                final NotificationRequest request = new NotificationRequest();
                // send a message to this user
                request.setUserId(notifyUserId);
                request.setNotificationType("REQUEST_APPROVED");

                request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getRequestId()));
                request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
                request.getParamList().add(new NotificationParam("REQUESTOR", approver.getFirstName() + " " + approver.getLastName()));
                request.getParamList().add(new NotificationParam("TARGET_USER", targetUserName));
                request.getParamList().add(new NotificationParam("IDENTITY", identity));
                request.getParamList().add(new NotificationParam("PSWD", password));


                mailService.sendNotification(request);
            }
        }
	}

}
