package org.openiam.bpm.activiti.delegate.user;

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
import org.openiam.idm.srvc.grp.service.UserGroupDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
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
import org.openiam.idm.srvc.role.service.UserRoleDAO;
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
	
	@Autowired
	private UserRoleDAO userRoleDAO;
	
	@Autowired
	private UserGroupDAO userGroupDAO;
	
	public RejectProfileProvisionNotifierDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String lastCaller = (String)execution.getVariable(ActivitiConstants.EXECUTOR_ID);
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		final List<String> approverAssociationIds = (List<String>)execution.getVariable(ActivitiConstants.APPROVER_ASSOCIATION_IDS);
		
		final Set<String> userIds = new HashSet<String>();
        final Set<String> emails = new HashSet<String>();
		
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final NewUserProfileRequestModel profileModel = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		if(CollectionUtils.isNotEmpty(profileModel.getEmails())) {
			for(final EmailAddress address : profileModel.getEmails()) {
				if(StringUtils.isNotBlank(address.getEmailAddress())) {
					emails.add(address.getEmailAddress());
				}
			}
		}
		final List<ApproverAssociationEntity> approverAssociationList = approverAssociationDao.findByIds(approverAssociationIds);
        
        for (final ApproverAssociationEntity approverAssociation : approverAssociationList) {
        	AssociationType typeOfUserToNotify = approverAssociation.getOnRejectEntityType();
        	final String notifyId = approverAssociation.getOnRejectEntityId();
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
            		final Supervisor supVisor = profileModel.getUser().getSupervisor();
                    if (supVisor != null) {
                    	final String notifyUserId = supVisor.getSupervisor().getUserId();
                    	if(StringUtils.isNotBlank(notifyUserId)) {
                    		userIds.add(notifyUserId);
                    	}
                    }
                    break;
            	case USER:
            		userIds.add(notifyId);
            		break;
            	case TARGET_USER:
            		break;
            	default: /* send back to original requestor if none of the above */ 
            		final String notifyUserId = provisionRequest.getRequestorId();
                	if(StringUtils.isNotBlank(notifyUserId)) {
                		userIds.add(notifyUserId);
                	}
            		break;
            }
        }
        final UserEntity requestor = userManager.getUser(lastCaller);
        sendEmails(requestor, provisionRequest, profileModel.getUser(), userIds, emails);
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
