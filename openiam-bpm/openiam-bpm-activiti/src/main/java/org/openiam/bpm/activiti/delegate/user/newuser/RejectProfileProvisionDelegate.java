package org.openiam.bpm.activiti.delegate.user.newuser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.bpm.activiti.delegate.entitlements.RejectEntitlementsNotifierDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;

public class RejectProfileProvisionDelegate extends RejectEntitlementsNotifierDelegate {
	
	private static final Log LOG = LogFactory.getLog(RejectProfileProvisionDelegate.class);

     public RejectProfileProvisionDelegate() {
    	 super();
     }
     
     @Override
     public void execute(DelegateExecution execution) throws Exception {
    	final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.NOTIFICATION.value());
 		try {
	    	 final String reqeustorId = getRequestorId(execution);
	             
	    	 final Set<String> emails = new HashSet<String>();
	             
	    	 final NewUserProfileRequestModel profileModel = getObjectVariable(execution, ActivitiConstants.REQUEST, NewUserProfileRequestModel.class);
	         if(CollectionUtils.isNotEmpty(profileModel.getEmails())) {
	        	 for(final EmailAddress address : profileModel.getEmails()) {
	            	 if(StringUtils.isNotBlank(address.getEmailAddress())) {
	            		 emails.add(address.getEmailAddress());
	            	 }
	             }
	         }
	             
	         final Collection<String> userIds = activitiHelper.getOnRejectUserIds(execution, null, profileModel.getSupervisorIds());
	         
	         final UserEntity requestor = getUserEntity(reqeustorId);
	         sendEmails(profileModel, requestor, execution, profileModel.getUser(), userIds, emails);
	         idmAuditLog.succeed();
		} catch(Throwable e) {
			idmAuditLog.setException(e);
			idmAuditLog.fail();
			throw new RuntimeException(e);
		} finally {
			addAuditLogChild(execution, idmAuditLog);
		}
     }
     
     private void sendEmails(final NewUserProfileRequestModel profileModel, final UserEntity requestor, final DelegateExecution execution, final User user, final Collection<String> userIds, final Collection<String> emailAddresses) {
         if(CollectionUtils.isNotEmpty(userIds)) {
             for(final String userId : userIds) {
                     sendEmail(profileModel, requestor, execution, user, userId, null);
             }
         }
         
         if(CollectionUtils.isNotEmpty(emailAddresses)) {
             for(final String email : emailAddresses) {
                     sendEmail(profileModel, requestor, execution, user, null, email);
             }
         }
     }
     
     private void sendEmail(final NewUserProfileRequestModel profileModel, final UserEntity requestor, final DelegateExecution execution, final User user, final String userId, final String email) {
	     final NotificationRequest request = new NotificationRequest();
	     request.setUserId(userId);
	     request.setNotificationType(getNotificationType(execution));
	     request.setTo(email);
	     
	     request.getParamList().add(new NotificationParam("TARGET_REQUEST", profileModel));
	     request.getParamList().add(new NotificationParam("REQUEST_REASON", getTaskDescription(execution)));
	     request.getParamList().add(new NotificationParam("TARGET_USER", user.getDisplayName()));
	     request.getParamList().add(new NotificationParam("COMMENT", getComment(execution)));
	     if(requestor != null) {
	    	 request.getParamList().add(new NotificationParam("REQUESTOR", requestor.getDisplayName()));
	     }
	     mailService.sendNotification(request);
     }
}
