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
import org.openiam.bpm.activiti.delegate.core.AbstractNotificationDelegate;
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

public class SendNewHireRequestDelegate extends AbstractNotificationDelegate {

	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	public SendNewHireRequestDelegate() {
		SpringContextProvider.autowire(this);
	}

	private NewUserProfileRequestModel profileModel;
	private ProvisionRequestEntity provisionRequest;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		//final String callerId = (String)execution.getVariable(ActivitiConstants.TASK_OWNER);
		
		provisionRequest = provRequestService.getRequest(provisionRequestId);
		profileModel = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		
		final Collection<String> candidateUserIds = activitiHelper.getCandidateUserIds(execution, null, profileModel.getSupervisorIds());
		for(final String candidateId : candidateUserIds) {
			final UserEntity entity = getUserEntity(candidateId);
			if(entity != null) {
				sendNotificationRequest(entity);
			}
		}
	}
	
	private void sendNotificationRequest(final UserEntity user) {
		final NotificationRequest request = new NotificationRequest();
        request.setUserId(user.getUserId());
        request.setNotificationType(getNotificationType());
        request.getParamList().add(new NotificationParam("REQUEST_ID", provisionRequest.getId()));
        request.getParamList().add(new NotificationParam("REQUEST_REASON", provisionRequest.getRequestReason()));
        request.getParamList().add(new NotificationParam("REQUESTOR",  user.getDisplayName()));
        request.getParamList().add(new NotificationParam("TARGET_USER", profileModel.getUser().getDisplayName()));
        mailService.sendNotification(request);
	}

	@Override
	protected String getNotificationType() {
		return "NEW_PENDING_REQUEST";
	}
}
