package org.openiam.bpm.activiti.delegate;

import java.util.Date;
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
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.domain.RequestApproverEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.Supervisor;
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
		
		final UserEntity newUser = userManager.getUser(newUserId);
		
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final NewUserProfileRequestModel profileModel = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
        
        final String requestType = provisionRequest.getRequestType();
        final List<ApproverAssociationEntity> approverAssociationList = approverAssociationDao.findApproversByRequestType(requestType, 1);

        /* notify the approvers */
        for (final ApproverAssociationEntity approverAssociation : approverAssociationList) {
            String typeOfUserToNotify = approverAssociation.getApproveNotificationUserType();
            if (StringUtils.isBlank(typeOfUserToNotify)) {
                typeOfUserToNotify = "USER";
            }
            String notifyUserId = null;
            //String notifyEmail = null;
            if (StringUtils.equalsIgnoreCase(typeOfUserToNotify, "user")) {
                notifyUserId = approverAssociation.getNotifyUserOnApprove();
                /*
                if(StringUtils.isNotBlank(notifyUserId)) {
                	final User notifyUser = userDAO.findById(notifyUserId);
                	if(notifyUser != null && notifyUser.getEmailAddress() != null) {
                		notifyEmail = notifyUser.getEmail();
                	}
                }
                */
            } else if(StringUtils.equalsIgnoreCase(typeOfUserToNotify, "supervisor")) {
            	final List<SupervisorEntity> supervisors = userManager.getSupervisors(newUserId);
                if (CollectionUtils.isNotEmpty(supervisors)) {
                	final SupervisorEntity supervisorEntity = supervisors.get(0);
                	if(supervisorEntity != null && supervisorEntity.getSupervisor() != null) {
                		notifyUserId = supervisorEntity.getSupervisor().getUserId();
                	}
                    //notifyEmail = supVisor.getSupervisor().getEmail();
                }
            }
            
            /* if there's no approver to notify, send it to the original user */
            if(notifyUserId == null) {
            	notifyUserId = newUserId;
            }
            
            if (StringUtils.isNotBlank(notifyUserId)) {
                String identity = null;
                String password = null;

                final UserEntity approver = userManager.getUser(lastCaller);

                final LoginEntity login = loginDS.getPrimaryIdentity(newUserId);
                if (login != null) {
                    identity = login.getLogin();
                    password = loginDS.decryptPassword(login.getUserId(),login.getPassword());
                }


                final NotificationRequest request = new NotificationRequest();
                // send a message to this user
                request.setUserId(notifyUserId);
                request.setNotificationType("REQUEST_APPROVED");

                request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getId()));
                request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
                request.getParamList().add(new NotificationParam("REQUESTOR", String.format("%s %s", approver.getFirstName(), approver.getLastName())));
                request.getParamList().add(new NotificationParam("TARGET_USER", String.format("%s %s", newUser.getFirstName(), newUser.getLastName())));
                request.getParamList().add(new NotificationParam("IDENTITY", identity));
                request.getParamList().add(new NotificationParam("PSWD", password));


                mailService.sendNotification(request);
            }
        }
	}
}
