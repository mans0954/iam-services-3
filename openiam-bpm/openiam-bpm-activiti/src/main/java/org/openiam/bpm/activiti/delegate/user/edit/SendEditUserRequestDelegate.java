package org.openiam.bpm.activiti.delegate.user.edit;

import java.util.ArrayList;
import java.util.Collection;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class SendEditUserRequestDelegate extends AbstractEntitlementsDelegate {

    public SendEditUserRequestDelegate() {
        super();
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        super.execute(execution);
    }

    @Override
    protected void sendNotification(final UserEntity toNotify,
                                    final UserEntity targetUser,
                                    final DelegateExecution execution) {
        {
            final String taskName = getStringVariable(execution, ActivitiConstants.TASK_NAME);
            final String taskDescription = getStringVariable(execution, ActivitiConstants.TASK_DESCRIPTION);
            final String taskOwner = getRequestorId(execution);
            final UserEntity owner = getUserEntity(taskOwner);
            final NotificationRequest request = new NotificationRequest();
            request.setUserId(toNotify.getId());
            request.setNotificationType(getNotificationType(execution));
            UserProfileRequestModel profileModel = getObjectVariable(execution, ActivitiConstants.REQUEST, UserProfileRequestModel.class);
            request.getParamList().add(new NotificationParam("TO_NOTIFY", toNotify));
            request.getParamList().add(new NotificationParam("TARGET_USER", targetUser));
            request.getParamList().add(new NotificationParam("TARGET_REQUEST", profileModel));
            request.getParamList().add(new NotificationParam("REQUESTOR", owner));
            request.getParamList().add(new NotificationParam("COMMENT", getComment(execution)));
            request.getParamList().add(new NotificationParam("REQUEST_REASON", taskName));
            request.getParamList().add(new NotificationParam("REQUEST_DESCRIPTION", taskDescription));
            mailService.sendNotification(request);
        }
    }


    @Override
    protected ActivitiConstants getTargetVariable() {
        return ActivitiConstants.ASSOCIATION_ID;
    }
}
