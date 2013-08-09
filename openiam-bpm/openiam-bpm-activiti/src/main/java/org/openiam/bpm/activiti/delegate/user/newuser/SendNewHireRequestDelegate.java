package org.openiam.bpm.activiti.delegate.user.newuser;

import java.util.ArrayList;
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
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.domain.RequestApproverEntity;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestApprover;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class SendNewHireRequestDelegate implements JavaDelegate {

	private static Logger log = Logger.getLogger(SendNewHireRequestDelegate.class);

	public static final String DELEGATION_FILTER_SEARCH = "DelegationFilterSearch";
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private UserDataService userManager;
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	public SendNewHireRequestDelegate() {
		SpringContextProvider.autowire(this);
	}

	private NewUserProfileRequestModel profileModel;
	private ProvisionRequestEntity provisionRequest;
	private UserEntity requestor;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		final String callerId = (String)execution.getVariable(ActivitiConstants.TASK_OWNER);
		final Object candidateUserIdsObj = execution.getVariable(ActivitiConstants.CANDIDATE_USERS_IDS);
		final Collection<String> candidateUsersIds = new ArrayList<String>();
		if(candidateUserIdsObj != null) {
			if((candidateUserIdsObj instanceof Collection<?>)) {
				for(final String candidateId : (Collection<String>)candidateUserIdsObj) {
					if(candidateId != null) {
						candidateUsersIds.add(candidateId);
					}
				}
			} else if(candidateUserIdsObj instanceof String) {
				if(StringUtils.isNotBlank(((String)candidateUserIdsObj))) {
					candidateUsersIds.add(((String)candidateUserIdsObj));
				}
			}
		}
		
		provisionRequest = provRequestService.getRequest(provisionRequestId);
		profileModel = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		for(final String candidateId : candidateUsersIds) {
			final UserEntity entity = userDao.findById(candidateId);
			if(entity != null) {
				sendNotificationRequest(entity);
			}
		}
	}
	
	private void sendNotificationRequest(final UserEntity user) {
		final NotificationRequest request = new NotificationRequest();
        request.setUserId(user.getUserId());
        request.setNotificationType("NEW_PENDING_REQUEST");
        request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getId()));
        request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
        request.getParamList().add(new NotificationParam("REQUESTOR",  user.getDisplayName()));
        request.getParamList().add(new NotificationParam("TARGET_USER", profileModel.getUser().getDisplayName()));
        mailService.sendNotification(request);
	}
}
