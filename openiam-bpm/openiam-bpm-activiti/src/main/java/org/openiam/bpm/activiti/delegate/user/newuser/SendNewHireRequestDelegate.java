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
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
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

public class SendNewHireRequestDelegate extends AbstractEntitlementsDelegate {

    public SendNewHireRequestDelegate() {
        super();
    }

    private NewUserProfileRequestModel profileModel;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        profileModel = getObjectVariable(execution, ActivitiConstants.REQUEST, NewUserProfileRequestModel.class);

        final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.NOTIFICATION.value());
		try {
	        final Collection<String> candidateUserIds = activitiHelper.getCandidateUserIds(execution, null, profileModel.getSupervisorIds());
	        for (final String candidateId : candidateUserIds) {
	            final UserEntity entity = getUserEntity(candidateId);
	            if (entity != null) {
	                sendNotificationRequest(entity, execution, idmAuditLog);
	            }
	        }
	        idmAuditLog.addAttributeAsJson(AuditAttributeName.CANDIDATE_USER_IDS, candidateUserIds, customJacksonMapper);
	        idmAuditLog.succeed();
		} catch(Throwable e) {
			idmAuditLog.setException(e);
			idmAuditLog.fail();
			throw new RuntimeException(e);
		} finally {
			addAuditLogChild(execution, idmAuditLog);
		}
    }

    private void sendNotificationRequest(final UserEntity user, final DelegateExecution execution, final IdmAuditLog idmAuditLog) {
        final NotificationRequest request = new NotificationRequest();
        request.setUserId(user.getId());
        request.setNotificationType(getNotificationType(execution));
        request.getParamList().add(new NotificationParam("TARGET_REQUEST", profileModel));
        request.getParamList().add(new NotificationParam("REQUEST_REASON", getTaskDescription(execution)));
        request.getParamList().add(new NotificationParam("REQUESTOR", user.getDisplayName()));
        request.getParamList().add(new NotificationParam("TARGET_USER", profileModel.getUser().getDisplayName()));
        request.getParamList().add(new NotificationParam("COMMENT", getComment(execution)));
        
        final IdmAuditLog child = createNewAuditLog(execution);
        child.setAction("Send Notification");
        child.setUserId(user.getId());
        child.addAttribute(AuditAttributeName.NOTIFICATION_TYPE, getNotificationType(execution));
        idmAuditLog.addChild(child);
        
        mailService.sendNotification(request);
    }
}
