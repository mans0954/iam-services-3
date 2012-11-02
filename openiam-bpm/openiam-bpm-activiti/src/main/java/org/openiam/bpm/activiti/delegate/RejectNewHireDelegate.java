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
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.request.NewHireRequest;
import org.openiam.bpm.request.RequestorInformation;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestUser;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class RejectNewHireDelegate implements JavaDelegate {

	private static Logger log = Logger.getLogger(RejectNewHireDelegate.class);
	
	@Autowired
	@Qualifier("mailService")
	private MailService mailService;
	
	@Autowired
	@Qualifier("approverAssociationDAO")
	private ApproverAssociationDAO approverAssociationDao;
	
	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	@Autowired
	private UserDataService userManager;
	
	@Autowired
	private LoginDataService loginDS;
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	@Autowired
	@Qualifier("userDAO")
	private UserDAO userDAO;
	
	public RejectNewHireDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Rejected new hire");
		
		final Object provisionRequestIdObj = execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		final Object newHireExecutorIdObj = execution.getVariable(ActivitiConstants.NEW_HIRE_EXECUTOR_ID);
		if(provisionRequestIdObj == null || !(provisionRequestIdObj instanceof String)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", ActivitiConstants.PROVISION_REQUEST_ID));
		}
		if(newHireExecutorIdObj == null || !(newHireExecutorIdObj instanceof String)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", ActivitiConstants.NEW_HIRE_EXECUTOR_ID));
		}
		
		final String provisionRequestId = (String)provisionRequestIdObj;
		
		final ProvisionRequest provisionRequest = provRequestService.getRequest(provisionRequestId);
		final ProvisionUser provisionUser = (ProvisionUser)new XStream().fromXML(provisionRequest.getRequestXML());
		final String newHireExecutorId = (String)newHireExecutorIdObj;
		
		final String requestType = provisionRequest.getRequestType();
        final List<ApproverAssociation> approverAssociationList = approverAssociationDao.findApproversByRequestType(requestType, 1);
        for (final ApproverAssociation approverAssociation : approverAssociationList) {
            String notifyEmail = null;
            approverAssociation.getApproverUserId();
            String typeOfUserToNotify = approverAssociation.getRejectNotificationUserType();
            if (StringUtils.isBlank(typeOfUserToNotify)) {
                typeOfUserToNotify = "USER";
            }
            String notifyUserId = null;
            if (StringUtils.equalsIgnoreCase(typeOfUserToNotify, "user")) {
                notifyUserId = approverAssociation.getNotifyUserOnReject();
                if(StringUtils.isNotBlank(notifyUserId)) {
                	final UserEntity notifyUser = userDAO.findById(notifyUserId);
                	if(notifyUser != null && notifyUser.getEmailAddresses() != null) {
                		notifyEmail = notifyUser.getEmail();
                	}
                }
            } else if (StringUtils.equalsIgnoreCase(typeOfUserToNotify, "supervisor")) {
                final Supervisor supVisor = provisionUser.getSupervisor();
                if (supVisor != null) {
                    notifyUserId = supVisor.getSupervisor().getUserId();
                    notifyEmail = supVisor.getSupervisor().getEmail();
                }
            } else if(StringUtils.equalsIgnoreCase(typeOfUserToNotify, "target_user")) {
            	//notifyUserId = ? /* can't set this - user isn't created on reject, so no ID */
            	notifyEmail = provisionUser.getEmail();
            } else { /* send back to original requestor if none of the above */ 
            	notifyUserId = provisionRequest.getRequestorId();
            	if(notifyUserId != null) {
            		final UserEntity requestor = userDAO.findById(notifyUserId);
            		if(requestor != null && requestor.getEmailAddresses() != null) {
            			notifyEmail = requestor.getEmail();
            		}
            	}
            }

            final User approver = userManager.getUserWithDependent(newHireExecutorId, false);
            final NotificationRequest request = new NotificationRequest();
            request.setUserId(notifyUserId);
            request.setNotificationType("REQUEST_REJECTED");
            request.setTo(notifyEmail);
            request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getRequestId()));
            request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
            request.getParamList().add(new NotificationParam("REQUESTOR", String.format("%s %s", approver.getFirstName(), approver.getLastName())));
            request.getParamList().add(new NotificationParam("TARGET_USER", String.format("%s %s", provisionUser.getFirstName(), provisionUser.getLastName())));
            mailService.sendNotification(request);

        }
	}

}
