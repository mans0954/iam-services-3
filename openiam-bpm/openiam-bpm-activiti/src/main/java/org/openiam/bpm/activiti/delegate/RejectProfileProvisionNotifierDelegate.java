package org.openiam.bpm.activiti.delegate;

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
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.request.RequestorInformation;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
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
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class RejectProfileProvisionNotifierDelegate implements JavaDelegate {

	private static Logger log = Logger.getLogger(RejectProfileProvisionNotifierDelegate.class);
	
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
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	@Autowired
	@Qualifier("userDAO")
	private UserDAO userDAO;
	
	public RejectProfileProvisionNotifierDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String lastCaller = (String)execution.getVariable(ActivitiConstants.EXECUTOR_ID);
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final NewUserProfileRequestModel profileModel = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		
		final String requestType = provisionRequest.getRequestType();
        final List<ApproverAssociationEntity> approverAssociationList = approverAssociationDao.findApproversByRequestType(requestType, 1);
        
        
        final Set<String> userIdsToNotify = new HashSet<String>();
        final Set<String> emailsToNotify = new HashSet<String>();
        for (final ApproverAssociationEntity approverAssociation : approverAssociationList) {
            approverAssociation.getApproverUserId();
            String typeOfUserToNotify = approverAssociation.getRejectNotificationUserType();
            if (StringUtils.isBlank(typeOfUserToNotify)) {
                typeOfUserToNotify = "USER";
            }
            if (StringUtils.equalsIgnoreCase(typeOfUserToNotify, "user")) {
            	final String notifyUserId = approverAssociation.getNotifyUserOnReject();
                if(StringUtils.isNotBlank(notifyUserId)) {
                	final UserEntity notifyUser = userDAO.findById(notifyUserId);
                	if(notifyUser != null) {
                		userIdsToNotify.add(notifyUser.getUserId());
                	}
                }
            } else if (StringUtils.equalsIgnoreCase(typeOfUserToNotify, "supervisor")) {
                final Supervisor supVisor = profileModel.getUser().getSupervisor();
                if (supVisor != null) {
                	final String notifyUserId = supVisor.getSupervisor().getUserId();
                    userIdsToNotify.add(notifyUserId);
                }
            } else if(StringUtils.equalsIgnoreCase(typeOfUserToNotify, "target_user")) {
            	//notifyUserId = ? /* can't set this - user isn't created on reject, so no ID */
            	final String notifyEmail = getPrimaryEmail(profileModel.getEmails());
            	if(StringUtils.isNotBlank(notifyEmail)) {
            		emailsToNotify.add(notifyEmail);
            	}
            } else { /* send back to original requestor if none of the above */ 
            	final String notifyUserId = provisionRequest.getRequestorId();
            	if(notifyUserId != null) {
            		final UserEntity requestor = userDAO.findById(notifyUserId);
            		if(requestor != null) {
            			userIdsToNotify.add(requestor.getUserId());
            		}
            	}
            }
        }
        final UserEntity requestor = userManager.getUser(lastCaller);
        sendEmails(requestor, provisionRequest, profileModel.getUser(), userIdsToNotify, emailsToNotify);
	}
	
	private void sendEmails(final UserEntity requestor, final ProvisionRequestEntity provisionRequest, final User user, final Set<String> userIds, final Set<String> emailAddresses) {
		if(CollectionUtils.isNotEmpty(userIds)) {
			for(final String userId : userIds) {
				sendEmail(requestor, provisionRequest, user, userId, null);
			}
		}
		
		if(CollectionUtils.isNotEmpty(emailAddresses)) {
			for(final String email : emailAddresses) {
				sendEmail(requestor, provisionRequest, user, null, email);
			}
		}
	}
	
	private void sendEmail(final UserEntity requestor, final ProvisionRequestEntity provisionRequest, final User user, final String userId, final String email) {
        final NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setNotificationType("REQUEST_REJECTED");
        request.setTo(email);
        request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getId()));
        request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
        request.getParamList().add(new NotificationParam("REQUESTOR", String.format("%s %s", requestor.getFirstName(), requestor.getLastName())));
        request.getParamList().add(new NotificationParam("TARGET_USER", String.format("%s %s", user.getFirstName(), user.getLastName())));
        mailService.sendNotification(request);
	}

	private String getPrimaryEmail(final List<EmailAddress> addressSet) {
		String retVal = null;
		if(CollectionUtils.isNotEmpty(addressSet)) {
			for(final EmailAddress email : addressSet) {
				if(email.getIsDefault()) {
					retVal = email.getEmailAddress();
					break;
				}
			}
			
			if(retVal == null) {
				if(addressSet.size() >= 1) {
					retVal = addressSet.iterator().next().getEmailAddress();
				}
			}
		}
		return retVal;
	}
}
