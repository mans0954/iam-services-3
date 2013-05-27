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
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.request.RequestorInformation;
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

public class AcceptNewHireDelegate implements JavaDelegate {

	private static Logger log = Logger.getLogger(AcceptNewHireDelegate.class);
	
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
	
	@Autowired
	@Qualifier("userDAO")
	private UserDAO userDAO;
	
	public AcceptNewHireDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Accepted new hire");
		
		final Object provisionRequestIdObj = execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		final Object newHireExecutorIdObj = execution.getVariable(ActivitiConstants.NEW_HIRE_EXECUTOR_ID);
		if(provisionRequestIdObj == null || !(provisionRequestIdObj instanceof String)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", ActivitiConstants.PROVISION_REQUEST_ID));
		}
		if(newHireExecutorIdObj == null || !(newHireExecutorIdObj instanceof String)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", ActivitiConstants.NEW_HIRE_EXECUTOR_ID));
		}
		
		final Object newUserIdObj = execution.getVariable(ActivitiConstants.NEW_USER_ID);
		if(newUserIdObj == null || !(newUserIdObj instanceof String)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", ActivitiConstants.NEW_USER_ID));
		}
		
		final String provisionRequestId = (String)provisionRequestIdObj;
		
		final String newUserId = (String)newUserIdObj;
		final UserEntity newUser = userDAO.findById(newUserId);
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final NewUserProfileRequestModel profileModel = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		final String newHireExecutorId = (String)newHireExecutorIdObj;
        
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
               final Supervisor supVisor = profileModel.getUser().getSupervisor();
                if (supVisor != null) {
                    notifyUserId = supVisor.getSupervisor().getUserId();
                    //notifyEmail = supVisor.getSupervisor().getEmail();
                }
            }
            
            /* if there's no approver to notify, send it to the original user */
            if(notifyUserId == null) {
            	notifyUserId = newUser.getUserId();
            }
            
            if (StringUtils.isNotBlank(notifyUserId)) {
                String identity = null;
                String password = null;

                final UserEntity approver = userManager.getUser(newHireExecutorId);

                final LoginEntity login = loginDS.getPrimaryIdentity(newUser.getUserId());
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
                request.getParamList().add(new NotificationParam("TARGET_USER", String.format("%s %s", profileModel.getUser().getFirstName(), profileModel.getUser().getLastName())));
                request.getParamList().add(new NotificationParam("IDENTITY", identity));
                request.getParamList().add(new NotificationParam("PSWD", password));


                mailService.sendNotification(request);
            }
        }
	}

}
