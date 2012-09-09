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
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestUser;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RejectNewHireDelegate implements JavaDelegate {

	private static Logger log = Logger.getLogger(RejectNewHireDelegate.class);
	
	@Autowired
	@Qualifier("mailService")
	private MailService mailService;
	
	@Autowired
	@Qualifier("approverAssociationDao")
	private ApproverAssociationDAO approverAssociationDao;
	
	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	@Autowired
	private UserDataService userManager;
	
	@Autowired
	private LoginDataService loginDS;
	
	public static final String REQUESTING_FOR = "RequestingFor";
	public static final String PROVISION_REQUEST = "ProvisionRequest";
	public static final String APPROVER = "ApproverId";
	
	private ProvisionRequest provisionRequest;
	private ProvisionUser provisionUser;
	private String approverId;
	
	public RejectNewHireDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Rejected new hire");
		
		final Object requestingForObj = execution.getVariable(REQUESTING_FOR);
		final Object provisionRequestObj = execution.getVariable(PROVISION_REQUEST);
		final Object approverObj = execution.getVariable(APPROVER);
		
		if(requestingForObj == null || !(requestingForObj instanceof ProvisionUser)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", REQUESTING_FOR));
		}
		
		if(provisionRequestObj == null || !(provisionRequestObj instanceof ProvisionRequest)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", PROVISION_REQUEST));
		}
		
		if(approverObj == null || !(approverObj instanceof User)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", APPROVER));
		}
		
		provisionRequest = (ProvisionRequest)provisionRequestObj;
		provisionUser = (ProvisionUser)requestingForObj;
		approverId = (String)approverObj;
		
		final String requestType = provisionRequest.getRequestType();
        final List<ApproverAssociation> approverAssociationList = approverAssociationDao.findApproversByRequestType(requestType, 1);
        for (final ApproverAssociation approverAssociation : approverAssociationList) {
            String notifyEmail = null;
            String typeOfUserToNotify = approverAssociation.getRejectNotificationUserType();
            if (StringUtils.isBlank(typeOfUserToNotify)) {
                typeOfUserToNotify = "USER";
            }
            String notifyUserId = null;
            if (StringUtils.equalsIgnoreCase(typeOfUserToNotify, "user")) {
                notifyUserId = approverAssociation.getNotifyUserOnReject();
            } else {
                if (StringUtils.equalsIgnoreCase(typeOfUserToNotify, "supervisor")) {
                    final Supervisor supVisor = provisionUser.getSupervisor();
                    if (supVisor != null) {
                        notifyUserId = supVisor.getSupervisor().getUserId();
                    }
                } else {
                    if (provisionUser.getEmailAddress() != null) {
                        notifyEmail = provisionUser.getEmail();
                    }
                }
            }

            User approver = userManager.getUserWithDependent(approverId, false);

            String targetUserName = null;
            final Set<RequestUser> requestUserSet = provisionRequest.getRequestUsers();
            if (CollectionUtils.isNotEmpty(requestUserSet)) {
                final Iterator<RequestUser> userIt = requestUserSet.iterator();
                if (userIt.hasNext()) {
                    final RequestUser targetUser = userIt.next();
                    targetUserName = String.format("%s %s", targetUser.getFirstName(), targetUser.getLastName());
                }

            }

            final NotificationRequest request = new NotificationRequest();
            request.setUserId(notifyUserId);
            request.setNotificationType("REQUEST_REJECTED");
            request.setTo(notifyEmail);
            request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getRequestId()));
            request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
            request.getParamList().add(new NotificationParam("REQUESTOR", approver.getFirstName() + " " + approver.getLastName()));
            request.getParamList().add(new NotificationParam("TARGET_USER", targetUserName));
            mailService.sendNotification(request);

        }
	}

}
