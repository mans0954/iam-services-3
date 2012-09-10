package org.openiam.bpm.activiti.delegate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.UserManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.activiti.util.ActivitiConstants;
import org.openiam.bpm.request.NewHireRequest;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestApprover;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class SendNewHireRequestDelegate implements JavaDelegate {

	private static Logger log = Logger.getLogger(SendNewHireRequestDelegate.class);

	public static final String DELEGATION_FILTER_SEARCH = "DelegationFilterSearch";
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private UserDataService userManager;
	
	public SendNewHireRequestDelegate() {
		SpringContextProvider.autowire(this);
	}

	private ProvisionRequest provisionRequest;
	private User requestor;
	private ProvisionUser provisionUser;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Object newHireRequestObj = execution.getVariable(ActivitiConstants.NEW_HIRE_BPM_VAR);
		final Object delegationFilterSearchObj = execution.getVariable(ActivitiConstants.DELEGATION_FILTER_SEARCH);
		if(newHireRequestObj == null || !(newHireRequestObj instanceof NewHireRequest)) {
			throw new ActivitiException(String.format("No '%s' parameter specified, or object is not of proper type", ActivitiConstants.NEW_HIRE_BPM_VAR));
		}
		
		final NewHireRequest newHireRequest = (NewHireRequest)newHireRequestObj;
		
		provisionUser = newHireRequest.getProvisionUser();
		provisionRequest = newHireRequest.getProvisionRequest();
		final String callerId = newHireRequest.getRequestorInformation().getCallerUserId();
		
		if(CollectionUtils.isNotEmpty(provisionRequest.getRequestApprovers())) {
			if(callerId == null) {
				throw new ActivitiException("No Requestor specified for this request...");
			}
			
			requestor = userDao.findById(callerId);
			if(requestor == null) {
				throw new ActivitiException(String.format("User with requestorId '%s' does not exist", callerId));
			}
		       
			for (final RequestApprover requestApprover : provisionRequest.getRequestApprovers()) {
				if(!StringUtils.equalsIgnoreCase(requestApprover.getApproverType(), "role")) {
					sendNotification(requestApprover);
				} else {
					if(delegationFilterSearchObj != null && delegationFilterSearchObj instanceof DelegationFilterSearch) {
						final List<User> roleApprovers = userManager.searchByDelegationProperties((DelegationFilterSearch)delegationFilterSearchObj);
						if (CollectionUtils.isNotEmpty(roleApprovers)) {
							for (final User approver : roleApprovers) {
								sendNotificationRequest(approver);
							}
						}
					}
	            }
			}
		}
	}
	
	private void sendNotificationRequest(final User user) {
		final NotificationRequest request = new NotificationRequest();
        request.setUserId(user.getUserId());
        request.setNotificationType("NEW_PENDING_REQUEST");
        request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getRequestId()));
        request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
        request.getParamList().add(new NotificationParam("REQUESTOR",  String.format("%s %s",user.getFirstName(), user.getLastName())));
        request.getParamList().add(new NotificationParam("TARGET_USER", String.format("%s %s", provisionUser.getFirstName(), provisionUser.getLastName())));
        mailService.sendNotification(request);
	}
	
	private void sendNotification(final RequestApprover requestApprover) {
		final  NotificationRequest request = new NotificationRequest();
        request.setUserId(requestApprover.getApproverId());
        request.setNotificationType("NEW_PENDING_REQUEST");
        request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getRequestId()));
        request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
        request.getParamList().add(new NotificationParam("REQUESTOR", String.format("%s %s",requestor.getFirstName(), requestor.getLastName())));
        request.getParamList().add(new NotificationParam("TARGET_USER", String.format("%s %s", provisionUser.getFirstName(), provisionUser.getLastName())));
        mailService.sendNotification(request);

	}
}
