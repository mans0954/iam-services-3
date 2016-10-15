package org.openiam.bpm.activiti.delegate.user.newuser;

import java.util.HashSet;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.bpm.activiti.delegate.entitlements.AcceptEntitlementsNotifierDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.base.request.NotificationParam;
import org.openiam.base.request.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AcceptProfileProvisionDelegate extends AcceptEntitlementsNotifierDelegate {

    @Autowired
    private LoginDataService loginDS;

    public AcceptProfileProvisionDelegate() {
        super();
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.NOTIFICATION.value());
        try {
            final NewUserProfileRequestModel request = getObjectVariable(execution, ActivitiConstants.REQUEST, NewUserProfileRequestModel.class);
            final String reqeustorId = getExecutorId(execution);
            final String newUserId = getStringVariable(execution, ActivitiConstants.NEW_USER_ID);

            final UserEntity newUser = getUserEntity(newUserId);
            final UserEntity requestor = getUserEntity(reqeustorId);

			/* notify the approvers */
            final Set<String> userIds = new HashSet<String>();
            final Set<String> emails = new HashSet<String>();

            userIds.addAll(activitiHelper.getOnAcceptUserIds(execution, newUserId, getSupervisorsForUser(newUser)));
            userIds.remove(newUserId); /* don't send to target user just quite yet */

            sendEmails(execution, requestor, newUser, userIds, emails, null, null, request);

            String identity = null;
            String password = null;

            final LoginEntity login = loginDS.getPrimaryIdentity(newUserId);
            if (login != null) {
                identity = login.getLogin();
                password = loginDS.decryptPassword(login.getUserId(), login.getPassword());
            }
            if(propertyValueSweeper.getBoolean("org.openiam.send.user.activation.link")) {
                sendEmail("NEW_USER_ACTIVATION_REMIND", execution, requestor, newUser, newUser.getId(), null, identity, password, request);
            } else {
                sendEmail(execution, requestor, newUser, newUser.getId(), null, identity, password, request);
            }

            idmAuditLog.succeed();
        } catch (Throwable e) {
            idmAuditLog.setException(e);
            idmAuditLog.fail();
            throw new RuntimeException(e);
        } finally {
            addAuditLogChild(execution, idmAuditLog);
        }
    }

    private void sendEmails(final DelegateExecution execution,
                            final UserEntity requestor,
                            final UserEntity newUser,
                            final Set<String> userIds,
                            final Set<String> emailAddresses,
                            final String identity,
                            final String password,
                            final NewUserProfileRequestModel request) {
        if (CollectionUtils.isNotEmpty(userIds)) {
            for (final String userId : userIds) {
                sendEmail(execution, requestor, newUser, userId, null, identity, password, request);
            }
        }

        if (CollectionUtils.isNotEmpty(emailAddresses)) {
            for (final String email : emailAddresses) {
                sendEmail(execution, requestor, newUser, null, email, identity, password, request);
            }
        }
    }

    private void sendEmail(final DelegateExecution execution,
                           final UserEntity requestor,
                           final UserEntity newUser,
                           final String userId,
                           final String email,
                           final String identity,
                           final String password,
                           final NewUserProfileRequestModel profileRequestModel) {

        sendEmail(getNotificationType(execution), execution, requestor, newUser, userId, email, identity, password, profileRequestModel);
    }

    private void sendEmail(final String notificationType,
                           final DelegateExecution execution,
                           final UserEntity requestor,
                           final UserEntity newUser,
                           final String userId,
                           final String email,
                           final String identity,
                           final String password,
                           final NewUserProfileRequestModel profileRequestModel) {
        final NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setNotificationType(notificationType);
        request.setTo(email);
        request.getParamList().add(new NotificationParam("TARGET_REQUEST", profileRequestModel));
        request.getParamList().add(new NotificationParam("COMMENT", getComment(execution)));
        request.getParamList().add(new NotificationParam("REQUEST_REASON", getTaskDescription(execution)));
        request.getParamList().add(new NotificationParam("TARGET_USER", newUser.getDisplayName()));
        request.getParamList().add(new NotificationParam("IDENTITY", identity));
        request.getParamList().add(new NotificationParam("PSWD", password));
        if (requestor != null) {
            request.getParamList().add(new NotificationParam("REQUESTOR", requestor.getDisplayName()));
        }


        mailService.sendNotification(request);
    }
}
